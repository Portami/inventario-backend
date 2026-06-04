package ch.portami.inventorybackend.stocktake.felt.entity;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "felt_stocktake_roll_or_scrap")
public class FeltStocktakeRollOrScrap {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "item_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeltStocktakeItem stocktakeItem;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private Type type;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "roll_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private FeltRoll roll;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "scrap_id", nullable = true)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private ScrapPiece scrap;

    @ManyToOne(fetch = FetchType.EAGER, optional = true)
    @JoinColumn(name = "expected_storage_id", nullable = true)
    private Storage expectedStorage;

    @Column(name = "length", nullable = false)
    private Double length;

    @Column(name = "width", nullable = false)
    private Double width;

    @Column(name = "color", nullable = false)
    private String color;

    @Column(name = "thickness", nullable = false)
    private Double thickness;

    @Column(name = "density", nullable = false)
    private Double density;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "article_number", nullable = false)
    private String articleNumber;

    @Column(name = "felt_type_name", nullable = false)
    private String feltTypeName;

    @Column(name = "supplier_name", nullable = false)
    private String supplierName;

    protected FeltStocktakeRollOrScrap() {
    }

    public FeltStocktakeRollOrScrap(FeltStocktakeItem stocktakeItem, @Nullable Storage expectedStorage, Double length,
            Double width,
            String color, Double thickness, Double density, BigDecimal price, String articleNumber, String feltTypeName,
            String supplierName, FeltRoll roll) {
        this.stocktakeItem = stocktakeItem;
        this.expectedStorage = expectedStorage;
        this.length = length;
        this.width = width;
        this.color = color;
        this.thickness = thickness;
        this.density = density;
        this.price = price;
        this.articleNumber = articleNumber;
        this.feltTypeName = feltTypeName;
        this.supplierName = supplierName;

        this.type = Type.ROLL;
        this.roll = roll;
    }

    public FeltStocktakeRollOrScrap(FeltStocktakeItem stocktakeItem, @Nullable Storage expectedStorage, Double length,
            Double width,
            String color, Double thickness, Double density, BigDecimal price, String articleNumber, String feltTypeName,
            String supplierName, ScrapPiece scrap) {
        this.stocktakeItem = stocktakeItem;
        this.expectedStorage = expectedStorage;
        this.length = length;
        this.width = width;
        this.color = color;
        this.thickness = thickness;
        this.density = density;
        this.price = price;
        this.articleNumber = articleNumber;
        this.feltTypeName = feltTypeName;
        this.supplierName = supplierName;

        this.type = Type.SCRAP;
        this.scrap = scrap;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FeltStocktakeItem getStocktakeItem() {
        return stocktakeItem;
    }

    public @Nullable Storage getExpectedStorage() {
        return expectedStorage;
    }

    public Type getType() {
        return type;
    }

    public @Nullable FeltRoll getRoll() {
        return roll;
    }

    public @Nullable ScrapPiece getScrap() {
        return scrap;
    }

    public Double getLength() {
        return length;
    }

    public Double getWidth() {
        return width;
    }

    public String getColor() {
        return color;
    }

    public Double getThickness() {
        return thickness;
    }

    public Double getDensity() {
        return density;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public String getFeltTypeName() {
        return feltTypeName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeltStocktakeRollOrScrap that)) {
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
        return "FeltStocktakeRollOrScrap{id=" + id + ", articleNumber='" + articleNumber + "', color='" + color + "'}";
    }

    public enum Type {
        ROLL, SCRAP
    }

}
