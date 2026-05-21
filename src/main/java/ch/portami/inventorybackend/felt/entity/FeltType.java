package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "felt_type")
public class FeltType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // No cascade: FeltType is a lookup; Felts must outlive it.
    @OneToMany(mappedBy = "feltType", fetch = FetchType.LAZY)
    private final List<Felt> felts = new ArrayList<>();

    public FeltType() {
    }

    public FeltType(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Felt> getFelts() {
        return felts;
    }

    // --- Sync helpers ---------------------------------------------------

    public void addFelt(Felt felt) {
        felts.add(felt);
        felt.setFeltType(this);
    }

    public void removeFelt(Felt felt) {
        felts.remove(felt);
        felt.setFeltType(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof FeltType that)) {
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
        return "FeltType{id=" + id + ", name='" + name + "'}";
    }
}