package ch.portami.inventorybackend.stocktake.felt.entity;

import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "felt_stocktake_scan")
public class FeltStocktakeScan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "stocktake_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeltStocktake stocktake;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "stocktake_item_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeltStocktakeItem stocktakeItem;

    @Column(name = "barcode", nullable = false)
    private String barcode;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "scanned_storage_id", nullable = false)
    private Storage scannedStorage;

    @Column(name = "voided", nullable = false)
    private Boolean voided = false;

    @Column(name = "corrected", nullable = false)
    private Boolean corrected = false;

    @Column(name = "scanned_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Instant scannedAt;

    protected FeltStocktakeScan() {
    }

    public FeltStocktakeScan(FeltStocktake stocktake, FeltStocktakeItem stocktakeItem, String barcode,
            Storage scannedStorage) {
        this.stocktake = stocktake;
        this.stocktakeItem = stocktakeItem;
        this.barcode = barcode;
        this.scannedStorage = scannedStorage;
    }

    public Long getId() {
        return id;
    }

    public FeltStocktake getStocktake() {
        return stocktake;
    }

    public FeltStocktakeItem getStocktakeItem() {
        return stocktakeItem;
    }

    public String getBarcode() {
        return barcode;
    }

    public Storage getScannedStorage() {
        return scannedStorage;
    }

    public @Nonnull Boolean isVoided() {
        return voided;
    }

    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    public @Nonnull Boolean isCorrected() {
        return corrected;
    }

    public void setCorrected(Boolean corrected) {
        this.corrected = corrected;
    }

    public Instant getScannedAt() {
        return scannedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeltStocktakeScan that)) {
            return false;
        }
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FeltStocktakeScan{id=" + id + ", barcode='" + barcode + "', scannedAt=" + scannedAt + "}";
    }

}
