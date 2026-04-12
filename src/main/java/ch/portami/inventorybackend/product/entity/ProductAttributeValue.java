package ch.portami.inventorybackend.product.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(
        name = "product_attribute_value",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_pav_variant_attribute",
                columnNames = {"product_variant_id", "product_attribute_id"}
        )
)
public class ProductAttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_attribute_id", nullable = false)
    private ProductAttribute productAttribute;

    @Column(nullable = false)
    private String value;

    public ProductAttributeValue() {}

    public ProductAttributeValue(ProductVariant productVariant, ProductAttribute productAttribute, String value) {
        this.productVariant = productVariant;
        this.productAttribute = productAttribute;
        this.value = value;
    }

    public Long getId() { return id; }

    public ProductVariant getProductVariant() { return productVariant; }
    public void setProductVariant(ProductVariant productVariant) { this.productVariant = productVariant; }

    public ProductAttribute getProductAttribute() { return productAttribute; }
    public void setProductAttribute(ProductAttribute productAttribute) { this.productAttribute = productAttribute; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductAttributeValue that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProductAttributeValue{id=" + id + ", value='" + value + "'}";
    }
}