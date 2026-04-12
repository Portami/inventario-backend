package ch.portami.inventorybackend.product.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "product_attribute")
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "productAttribute", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ProductAttributeValue> productAttributeValues = new ArrayList<>();

    public ProductAttribute() {}

    public ProductAttribute(Product product, String name) {
        this.product = product;
        this.name = name;
    }

    public Long getId() { return id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<ProductAttributeValue> getProductAttributeValues() { return productAttributeValues; }

    // --- Sync helpers ---------------------------------------------------

    public void addProductAttributeValue(ProductAttributeValue value) {
        productAttributeValues.add(value);
        value.setProductAttribute(this);
    }

    public void removeProductAttributeValue(ProductAttributeValue value) {
        productAttributeValues.remove(value);
        value.setProductAttribute(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductAttribute that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProductAttribute{id=" + id + ", name='" + name + "'}";
    }
}