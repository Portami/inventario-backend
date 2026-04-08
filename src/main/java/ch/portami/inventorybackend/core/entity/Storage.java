package ch.portami.inventorybackend.shared.entity;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.product.entity.ProductInventory;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "storage")
public class Storage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "storage_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "storage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ScrapPiece> scrapPieces = new ArrayList<>();

    @OneToMany(mappedBy = "storage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeltRoll> feltRolls = new ArrayList<>();

    @OneToMany(mappedBy = "storage", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductInventory> productInventories = new ArrayList<>();

    public Storage() {}

    public Storage(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<ScrapPiece> getScrapPieces() { return scrapPieces; }
    public void setScrapPieces(List<ScrapPiece> scrapPieces) { this.scrapPieces = scrapPieces; }

    public List<FeltRoll> getFeltRolls() { return feltRolls; }
    public void setFeltRolls(List<FeltRoll> feltRolls) { this.feltRolls = feltRolls; }

    public List<ProductInventory> getProductInventories() { return productInventories; }
    public void setProductInventories(List<ProductInventory> productInventories) { this.productInventories = productInventories; }
}