package ch.portami.inventorybackend.stocktake.felt.entity;

import ch.portami.inventorybackend.core.storage.entity.Storage;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "felt_stocktake_item")
public class FeltStocktakeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "barcode", nullable = true)
    private String barcode;

    @OneToOne(mappedBy = "stocktakeItem", fetch = FetchType.EAGER, optional = true)
    private FeltStocktakeRollOrScrap rollOrScrap;

    @Column(name = "problem_acknowledged", nullable = false)
    private Boolean problemAcknowledged = false;

    @Column(name = "mutation_wanted", nullable = false)
    private Boolean mutationWanted = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "expected_storage_id", nullable = true)
    private Storage newStorage;

    @Column(name = "mutation_applied", nullable = false)
    private Boolean mutationApplied = false;

    @OneToMany(mappedBy = "stocktakeItem", fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private final List<FeltStocktakeScan> scans = new ArrayList<>();

    protected FeltStocktakeItem() {
    }

    public FeltStocktakeItem(String barcode, @Nullable FeltStocktakeRollOrScrap rollOrScrap) {
        this.barcode = barcode;
        this.rollOrScrap = rollOrScrap;
    }

    public Long getId() {
        return id;
    }

    public @Nullable String getBarcode() {
        return barcode;
    }

    public @Nullable FeltStocktakeRollOrScrap getRollOrScrap() {
        return rollOrScrap;
    }

    public @Nullable Storage getNewStorage() {
        return newStorage;
    }

    public Boolean isProblemAcknowledged() {
        return problemAcknowledged;
    }

    public void setProblemAcknowledged(Boolean problemAcknowledged) {
        this.problemAcknowledged = problemAcknowledged;
    }

    public Boolean isMutationWanted() {
        return mutationWanted;
    }

    public void setMutationWanted(Boolean mutationWanted) {
        this.mutationWanted = mutationWanted;
    }

    public void setNewStorage(@Nullable Storage newStorage) {
        this.newStorage = newStorage;
    }

    public Boolean isMutationApplied() {
        return mutationApplied;
    }

    public void setMutationApplied(Boolean mutationApplied) {
        this.mutationApplied = mutationApplied;
    }

    public List<FeltStocktakeScan> getScans() {
        return scans;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeltStocktakeItem that)) {
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
        return "FeltStocktakeItem{id=" + id + ", barcode='" + barcode + "', rollOrScrap=" + rollOrScrap + "}";
    }

}
