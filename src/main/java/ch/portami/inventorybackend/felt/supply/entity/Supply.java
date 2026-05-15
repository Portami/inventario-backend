package ch.portami.inventorybackend.felt.supply.entity;

import ch.portami.inventorybackend.felt.entity.Felt;
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
    private Felt felt;

    protected Supply() {
    }

    public Supply(boolean isLowOnSupply, boolean hasBeenReordered, Felt felt) {
        this.isLowOnSupply = isLowOnSupply;
        this.hasBeenReordered = hasBeenReordered;
        this.felt = felt;
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

    public Felt getFelt() {
        return felt;
    }

    public void setFelt(Felt felt) {
        this.felt = felt;
    }
}
