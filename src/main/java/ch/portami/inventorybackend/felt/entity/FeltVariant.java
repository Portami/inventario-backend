package ch.portami.inventorybackend.felt.entity;

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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "felt_variant")
public class FeltVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "felt_id", nullable = false)
    private Felt felt;

    @Column(nullable = false)
    private Double thickness;

    @Column(nullable = false)
    private Double density;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    // True aggregate: FeltVariant owns its color variants.
    @OneToMany(mappedBy = "feltVariant", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FeltColorVariant> feltColorVariants = new ArrayList<>();

    public FeltVariant() {}

    public FeltVariant(Felt felt, Double thickness, Double density, BigDecimal price) {
        this.felt = felt;
        this.thickness = thickness;
        this.density = density;
        this.price = price;
    }

    public Long getId() { return id; }

    public Felt getFelt() { return felt; }
    public void setFelt(Felt felt) { this.felt = felt; }

    public Double getThickness() { return thickness; }
    public void setThickness(Double thickness) { this.thickness = thickness; }

    public Double getDensity() { return density; }
    public void setDensity(Double density) { this.density = density; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public List<FeltColorVariant> getFeltColorVariants() { return feltColorVariants; }

    // --- Sync helpers ---------------------------------------------------

    public void addFeltColorVariant(FeltColorVariant colorVariant) {
        feltColorVariants.add(colorVariant);
        colorVariant.setFeltVariant(this);
    }

    public void removeFeltColorVariant(FeltColorVariant colorVariant) {
        feltColorVariants.remove(colorVariant);
        colorVariant.setFeltVariant(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FeltVariant that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "FeltVariant{id=" + id + ", thickness=" + thickness + ", density=" + density + ", price=" + price + "}";
    }
}