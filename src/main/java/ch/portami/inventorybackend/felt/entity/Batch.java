package ch.portami.inventorybackend.felt.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "batch")
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "batch_id")
    private Long id;

    @Column(name = "batch_name", nullable = false)
    private String batchName;

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ScrapPiece> scrapPieces = new ArrayList<>();

    @OneToMany(mappedBy = "batch", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FeltRoll> feltRolls = new ArrayList<>();

    public Batch() {}

    public Batch(String batchName) {
        this.batchName = batchName;
    }

    public Long getId() { return id; }

    public String getBatchName() { return batchName; }
    public void setBatchName(String batchName) { this.batchName = batchName; }

    public List<ScrapPiece> getScrapPieces() { return scrapPieces; }
    public void setScrapPieces(List<ScrapPiece> scrapPieces) { this.scrapPieces = scrapPieces; }

    public List<FeltRoll> getFeltRolls() { return feltRolls; }
    public void setFeltRolls(List<FeltRoll> feltRolls) { this.feltRolls = feltRolls; }
}