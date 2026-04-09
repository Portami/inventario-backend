package ch.portami.inventorybackend.product.entity;

import ch.portami.inventorybackend.shared.entity.Storage;
import jakarta.persistence.*;

@Entity
@Table(name = "product_inventory")
public class ProductInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_inventory_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_variant_id", nullable = false)
    private ProductVariant productVariant;

    @ManyToOne(fetch = FetchType.LAZY)
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
}