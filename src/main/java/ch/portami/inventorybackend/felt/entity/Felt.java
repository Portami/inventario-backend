package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "felt")
public class Felt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "felt_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "felt_type_id", nullable = false)
    private FeltType feltType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;

    @Column(name = "article_number", nullable = false)
    private String articleNumber;

    @OneToMany(mappedBy = "felt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
    public void setFeltVariants(List<FeltVariant> feltVariants) { this.feltVariants = feltVariants; }
}