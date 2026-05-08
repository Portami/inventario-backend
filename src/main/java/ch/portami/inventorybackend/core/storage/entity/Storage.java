package ch.portami.inventorybackend.core.storage.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.product.entity.ProductInventory;

@Entity
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // No cascade: Storage is a location reference; inventory outlives a storage record.
    @OneToMany(mappedBy = "storage", fetch = FetchType.LAZY)
    private final List<ScrapPiece> scrapPieces = new ArrayList<>();

    @OneToMany(mappedBy = "storage", fetch = FetchType.LAZY)
    private final List<FeltRoll> feltRolls = new ArrayList<>();

    @OneToMany(mappedBy = "storage", fetch = FetchType.LAZY)
    private final List<ProductInventory> productInventories = new ArrayList<>();

    public Storage() {}

    public Storage(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<ScrapPiece> getScrapPieces() { return scrapPieces; }
    public List<FeltRoll> getFeltRolls() { return feltRolls; }
    public List<ProductInventory> getProductInventories() { return productInventories; }

    // --- Sync helpers ---------------------------------------------------

    public void addScrapPiece(ScrapPiece piece) {
        scrapPieces.add(piece);
        piece.setStorage(this);
    }

    public void removeScrapPiece(ScrapPiece piece) {
        scrapPieces.remove(piece);
        piece.setStorage(null);
    }

    public void addFeltRoll(FeltRoll roll) {
        feltRolls.add(roll);
        roll.setStorage(this);
    }

    public void removeFeltRoll(FeltRoll roll) {
        feltRolls.remove(roll);
        roll.setStorage(null);
    }

    public void addProductInventory(ProductInventory inventory) {
        productInventories.add(inventory);
        inventory.setStorage(this);
    }

    public void removeProductInventory(ProductInventory inventory) {
        productInventories.remove(inventory);
        inventory.setStorage(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Storage that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Storage{id=" + id + ", name='" + name + "'}";
    }
}