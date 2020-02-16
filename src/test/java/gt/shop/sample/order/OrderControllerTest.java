package gt.shop.sample.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import gt.shop.sample.product.Product;
import gt.shop.sample.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
class OrderControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private OrderRepository mockedOrderRepository;
    @MockBean
    private OrderService mockedOrderService;
    @MockBean
    private ProductRepository mockedProductRepository;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)).build();
        Order mockOrder = createMockOrder();
        when(mockedOrderService.save(any())).thenReturn(mockOrder);

        when(mockedProductRepository.findById(1)).thenReturn(Optional.of(new Product()));

        List<Order> mockedReturnedOrders = new ArrayList<>();
        Order anotherOrder = new Order();
        anotherOrder.getOrderedProducts()
                .add(new OrderedProduct(new BigDecimal("1.1"), new Product().setName("mocked returned name"), 2));
        mockedReturnedOrders.add(mockOrder);
        mockedReturnedOrders.add(anotherOrder);
        when(mockedOrderService.findOrderBetween(any(), any())).thenReturn(mockedReturnedOrders);

    }

    @Test
    void shouldAcceptAndReturnOrderJson() throws Exception {
        OrderRequest request = new OrderRequest().setBuyersEmail("email@of.buyer");
        Map<Integer, Integer> order = new HashMap<>();
        order.put(1, 3);
        request.setOrder(order);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"createdAt\":\"2019-11-29T10:03:42Z\"," +
                        "\"updatedAt\":null,\"orderedProducts\":[{\"id\":3,\"createdAt\":\"2011-11-11T22:23:24Z\"," +
                        "\"updatedAt\":null,\"price\":2.2,\"product\":{\"id\":2,\"createdAt\":\"2001-12-12T01:02:33Z\"," +
                        "\"updatedAt\":null,\"name\":\"mocked and saved name\",\"price\":321.123,\"currencyCode\":\"EUR\"}," +
                        "\"quantity\":3}],\"buyerEmail\":\"email@of.buyer\",\"totalPrice\":6.6}"))
                .andDo(document("orders-post"));
    }

    @Test
    void shouldParseISODateStringsToInstants() throws Exception {
        mockMvc.perform(get("/orders")
                .param("from", "2002-02-22T14:13:22Z")
                .param("to", "2003-03-23T23:33:43Z"))
                .andExpect(status().isOk())
                .andDo(document("orders-get", requestParameters(
                        parameterWithName("from").optional().description("Interval from in ISO 8601"),
                        parameterWithName("to").optional().description("Interval to in ISO 8601"))));
    }

    @Test
    void whenParamsAreMissing_shouldParseISODateStringsToInstants() throws Exception {
        mockMvc.perform(get("/orders")
                .param("from", "2002-02-22T14:13:22Z"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/orders")
                .param("to", "2002-02-22T14:13:22Z"))
                .andExpect(status().isOk());
    }

    private Order createMockOrder() {
        Order mockOrder = new Order("email@of.buyer");
        mockOrder.setId(1);
        mockOrder.setCreatedAt(Instant.parse("2019-11-29T10:03:42Z"));
        OrderedProduct orderedProduct = new OrderedProduct(new BigDecimal("2.2"), createMockProduct(), 3);
        orderedProduct.setId(3);
        orderedProduct.setCreatedAt(Instant.parse("2011-11-11T22:23:24Z"));
        mockOrder.getOrderedProducts().add(orderedProduct);
        return mockOrder;
    }

    private Product createMockProduct() {
        Product product = new Product("mocked and saved name", BigDecimal.valueOf(321.123), "EUR");
        product.setId(2);
        product.setCreatedAt(Instant.parse("2001-12-12T01:02:33Z"));
        return product;
    }
}
