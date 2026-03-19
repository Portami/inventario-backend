package ch.portami.inventorybackend.dto;

/**
 * This class describes a base generic product. This is currently a dummy structure.
 */
public class Product {

    /**
     * The unique identifier for this product.
     */
    private int id;

    /**
     * The name of this product.
     */
    private String name;

    /**
     * The article number is a human-readable unique identifier for a product.
     */
    private String articleNumber;

    /**
     * The product type
     */
    private ProductType type;

    /**
     * The felt thickness in millimeters (mm).
     */
    private int thickness;

    /**
     * The density in ?
     */
    private int density;

    /**
     * The description of this products color.
     */
    private Color color;

    public Product(int id, String articleNumber, ProductType type, Color color) {
        this.id = id;
        this.articleNumber = articleNumber;
        this.type = type;
        this.color = color;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int  getThickness() {
        return thickness;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public int getDensity() {
        return density;
    }

    public void setDensity(int density) {
        this.density = density;
    }

    public ProductType getType() {
        return type;
    }

    public void setType(ProductType type) {
        this.type = type;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}
