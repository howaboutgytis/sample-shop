package gt.shop.sample.order;

import java.util.HashMap;
import java.util.Map;

/**
 * Order POST request, where products are sent in a <code>Map<<b>productId, quantity</b>></code>
 */
public class OrderRequest {

    private Map<Integer, Integer> order = new HashMap<>();

    public Map<Integer, Integer> getOrder() {
        return order;
    }

    public OrderRequest setOrder(Map<Integer, Integer> order) {
        this.order = order;
        return this;
    }
}
