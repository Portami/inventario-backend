package ch.portami.inventorybackend.felt.entity;

import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
    @JoinColumn(name = "felt_id", nullable = false)
    private Felt felt;

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

    public ScrapPiece(Felt felt, Batch batch, Storage storage, Double length, Double width) {
        this.felt = felt;
        this.batch = batch;
        this.storage = storage;
        this.length = length;
        this.width = width;
    }

    public Long getId() { return id; }

    public Felt getFelt() { return felt; }
    public void setFelt(Felt felt) { this.felt = felt; }

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