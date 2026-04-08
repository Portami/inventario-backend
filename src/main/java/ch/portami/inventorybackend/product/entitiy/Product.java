package ch.portami.inventorybackend.product.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_type_id", nullable = false)
    private ProductType productType;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductVariant> productVariants = new ArrayList<>();

    public Product() {}

    public Product(ProductType productType) {
        this.productType = productType;
    }

    public Long getId() { return id; }

    public ProductType getProductType() { return productType; }
    public void setProductType(ProductType productType) { this.productType = productType; }

    public List<ProductAttribute> getProductAttributes() { return productAttributes; }
    public void setProductAttributes(List<ProductAttribute> productAttributes) { this.productAttributes = productAttributes; }

    public List<ProductVariant> getProductVariants() { return productVariants; }
    public void setProductVariants(List<ProductVariant> productVariants) { this.productVariants = productVariants; }
}