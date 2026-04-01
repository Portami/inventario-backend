package ch.portami.inventorybackend.products.model;

import jakarta.persistence.*;

@Entity
@Table(
        name = "products",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_products_article_number",
                columnNames = "article_number"
        )
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "article_number", nullable = false, unique = true)
    private String articleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductType type;

    private Integer thickness;
    private Integer density;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Color color;

    protected Product() {
    }

    public Product(String articleNumber, ProductType type, Color color) {
        this.articleNumber = articleNumber;
        this.type = type;
        this.color = color;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArticleNumber() {
        return articleNumber;
    }

    public void setArticleNumber(String articleNumber) {
        this.articleNumber = articleNumber;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public Integer getThickness() {
        return thickness;
    }

    public void setThickness(Integer thickness) {
        this.thickness = thickness;
    }

    public Integer getDensity() {
        return density;
    }

    public void setDensity(Integer density) {
        this.density = density;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}