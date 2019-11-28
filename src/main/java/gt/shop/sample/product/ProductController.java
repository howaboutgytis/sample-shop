package gt.shop.sample.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    private ProductRepository productRepository;

    @Autowired
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @PostMapping("/products")
    public Product createNewProduct(@RequestBody Product newProduct) {
        return productRepository.save(newProduct);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@RequestBody Product updatedProduct, @PathVariable Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(updatedProduct.getName());
                    product.setPrice(updatedProduct.getPrice());
                    product.setCurrencyCode(updatedProduct.getCurrencyCode());
                    return productRepository.save(product);
                })
                .orElseGet(() -> {
                    updatedProduct.setId(id);
                    return productRepository.save(updatedProduct);
                });
    }
}
