package ch.portami.inventorybackend.stocktake.felt.entity;

import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "felt_stocktake_storage")
@IdClass(FeltStocktakeStorageId.class)
public class FeltStocktakeStorage {

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "felt_stocktake_id", nullable = false)
    private FeltStocktake stocktake;

    @Id
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "storage_id", nullable = false)
    private Storage storage;

    @Column(name = "closed", nullable = false)
    private Boolean closed = false;

    protected FeltStocktakeStorage() {
    }

    public FeltStocktakeStorage(FeltStocktake stocktake, Storage storage) {
        this.stocktake = stocktake;
        this.storage = storage;
    }

    public FeltStocktake getStocktake() {
        return stocktake;
    }

    public Storage getStorage() {
        return storage;
    }

    public @Nonnull Boolean isClosed() {
        return closed;
    }

    public void setClosed(Boolean closed) {
        this.closed = closed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeltStocktakeStorage that)) {
            return false;
        }
        return Objects.equals(stocktake, that.stocktake) && Objects.equals(storage, that.storage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stocktake, storage);
    }
}

