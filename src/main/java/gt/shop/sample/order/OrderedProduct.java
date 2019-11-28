package gt.shop.sample.order;

import gt.shop.sample.common.IdentityInfo;
import gt.shop.sample.product.Product;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import java.math.BigDecimal;

@Entity
public class OrderedProduct extends IdentityInfo {

    private BigDecimal price;

    @OneToOne(cascade = CascadeType.ALL)
    private Product product;

    private Integer quantity;

    public OrderedProduct() { }

    public OrderedProduct(BigDecimal price, Product product, Integer quantity) {
        this.price = price;
        this.product = product;
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public OrderedProduct setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public Product getProduct() {
        return product;
    }

    public OrderedProduct setProduct(Product product) {
        this.product = product;
        return this;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public OrderedProduct setQuantity(Integer quantity) {
        this.quantity = quantity;
        return this;
    }
}
