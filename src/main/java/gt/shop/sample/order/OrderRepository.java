package gt.shop.sample.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    @Query(value = "SELECT o FROM Order o WHERE o.createdAt >= ?1 AND o.createdAt <= ?2")
    List<Order> findByCreatedAtBetween(Instant from, Instant to);
}
