package ch.portami.inventorybackend.product.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_variant")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_variant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductAttributeValue> productAttributeValues = new ArrayList<>();

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductInventory> productInventories = new ArrayList<>();

    public ProductVariant() {}

    public ProductVariant(Product product, BigDecimal price) {
        this.product = product;
        this.price = price;
    }

    public Long getId() { return id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public List<ProductAttributeValue> getProductAttributeValues() { return productAttributeValues; }
    public void setProductAttributeValues(List<ProductAttributeValue> values) { this.productAttributeValues = values; }

    public List<ProductInventory> getProductInventories() { return productInventories; }
    public void setProductInventories(List<ProductInventory> productInventories) { this.productInventories = productInventories; }
}