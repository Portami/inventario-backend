package ch.portami.inventorybackend.restocking.entity;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "supply")
public class Supply {
    @Id
    private Long id;

    @Column(name = "is_low_on_stock")
    private boolean isLowOnSupply;

    @Column(name = "is_reordered")
    private boolean hasBeenReordered;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private FeltRoll feltRoll;

    public Supply(boolean isLowOnSupply, boolean hasBeenReordered, FeltRoll feltRoll) {
        this.isLowOnSupply = isLowOnSupply;
        this.hasBeenReordered = hasBeenReordered;
        this.feltRoll = feltRoll;
    }

    public Long getId() {
        return id;
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

    public FeltRoll getFeltRoll() {
        return feltRoll;
    }

    public void setFeltRoll(FeltRoll feltRoll) {
        this.feltRoll = feltRoll;
    }
}
