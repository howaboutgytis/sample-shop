package gt.shop.sample.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * Database operations for {@link Order orders}
 */
@Repository
interface OrderRepository extends JpaRepository<Order, Integer> {

    /**
     * Finds orders between 2 given dates.
     * @param from beginning of interval
     * @param to end of interval
     * @return list of {@link Order orders}
     */
    @Query(value = "SELECT o FROM Order o WHERE o.createdAt >= ?1 AND o.createdAt <= ?2")
    List<Order> findByCreatedAtBetween(Instant from, Instant to);
}
