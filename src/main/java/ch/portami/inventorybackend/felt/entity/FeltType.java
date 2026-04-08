package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "felt_type")
public class FeltType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "felt_type_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "feltType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Felt> felts = new ArrayList<>();

    public FeltType() {}

    public FeltType(String name) {
        this.name = name;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Felt> getFelts() { return felts; }
    public void setFelts(List<Felt> felts) { this.felts = felts; }
}