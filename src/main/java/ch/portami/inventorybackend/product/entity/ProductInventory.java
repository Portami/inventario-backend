package ch.portami.inventorybackend.product.entity;

import ch.portami.inventorybackend.core.entity.Storage;
import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(
        name = "product_inventory",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_inventory_variant_storage",
                columnNames = {"product_variant_id", "storage_id"}
        )
)
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "storage_id", nullable = false)
    private Storage storage;

    @Column(nullable = false)
    private Integer count;

    public ProductInventory() {}

    public ProductInventory(ProductVariant productVariant, Storage storage, Integer count) {
        this.productVariant = productVariant;
        this.storage = storage;
        this.count = count;
    }

    public Long getId() { return id; }

    public ProductVariant getProductVariant() { return productVariant; }
    public void setProductVariant(ProductVariant productVariant) { this.productVariant = productVariant; }

    public Storage getStorage() { return storage; }
    public void setStorage(Storage storage) { this.storage = storage; }

    public Integer getCount() { return count; }
    public void setCount(Integer count) { this.count = count; }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductInventory that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ProductInventory{id=" + id + ", count=" + count + "}";
    }
}