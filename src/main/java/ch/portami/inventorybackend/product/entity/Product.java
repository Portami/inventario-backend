package ch.portami.inventorybackend.product.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<ProductVariant> productVariants = new ArrayList<>();

    public Product() {}

    public Product(Category category) {
        this.category = category;
    }

    public Long getId() { return id; }

    // Fixed: was getProductType/setProductType — the field is `category`.
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public List<ProductAttribute> getProductAttributes() { return productAttributes; }

    public List<ProductVariant> getProductVariants() { return productVariants; }

    // --- Sync helpers ---------------------------------------------------

    public void addProductAttribute(ProductAttribute attribute) {
        productAttributes.add(attribute);
        attribute.setProduct(this);
    }

    public void removeProductAttribute(ProductAttribute attribute) {
        productAttributes.remove(attribute);
        attribute.setProduct(null);
    }

    public void addProductVariant(ProductVariant variant) {
        productVariants.add(variant);
        variant.setProduct(this);
    }

    public void removeProductVariant(ProductVariant variant) {
        productVariants.remove(variant);
        variant.setProduct(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Product that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Product{id=" + id + "}";
    }
}