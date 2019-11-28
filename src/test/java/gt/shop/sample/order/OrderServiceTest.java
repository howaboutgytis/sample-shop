package gt.shop.sample.order;

import gt.shop.sample.product.Product;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepository;

    @BeforeEach
    void init() {
        List<Order> mockedReturnedOrders = new ArrayList<>();
        Order mockedSavedOrder = new Order();
        mockedSavedOrder.getOrderedProducts()
                .add(new OrderedProduct(new BigDecimal("2.2"), new Product().setName("mocked and saved name"), 3));
        mockedReturnedOrders.add(mockedSavedOrder);
        when(orderRepository.findByCreatedAtBetween(any(), any())).thenReturn(mockedReturnedOrders);
    }

    @Test
    void whenToIsMissing_shouldCorrectIntervalsByMonth() {
        Instant from = Instant.parse("2019-09-19T09:10:11Z");
        Instant to = LocalDateTime.ofInstant(from, ZoneOffset.UTC).plusMonths(1).toInstant(ZoneOffset.UTC);

        orderService.findOrderBetween(from, null);

        ArgumentCaptor<Instant> instantArgumentCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(orderRepository).findByCreatedAtBetween(instantArgumentCaptor.capture(), instantArgumentCaptor.capture());

        List<Instant> capturedInstants = instantArgumentCaptor.getAllValues();
        Assertions.assertEquals(from, capturedInstants.get(0));
        Assertions.assertEquals(to, capturedInstants.get(1));
    }

    @Test
    void whenFromIsMissing_shouldCorrectIntervalsByMonth() {
        Instant to = Instant.parse("2019-09-19T09:10:11Z");
        Instant from = LocalDateTime.ofInstant(to, ZoneOffset.UTC).minusMonths(1).toInstant(ZoneOffset.UTC);

        orderService.findOrderBetween(null, to);

        ArgumentCaptor<Instant> instantArgumentCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(orderRepository).findByCreatedAtBetween(instantArgumentCaptor.capture(), instantArgumentCaptor.capture());

        List<Instant> capturedInstants = instantArgumentCaptor.getAllValues();
        Assertions.assertEquals(from, capturedInstants.get(0));
        Assertions.assertEquals(to, capturedInstants.get(1));
    }

    @Test
    void whenFromAndToIsMissing_shouldCorrectIntervalsByMonth() {
        Instant to = Instant.now();
        Instant from = LocalDateTime.ofInstant(to, ZoneOffset.UTC).minusMonths(1).toInstant(ZoneOffset.UTC);

        orderService.findOrderBetween(null, null);

        ArgumentCaptor<Instant> instantArgumentCaptor = ArgumentCaptor.forClass(Instant.class);
        verify(orderRepository).findByCreatedAtBetween(instantArgumentCaptor.capture(), instantArgumentCaptor.capture());

        List<Instant> capturedInstants = instantArgumentCaptor.getAllValues();
        Assertions.assertEquals(from, capturedInstants.get(0));
        Assertions.assertEquals(to, capturedInstants.get(1));
    }
}
