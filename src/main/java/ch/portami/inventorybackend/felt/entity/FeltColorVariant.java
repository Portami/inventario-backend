package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "felt_color_variant")
public class FeltColorVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "felt_color_variant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_felt_variant_id", nullable = false)
    private FeltVariant feltVariant;

    @Column(nullable = false)
    private String color;

    @OneToMany(mappedBy = "feltColorVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ScrapPiece> scrapPieces = new ArrayList<>();

    @OneToMany(mappedBy = "feltColorVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeltRoll> feltRolls = new ArrayList<>();

    public FeltColorVariant() {}

    public FeltColorVariant(FeltVariant feltVariant, String color) {
        this.feltVariant = feltVariant;
        this.color = color;
    }

    public Long getId() { return id; }

    public FeltVariant getFeltVariant() { return feltVariant; }
    public void setFeltVariant(FeltVariant feltVariant) { this.feltVariant = feltVariant; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public List<ScrapPiece> getScrapPieces() { return scrapPieces; }
    public void setScrapPieces(List<ScrapPiece> scrapPieces) { this.scrapPieces = scrapPieces; }

    public List<FeltRoll> getFeltRolls() { return feltRolls; }
    public void setFeltRolls(List<FeltRoll> feltRolls) { this.feltRolls = feltRolls; }
}