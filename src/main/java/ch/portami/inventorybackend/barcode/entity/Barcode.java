package ch.portami.inventorybackend.barcode.entity;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "barcode")
public class Barcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BarcodeType type;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "felt_roll_id", unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeltRoll feltRoll;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrap_piece_id", unique = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ScrapPiece scrapPiece;

    public Barcode() {
    }

    private Barcode(BarcodeType type, FeltRoll feltRoll, ScrapPiece scrapPiece) {
        this.type = type;
        this.feltRoll = feltRoll;
        this.scrapPiece = scrapPiece;
    }

    public static Barcode forRoll(FeltRoll feltRoll) {
        return new Barcode(BarcodeType.ROLL, feltRoll, null);
    }

    public static Barcode forScrap(ScrapPiece scrapPiece) {
        return new Barcode(BarcodeType.SCRAP, null, scrapPiece);
    }

    public Long getId() {
        return id;
    }

    public BarcodeType getType() {
        return type;
    }

    public FeltRoll getFeltRoll() {
        return feltRoll;
    }

    public ScrapPiece getScrapPiece() {
        return scrapPiece;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Barcode that)) {
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
        return "Barcode{id=" + id + ", type=" + type + "}";
    }
}
