package gt.shop.sample.order;

import gt.shop.sample.product.Product;
import gt.shop.sample.product.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;

    private Integer savedOrderId = 0;

    @BeforeEach
    void initMocks() {
        Order mockedOrder = new Order("buyer@aaa.bbb");
        mockedOrder.getOrderedProducts()
                .add(new OrderedProduct(new BigDecimal("2.2"), new Product().setName("mocked first product"), 3));
        Order anotherOrder = new Order("buyer@aaa.bbb");
        anotherOrder.getOrderedProducts()
                .add(new OrderedProduct(new BigDecimal("1.1"), new Product().setName("second product"), 2));
        mockedOrder = orderRepository.save(mockedOrder);
        savedOrderId = mockedOrder.getId();
        orderRepository.save(anotherOrder);
    }

    @Test
    void shouldFindByValidInstantIntervals() {
        Assertions.assertEquals(2, orderRepository.findAll().size());
        Instant almostOneMonthBack = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
                .minusMonths(1).plusDays(1).toInstant(ZoneOffset.UTC);
        List<Order> result = orderRepository.findByCreatedAtBetween(almostOneMonthBack, Instant.now());
        Assertions.assertEquals(2, result.size());
    }

    @Test
    void shouldNotFindByInvalidIntervals() {
        Instant moreThanMonthPast = LocalDateTime.ofInstant(Instant.now(), ZoneOffset.UTC)
                .minusMonths(1).minusDays(2).toInstant(ZoneOffset.UTC);
        Instant moreThanMonthPastPlusOneMonth = LocalDateTime.ofInstant(moreThanMonthPast, ZoneOffset.UTC)
                .plusMonths(1).toInstant(ZoneOffset.UTC);
        Assertions.assertEquals(0, orderRepository.findByCreatedAtBetween(moreThanMonthPast, moreThanMonthPastPlusOneMonth).size());
    }

    @Test
    void shouldCalculateTotalOrderPriceIndependentFromProduct() {
        Optional<Order> order = orderRepository.findById(savedOrderId);
        Assertions.assertEquals(BigDecimal.valueOf(6.6), order.get().getTotalPrice());

        Optional<Product> productInsideAnOrder = productRepository.findByName("mocked first product");
        productInsideAnOrder.get().setPrice(BigDecimal.ONE);
        productRepository.save(productInsideAnOrder.get());

        order = orderRepository.findById(savedOrderId);
        Assertions.assertEquals(BigDecimal.valueOf(6.6), order.get().getTotalPrice());
    }
}
