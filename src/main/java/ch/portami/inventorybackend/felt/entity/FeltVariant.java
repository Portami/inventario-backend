package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "felt_variant")
public class FeltVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "felt_variant_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "felt_id", nullable = false)
    private Felt felt;

    @Column(nullable = false)
    private Double thickness;

    @Column(nullable = false)
    private Double density;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @OneToMany(mappedBy = "feltVariant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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
    public void setFeltColorVariants(List<FeltColorVariant> feltColorVariants) { this.feltColorVariants = feltColorVariants; }
}