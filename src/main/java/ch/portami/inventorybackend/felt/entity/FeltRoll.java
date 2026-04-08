package ch.portami.inventorybackend.felt.entity;

import ch.portami.inventorybackend.shared.entity.Storage;
import jakarta.persistence.*;

@Entity
@Table(name = "felt_roll")
public class FeltRoll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "felt_roll_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_felt_color_variant_id", nullable = false)
    private FeltColorVariant feltColorVariant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_batch_id")
    private Batch batch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_storage_id")
    private Storage storage;

    @Column(nullable = false)
    private Double length;

    @Column(nullable = false)
    private Double width;

    // TBD: mainroll identifier — placeholder for PORTAMI-19
    @Column(name = "is_main_roll", nullable = false)
    private boolean isMainRoll = false;

    public FeltRoll() {}

    public FeltRoll(FeltColorVariant feltColorVariant, Batch batch, Storage storage, Double length, Double width, boolean isMainRoll) {
        this.feltColorVariant = feltColorVariant;
        this.batch = batch;
        this.storage = storage;
        this.length = length;
        this.width = width;
        this.isMainRoll = isMainRoll;
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

    public boolean isMainRoll() { return isMainRoll; }
    public void setMainRoll(boolean isMainRoll) { this.isMainRoll = isMainRoll; }
}