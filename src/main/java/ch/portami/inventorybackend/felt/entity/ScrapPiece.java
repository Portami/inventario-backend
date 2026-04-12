package ch.portami.inventorybackend.felt.entity;

import ch.portami.inventorybackend.core.entity.Storage;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import java.util.Objects;

@Entity
@Table(name = "scrap_piece")
public class ScrapPiece {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "felt_color_variant_id", nullable = false)
    private FeltColorVariant feltColorVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "storage_id")
    private Storage storage;

    @DecimalMin("44.0")
    @Column(nullable = false)
    private Double length;

    @DecimalMin("44.0")
    @Column(nullable = false)
    private Double width;

    public ScrapPiece() {}

    public ScrapPiece(FeltColorVariant feltColorVariant, Batch batch, Storage storage, Double length, Double width) {
        this.feltColorVariant = feltColorVariant;
        this.batch = batch;
        this.storage = storage;
        this.length = length;
        this.width = width;
    }

    public Long getId() { return id; }

    public FeltColorVariant getFeltColorVariant() { return feltColorVariant; }
    public void setFeltColorVariant(FeltColorVariant feltColorVariant) { this.feltColorVariant = feltColorVariant; }

    public Batch getBatch() { return batch; }
    public void setBatch(Batch batch) { this.batch = batch; }

    public Storage getStorage() { return storage; }
    public void setStorage(Storage storage) { this.storage = storage; }

    public Double getLength() { return length; }
    public void setLength(Double length) { this.length = length; }

    public Double getWidth() { return width; }
    public void setWidth(Double width) { this.width = width; }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ScrapPiece that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "ScrapPiece{id=" + id + ", length=" + length + ", width=" + width + "}";
    }
}