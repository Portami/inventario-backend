package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "felt_color_variant")
public class FeltColorVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "felt_variant_id", nullable = false)
    private FeltVariant feltVariant;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String supplierColor;

    @OneToMany(mappedBy = "feltColorVariant", fetch = FetchType.LAZY)
    private List<ScrapPiece> scrapPieces = new ArrayList<>();

    @OneToMany(mappedBy = "feltColorVariant", fetch = FetchType.LAZY)
    private List<FeltRoll> feltRolls = new ArrayList<>();

    public FeltColorVariant() {}

    public FeltColorVariant(FeltVariant feltVariant, String color) {
        this.feltVariant = feltVariant;
        this.color = color;
    }

    public Long getId() { return id; }

    public FeltVariant getFeltVariant() { return feltVariant; }
    public void setFeltVariant(FeltVariant feltVariant) { this.feltVariant = feltVariant; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getSupplierColor() {
        return supplierColor;
    }

    public void setSupplierColor(String supplierColor) {
        this.supplierColor = supplierColor;
    }

    public List<ScrapPiece> getScrapPieces() { return scrapPieces; }
    public List<FeltRoll> getFeltRolls() { return feltRolls; }

    // --- Sync helpers ---------------------------------------------------

    public void addScrapPiece(ScrapPiece piece) {
        scrapPieces.add(piece);
        piece.setFeltColorVariant(this);
    }

    public void removeScrapPiece(ScrapPiece piece) {
        scrapPieces.remove(piece);
        piece.setFeltColorVariant(null);
    }

    public void addFeltRoll(FeltRoll roll) {
        feltRolls.add(roll);
        roll.setFeltColorVariant(this);
    }

    public void removeFeltRoll(FeltRoll roll) {
        feltRolls.remove(roll);
        roll.setFeltColorVariant(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeltColorVariant that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FeltColorVariant{id=" + id + ", color='" + color + "'}";
    }
}