package gt.shop.sample.order;

import gt.shop.sample.common.IdentityInfo;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * Explicit table name is given because the word <code>ORDER</code> is reserved.
 * <p>Note: In JPA queries you should use <b>Order</b> when referencing this <i>object</i>.</p>
 */
@Entity
@Table(name = "orders")
class Order extends IdentityInfo {

    public Order() { }

    public Order(@NotNull String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    @OneToMany(cascade = CascadeType.ALL)
    private Set<OrderedProduct> orderedProducts = new HashSet<>();

    @NotNull
    @Email
    private String buyerEmail;

    /**
     * Total sum of whole order. Product prices are saved with the order and calculations are made with those prices,
     * not the current prices of the products.
     * @return total sum of order
     */
    @Transient
    public BigDecimal getTotalPrice() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderedProduct product : orderedProducts) {
            if (product.getPrice().compareTo(BigDecimal.ZERO) > 0) {
                result = result.add(product.getPrice().multiply(new BigDecimal(product.getQuantity())));
            }
        }
        return result;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public Order setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
        return this;
    }

    public Set<OrderedProduct> getOrderedProducts() {
        return orderedProducts;
    }

    public Order setOrderedProducts(Set<OrderedProduct> orderedProducts) {
        this.orderedProducts = orderedProducts;
        return this;
    }
}
