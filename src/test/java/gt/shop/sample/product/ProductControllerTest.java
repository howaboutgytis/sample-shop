package gt.shop.sample.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(RestDocumentationExtension.class)
class ProductControllerTest {

    private MockMvc mockMvc;

    @MockBean
    private ProductRepository mockedProductRepository;

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)).build();
        List<Product> mockedProducts = new ArrayList<>();
        mockedProducts.add(createMockedProduct(1));
        mockedProducts.add(createMockedProduct(2));
        mockedProducts.add(createMockedProduct(3));
        mockedProducts.get(2).setUpdatedAt(Instant.parse("2018-08-28T17:18:19Z"));

        when(mockedProductRepository.save(new Product("", BigDecimal.ONE, "EUR"))).thenReturn(mockedProducts.get(0));
        when(mockedProductRepository.save(mockedProducts.get(2))).thenReturn(mockedProducts.get(2));

        when(mockedProductRepository.findById(1)).thenReturn(Optional.of(mockedProducts.get(0)));
        when(mockedProductRepository.findById(2)).thenThrow(IllegalArgumentException.class);
        when(mockedProductRepository.findById(3)).thenReturn(Optional.of(mockedProducts.get(2)));

        when(mockedProductRepository.findAll()).thenReturn(mockedProducts);
    }

    @Test
    void shouldFindAllProducts() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"id\":1,\"createdAt\":\"2001-01-12T01:02:33Z\",\"updatedAt\":null," +
                        "\"name\":\"mocked name 1\",\"price\":1,\"currencyCode\":\"EUR\"},{\"id\":2,\"createdAt\":" +
                        "\"2001-02-12T01:02:33Z\",\"updatedAt\":null,\"name\":\"mocked name 2\",\"price\":2,\"currencyCode\":" +
                        "\"EUR\"},{\"id\":3,\"createdAt\":\"2001-03-12T01:02:33Z\",\"updatedAt\":\"2018-08-28T17:18:19Z\",\"name\":\"mocked name 3\"," +
                        "\"price\":3,\"currencyCode\":\"EUR\"}]", true))
                .andDo(document("products-get-all"));
    }

    @Test
    void shouldFindProductById() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/products/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"createdAt\":\"2001-01-12T01:02:33Z\",\"updatedAt\":null," +
                        "\"name\":\"mocked name 1\",\"price\":1,\"currencyCode\":\"EUR\"}", true))
                .andDo(document("products-get-by-id", pathParameters(
                        parameterWithName("id").description("Product id")
                )));
    }

    @Test
    void shouldThrowWhenProductNotFound() throws Exception {
        mockMvc.perform(RestDocumentationRequestBuilders.get("/products/{id}", 2))
                .andExpect(status().is5xxServerError())
                .andDo(document("products-get-by-id", pathParameters(
                        parameterWithName("id").description("Product id")
                )))
                .andDo(print());
    }

    @Test
    void shouldCreateNewProduct() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(new Product("mocked name 1", BigDecimal.ONE, "EUR"));

        mockMvc.perform(RestDocumentationRequestBuilders.post("/products")
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"createdAt\":\"2001-01-12T01:02:33Z\",\"updatedAt\":null," +
                        "\"name\":\"mocked name 1\",\"price\":1,\"currencyCode\":\"EUR\"}"))
                .andDo(document("products-post"))
                .andDo(print());
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        String requestJson = ow.writeValueAsString(new Product("mocked name 1", BigDecimal.ONE, "EUR"));

        mockMvc.perform(RestDocumentationRequestBuilders.put("/products/{id}", 3)
                .contentType(MediaType.APPLICATION_JSON).content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":3,\"createdAt\":\"2001-03-12T01:02:33Z\",\"updatedAt\":\"2018-08-28T17:18:19Z\"," +
                        "\"name\":\"mocked name 1\",\"price\":1,\"currencyCode\":\"EUR\"}", true))
                .andDo(document("products-put"))
                .andDo(print());
    }

    private Product createMockedProduct(int mockId) {
        Product product = new Product("mocked name " + mockId, BigDecimal.valueOf(mockId + mockId / 10), "EUR");
        product.setId(mockId);
        product.setCreatedAt(Instant.parse("2001-0" + mockId + "-12T01:02:33Z"));
        return product;
    }
}
