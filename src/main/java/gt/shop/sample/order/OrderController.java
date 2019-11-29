package gt.shop.sample.order;

import gt.shop.sample.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
class OrderController {

    private ProductRepository productRepository;
    private OrderService orderService;

    @Autowired
    public OrderController(ProductRepository productRepository, OrderService orderService) {
        this.productRepository = productRepository;
        this.orderService = orderService;
    }

    @GetMapping("/orders")
    public List<Order> getOrdersFromTimeInterval(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return orderService.findOrderBetween(from, to);
    }

    @PostMapping("/orders")
    public Order placeNewOrder(@RequestBody OrderRequest orderRequest) {
        Order result = null;

        if (!orderRequest.getOrder().isEmpty()) {
            Order order = new Order(orderRequest.getBuyersEmail());
            orderRequest.getOrder().forEach((productId, quantity) -> productRepository.findById(productId)
                    .ifPresent(product -> order.getOrderedProducts()
                            .add(new OrderedProduct(product.getPrice(), product, quantity))));
            result = orderService.save(order);
        }

        return result;
    }
}
