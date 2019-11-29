package gt.shop.sample.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Service for orders business logic.
 */
@Service
class OrderService {

    private OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * If any of the date parameters is <code>null</code> - defaults to 1 month length periods.
     * <p>If both are <code>null</code> - defaults to 1 month from now.</p>
     *
     * @param from gets orders from this date
     * @param to gets orders until this date
     * @return orders in a specified time interval
     */
    List<Order> findOrderBetween(Instant from, Instant to) {
        if (from == null && to == null) {
            to = Instant.now();
            from = LocalDateTime.ofInstant(to, ZoneOffset.UTC).minusMonths(1).toInstant(ZoneOffset.UTC);
        } else if (from == null) {
            from = LocalDateTime.ofInstant(to, ZoneOffset.UTC).minusMonths(1).toInstant(ZoneOffset.UTC);
        } else if (to == null) {
            to = LocalDateTime.ofInstant(from, ZoneOffset.UTC).plusMonths(1).toInstant(ZoneOffset.UTC);
        }

        return orderRepository.findByCreatedAtBetween(from, to);
    }

    Order save(Order order) {
        return orderRepository.save(order);
    }
}
