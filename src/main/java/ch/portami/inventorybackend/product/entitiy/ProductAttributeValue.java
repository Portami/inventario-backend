package ch.portami.inventorybackend.product.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_attribute_value")
public class ProductAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_attribute_value_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_variant_id", nullable = false)
    private ProductVariant productVariant;

    // Denormalized FK — kept for query convenience; also derivable via productAttribute.getProduct()
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_attribute_id", nullable = false)
    private ProductAttribute productAttribute;

    @Column(nullable = false)
    private String value;

    public ProductAttributeValue() {}

    public ProductAttributeValue(ProductVariant productVariant, Product product, ProductAttribute productAttribute, String value) {
        this.productVariant = productVariant;
        this.product = product;
        this.productAttribute = productAttribute;
        this.value = value;
    }

    public Long getId() { return id; }

    public ProductVariant getProductVariant() { return productVariant; }
    public void setProductVariant(ProductVariant productVariant) { this.productVariant = productVariant; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public ProductAttribute getProductAttribute() { return productAttribute; }
    public void setProductAttribute(ProductAttribute productAttribute) { this.productAttribute = productAttribute; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}