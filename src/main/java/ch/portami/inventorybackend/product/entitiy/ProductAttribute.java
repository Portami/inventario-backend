package ch.portami.inventorybackend.product.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product_attribute")
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_attribute_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String name;

    // Consider replacing with an enum: STRING, INTEGER, BOOLEAN, DECIMAL
    @Column(name = "data_type")
    private String dataType;

    @Column(name = "is_required", nullable = false)
    private boolean isRequired;

    @Column(name = "default_value")
    private String defaultValue;

    @OneToMany(mappedBy = "productAttribute", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductAttributeValue> productAttributeValues = new ArrayList<>();

    public ProductAttribute() {}

    public ProductAttribute(Product product, String name, String dataType, boolean isRequired, String defaultValue) {
        this.product = product;
        this.name = name;
        this.dataType = dataType;
        this.isRequired = isRequired;
        this.defaultValue = defaultValue;
    }

    public Long getId() { return id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDataType() { return dataType; }
    public void setDataType(String dataType) { this.dataType = dataType; }

    public boolean isRequired() { return isRequired; }
    public void setRequired(boolean isRequired) { this.isRequired = isRequired; }

    public String getDefaultValue() { return defaultValue; }
    public void setDefaultValue(String defaultValue) { this.defaultValue = defaultValue; }

    public List<ProductAttributeValue> getProductAttributeValues() { return productAttributeValues; }
    public void setProductAttributeValues(List<ProductAttributeValue> values) { this.productAttributeValues = values; }
}