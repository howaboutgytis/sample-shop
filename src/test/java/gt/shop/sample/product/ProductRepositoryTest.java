package gt.shop.sample.product;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Order(1)
    void shouldSaveAndRead() {
        Optional<Product> product = productRepository.findByName("test product");
        Assertions.assertTrue(product.isEmpty());

        productRepository.saveAndFlush(new Product().setName("test product"));

        product = productRepository.findByName("test product");
        Assertions.assertNotNull(product.get().getCreatedAt());
        Assertions.assertEquals("test product", product.get().getName());
    }

    @Test
    @Order(2)
    void shouldDelete() {
        Optional<Product> product = productRepository.findByName("test product");
        Assertions.assertTrue(product.isEmpty());

        productRepository.save(new Product().setName("test product"));

        product = productRepository.findByName("test product");
        Assertions.assertEquals("test product", product.get().getName());

        productRepository.deleteById(product.get().getId());

        product = productRepository.findByName("test product");
        Assertions.assertTrue(product.isEmpty());
    }

    @Test
    @Order(3)
    void shouldUpdate() {
        productRepository.save(new Product().setName("test update product"));

        Optional<Product> product = productRepository.findByName("test update product");
        Assertions.assertEquals("test update product", product.get().getName());

        productRepository.save(product.get().setName("updated"));
        Assertions.assertEquals("updated", product.get().getName());
    }

}
