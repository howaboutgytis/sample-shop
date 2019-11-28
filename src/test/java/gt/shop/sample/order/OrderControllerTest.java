package gt.shop.sample.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import gt.shop.sample.product.Product;
import gt.shop.sample.product.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderRepository mockedOrderRepository;
    @MockBean
    private OrderService mockedOrderService;
    @MockBean
    private ProductRepository mockedProductRepository;

    @BeforeEach
    void initMocks() {
        Order mockedSavedOrder = new Order();
        mockedSavedOrder.getOrderedProducts()
                .add(new OrderedProduct(new BigDecimal("2.2"), new Product().setName("mocked and saved name"), 3));

        when(mockedOrderService.save(any())).thenReturn(mockedSavedOrder);

        when(mockedProductRepository.findById(1)).thenReturn(Optional.of(new Product()));

        List<Order> mockedReturnedOrders = new ArrayList<>();
        Order anotherOrder = new Order();
        anotherOrder.getOrderedProducts()
                .add(new OrderedProduct(new BigDecimal("1.1"), new Product().setName("mocked returned name"), 2));
        mockedReturnedOrders.add(mockedSavedOrder);
        mockedReturnedOrders.add(anotherOrder);
        when(mockedOrderService.findOrderBetween(any(), any())).thenReturn(mockedReturnedOrders);
    }

    @Test
    void shouldAcceptAndReturnOrderJson() throws Exception {
        OrderRequest request = new OrderRequest();
        Map<Integer, Integer> order = new HashMap<>();
        order.put(1, 2);
        request.setOrder(order);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(request);

        mockMvc.perform(post("/orders").contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().json("{\"id\":null,\"createdAt\":null,\"orderedProducts\":" +
                        "[{\"id\":null,\"createdAt\":null,\"price\":2.2,\"product\":{\"id\":null,\"createdAt\":null," +
                        "\"name\":\"mocked and saved name\",\"price\":null,\"currencyCode\":null},\"quantity\":3}]}"));
    }

    @Test
    void shouldParseISODateStringsToInstants() throws Exception {
        mockMvc.perform(get("/orders")
                .param("from", "2002-02-22T14:13:22Z")
                .param("to", "2003-03-23T23:33:43Z"))
                .andExpect(status().isOk());
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
}
