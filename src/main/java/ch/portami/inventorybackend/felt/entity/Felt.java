package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "felt")
public class Felt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "felt_type_id", nullable = false)
    private FeltType feltType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "article_number", nullable = false)
    private String articleNumber;

    // True aggregate: Felt owns its variants.
    @OneToMany(mappedBy = "felt", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FeltVariant> feltVariants = new ArrayList<>();

    public Felt() {}

    public Felt(FeltType feltType, Supplier supplier, String articleNumber) {
        this.feltType = feltType;
        this.supplier = supplier;
        this.articleNumber = articleNumber;
    }

    public Long getId() { return id; }

    public FeltType getFeltType() { return feltType; }
    public void setFeltType(FeltType feltType) { this.feltType = feltType; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public String getArticleNumber() { return articleNumber; }
    public void setArticleNumber(String articleNumber) { this.articleNumber = articleNumber; }

    public List<FeltVariant> getFeltVariants() { return feltVariants; }

    // --- Sync helpers ---------------------------------------------------

    public void addFeltVariant(FeltVariant variant) {
        feltVariants.add(variant);
        variant.setFelt(this);
    }

    public void removeFeltVariant(FeltVariant variant) {
        feltVariants.remove(variant);
        variant.setFelt(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Felt that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Felt{id=" + id + ", articleNumber='" + articleNumber + "'}";
    }
}