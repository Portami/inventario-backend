package ch.portami.inventorybackend.restocking.entity;

import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "supply")
public class Supply {

    @Id
    private Long id;

    @Column(name = "is_low_on_supply")
    private boolean isLowOnSupply;

    @Column(name = "is_reordered")
    private boolean hasBeenReordered;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private FeltColorVariant feltColorVariant;

    protected Supply() {
    }

    public Supply(boolean isLowOnSupply, boolean hasBeenReordered, FeltColorVariant feltColorVariant) {
        this.isLowOnSupply = isLowOnSupply;
        this.hasBeenReordered = hasBeenReordered;
        this.feltColorVariant = feltColorVariant;
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

    public FeltColorVariant getFeltColorVariant() {
        return feltColorVariant;
    }

    public void setFeltColorVariant(FeltColorVariant feltColorVariant) {
        this.feltColorVariant = feltColorVariant;
    }
}
