package ch.portami.inventorybackend.products;

import ch.portami.inventorybackend.products.dto.ProductPatchRequest;
import ch.portami.inventorybackend.products.dto.ProductRequest;
import ch.portami.inventorybackend.products.exceptions.ProductNotFoundException;
import ch.portami.inventorybackend.products.model.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business-logic layer for the Products resource.
 */
@Service
public class ProductService {

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    // ── Queries ────────────────────────────────────────────────────────────────

    /**
     * Returns all products, with optional filtering by {@code type} and/or {@code color}.
     * {@code null} parameters mean "no filter applied".
     */
    public List<Product> getAllProducts(ProductType type, Color color) {
        return repository.findAll(type, color);
    }

    /**
     * Returns a single product by its numeric ID.
     *
     * @throws ProductNotFoundException if no product with that ID exists.
     */
    public Product getProductById(int id) {
        return repository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    /**
     * Creates a new product from the given request payload.
     */
    public Product createProduct(ProductRequest request) {
        var product = new Product(0, request.articleNumber(), request.type(), request.color());
        applyFullUpdate(product, request);
        return repository.save(product);
    }

    /**
     * Fully replaces an existing product (PUT semantics).
     *
     * @throws ProductNotFoundException if no product with that ID exists.
     */
    public Product updateProduct(int id, ProductRequest request) {
        var product = getProductById(id);         // throws if absent
        product.setArticleNumber(request.articleNumber());
        product.setType(request.type());
        product.setColor(request.color());
        applyFullUpdate(product, request);
        return repository.save(product);
    }

    /**
     * Partially updates an existing product (PATCH semantics).
     * Only non-null fields in the request are applied.
     *
     * @throws ProductNotFoundException if no product with that ID exists.
     */
    public Product patchProduct(int id, ProductPatchRequest request) {
        var product = getProductById(id);         // throws if absent

        if (request.name()          != null) product.setName(request.name());
        if (request.articleNumber() != null) product.setArticleNumber(request.articleNumber());
        if (request.type()          != null) product.setType(request.type());
        if (request.color()         != null) product.setColor(request.color());
        if (request.thickness()     != null) product.setThickness(request.thickness());
        if (request.density()       != null) product.setDensity(request.density());

        return repository.save(product);
    }

    /**
     * Deletes a product by its ID.
     *
     * @throws ProductNotFoundException if no product with that ID exists.
     */
    public void deleteProduct(int id) {
        if (!repository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        repository.deleteById(id);
    }


    /** Applies all nullable fields from a {@link ProductRequest} onto an existing product. */
    private static void applyFullUpdate(Product product, ProductRequest request) {
        if (request.name()      != null) product.setName(request.name());
        if (request.thickness() != null) product.setThickness(request.thickness());
        if (request.density()   != null) product.setDensity(request.density());
    }
}
