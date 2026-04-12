package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "supplier")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    // No cascade: Supplier is a reference; Felts must outlive it.
    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private List<Felt> felts = new ArrayList<>();

    public Supplier() {}

    public Supplier(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Felt> getFelts() { return felts; }

    // --- Sync helpers ---------------------------------------------------

    public void addFelt(Felt felt) {
        felts.add(felt);
        felt.setSupplier(this);
    }

    public void removeFelt(Felt felt) {
        felts.remove(felt);
        felt.setSupplier(null);
    }

    // --- equals / hashCode ----------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Supplier that)) return false;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Supplier{id=" + id + ", name='" + name + "'}";
    }
}