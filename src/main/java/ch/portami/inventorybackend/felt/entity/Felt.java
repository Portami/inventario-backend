package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.math.BigDecimal;
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

    @Column(nullable = false)
    private Double thickness;

    @Column(nullable = false)
    private Double density;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String supplierColor;

    @Column(name = "is_low_on_supply", nullable = false, columnDefinition = "boolean default false")
    private boolean isLowOnSupply;

    @Column(name = "is_reordered", nullable = false, columnDefinition = "boolean default false")
    private boolean hasBeenReordered;

    @OneToMany(mappedBy = "felt", fetch = FetchType.LAZY)
    private final List<ScrapPiece> scrapPieces = new ArrayList<>();

    @OneToMany(mappedBy = "felt", fetch = FetchType.LAZY)
    private final List<FeltRoll> feltRolls = new ArrayList<>();

    public Felt() {
    }

    public Felt(FeltType feltType, Supplier supplier, String articleNumber,
            Double thickness, Double density, BigDecimal price,
            String color, String supplierColor) {
        this.feltType = feltType;
        this.supplier = supplier;
        this.articleNumber = articleNumber;
        this.thickness = thickness;
        this.density = density;
        this.price = price;
        this.color = color;
        this.supplierColor = supplierColor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FeltType getFeltType() {
        return feltType;
    }

    public void setFeltType(FeltType feltType) {
        this.feltType = feltType;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public Double getThickness() {
        return thickness;
    }

    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    public Double getDensity() {
        return density;
    }

    public void setDensity(Double density) {
        this.density = density;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSupplierColor() {
        return supplierColor;
    }

    public void setSupplierColor(String supplierColor) {
        this.supplierColor = supplierColor;
    }

    public boolean isLowOnSupply() {
        return isLowOnSupply;
    }

    public void setLowOnSupply(boolean lowOnSupply) {
        isLowOnSupply = lowOnSupply;
    }

    public boolean isHasBeenReordered() {
        return hasBeenReordered;
    }

    public void setHasBeenReordered(boolean hasBeenReordered) {
        this.hasBeenReordered = hasBeenReordered;
    }

    public List<ScrapPiece> getScrapPieces() {
        return scrapPieces;
    }

    public List<FeltRoll> getFeltRolls() {
        return feltRolls;
    }

    // --- Sync helpers ---------------------------------------------------

    public void addScrapPiece(ScrapPiece piece) {
        scrapPieces.add(piece);
        piece.setFelt(this);
    }

    public void removeScrapPiece(ScrapPiece piece) {
        scrapPieces.remove(piece);
        piece.setFelt(null);
    }

    public void addFeltRoll(FeltRoll roll) {
        feltRolls.add(roll);
        roll.setFelt(this);
    }

    public void removeFeltRoll(FeltRoll roll) {
        feltRolls.remove(roll);
        roll.setFelt(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Felt that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Felt{id=" + id + "'}";
    }
}