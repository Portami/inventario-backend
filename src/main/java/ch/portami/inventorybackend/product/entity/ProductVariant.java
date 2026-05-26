package ch.portami.inventorybackend.product.entity;

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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Entity
@Table(name = "product_variant")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<ProductAttributeValue> productAttributeValues = new ArrayList<>();

    @OneToMany(mappedBy = "productVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private final List<ProductInventory> productInventories = new ArrayList<>();

    public ProductVariant() {
    }

    public ProductVariant(Product product, String name, BigDecimal price) {
        this.product = product;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public List<ProductAttributeValue> getProductAttributeValues() {
        return productAttributeValues;
    }

    public Optional<ProductAttributeValue> getProductAttributeValueByAttributeId(Long attributeId) {
        return productAttributeValues.stream()
                                     .filter(av -> attributeId.equals(av.getProductAttribute()
                                                                        .getId()))
                                     .findFirst();
    }

    public List<ProductInventory> getProductInventories() {
        return productInventories;
    }

    // --- Sync helpers ---------------------------------------------------

    public void addProductAttributeValue(ProductAttributeValue value) {
        productAttributeValues.add(value);
        value.setProductVariant(this);
    }

    public void removeProductAttributeValue(ProductAttributeValue value) {
        productAttributeValues.remove(value);
        value.setProductVariant(null);
    }

    public void addProductInventory(ProductInventory inventory) {
        productInventories.add(inventory);
        inventory.setProductVariant(this);
    }

    public void removeProductInventory(ProductInventory inventory) {
        productInventories.remove(inventory);
        inventory.setProductVariant(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductVariant that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProductVariant{id=" + id + ", name='" + name + "', price=" + price + "}";
    }
}