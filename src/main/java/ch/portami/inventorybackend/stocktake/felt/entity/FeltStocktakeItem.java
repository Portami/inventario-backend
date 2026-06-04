package ch.portami.inventorybackend.stocktake.felt.entity;

import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.CascadeType;
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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "felt_stocktake_item")
public class FeltStocktakeItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "barcode", nullable = true)
    private String barcode;

    @OneToOne(mappedBy = "stocktakeItem", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true, optional = true)
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

    @OneToMany(mappedBy = "stocktakeItem", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 100)
    private final List<FeltStocktakeScan> scans = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stocktake_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeltStocktake stocktake;

    @Column(name = "resolution_comment", nullable = true)
    private String resolutionComment;

    protected FeltStocktakeItem() {
    }

    public FeltStocktakeItem(FeltStocktake stocktake) {
        this.stocktake = stocktake;
    }

    public FeltStocktakeItem(FeltStocktake stocktake, String barcode) {
        this.stocktake = stocktake;
        this.barcode = barcode;
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

    public void setRollOrScrap(@Nullable FeltStocktakeRollOrScrap rollOrScrap) {
        this.rollOrScrap = rollOrScrap;
    }

    public @Nullable Storage getNewStorage() {
        return newStorage;
    }

    public @Nonnull Boolean isProblemAcknowledged() {
        return problemAcknowledged;
    }

    public void setProblemAcknowledged(Boolean problemAcknowledged) {
        this.problemAcknowledged = problemAcknowledged;
    }

    public @Nonnull Boolean isMutationWanted() {
        return mutationWanted;
    }

    public void setMutationWanted(Boolean mutationWanted) {
        this.mutationWanted = mutationWanted;
    }

    public void setNewStorage(@Nullable Storage newStorage) {
        this.newStorage = newStorage;
    }

    public @Nonnull Boolean isMutationApplied() {
        return mutationApplied;
    }

    public void setMutationApplied(Boolean mutationApplied) {
        this.mutationApplied = mutationApplied;
    }

    public List<FeltStocktakeScan> getScans() {
        return scans;
    }

    public void addScan(FeltStocktakeScan scan) {
        scans.add(scan);
    }

    public void removeScan(FeltStocktakeScan scan) {
        scans.remove(scan);
    }

    public FeltStocktake getStocktake() {
        return stocktake;
    }

    public @Nullable String getResolutionComment() {
        return resolutionComment;
    }

    public void setResolutionComment(@Nullable String resolutionComment) {
        this.resolutionComment = resolutionComment;
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