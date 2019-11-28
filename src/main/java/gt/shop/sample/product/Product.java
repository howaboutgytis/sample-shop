package gt.shop.sample.product;

import gt.shop.sample.common.IdentityInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
public class Product extends IdentityInfo {

    private String name;

    private BigDecimal price;

    @Column(name = "currency_code")
    private String currencyCode;

    public String getName() {
        return name;
    }

    public Product setName(String name) {
        this.name = name;
        return this;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Product setPrice(BigDecimal price) {
        this.price = price;
        return this;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Product setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
        return this;
    }

}
