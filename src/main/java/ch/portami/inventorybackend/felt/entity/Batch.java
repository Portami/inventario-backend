package ch.portami.inventorybackend.felt.entity;

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

@Entity
@Table(name = "batch")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    // No cascade: a Batch is a lot identifier. Deleting it must not delete
    // physical inventory (scrap pieces / rolls) that still exists.
    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY)
    private final List<ScrapPiece> scrapPieces = new ArrayList<>();

    @OneToMany(mappedBy = "batch", fetch = FetchType.LAZY)
    private final List<FeltRoll> feltRolls = new ArrayList<>();

    public Batch() {}

    public Batch(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String batchName) { this.name = batchName; }

    public List<ScrapPiece> getScrapPieces() { return scrapPieces; }
    public List<FeltRoll> getFeltRolls() { return feltRolls; }

    // --- Sync helpers ---------------------------------------------------

    public void addScrapPiece(ScrapPiece piece) {
        scrapPieces.add(piece);
        piece.setBatch(this);
    }

    public void removeScrapPiece(ScrapPiece piece) {
        scrapPieces.remove(piece);
        piece.setBatch(null);
    }

    public void addFeltRoll(FeltRoll roll) {
        feltRolls.add(roll);
        roll.setBatch(this);
    }

    public void removeFeltRoll(FeltRoll roll) {
        feltRolls.remove(roll);
        roll.setBatch(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Batch that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Batch{id=" + id + ", name='" + name + "'}";
    }
}