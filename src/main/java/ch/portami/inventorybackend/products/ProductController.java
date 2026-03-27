package ch.portami.inventorybackend.products;

import ch.portami.inventorybackend.products.dto.ProductPatchRequest;
import ch.portami.inventorybackend.products.dto.ProductRequest;
import ch.portami.inventorybackend.products.model.Color;
import ch.portami.inventorybackend.products.model.Product;
import ch.portami.inventorybackend.products.model.ProductType;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the {@code /products} resource.
 *
 * <p>Implements the full OpenAPI contract:
 * <ul>
 *   <li>GET    /products          – list, with optional type/color filters</li>
 *   <li>POST   /products          – create</li>
 *   <li>GET    /products/{id}     – get by id</li>
 *   <li>PUT    /products/{id}     – full replace</li>
 *   <li>PATCH  /products/{id}     – partial update</li>
 *   <li>DELETE /products/{id}     – delete</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * Lists all products. Results can be narrowed with optional query parameters:
     * {@code ?type=WOOL} and/or {@code ?color=RED}.
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false) ProductType type,
            @RequestParam(required = false) Color color) {

        List<Product> products = productService.getAllProducts(type, color);
        return ResponseEntity.ok(products);
    }

    /**
     * Creates a new product. Returns {@code 201 Created} with the persisted product.
     */
    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest request) {
        Product created = productService.createProduct(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Returns a single product by its numeric ID.
     * Responds with {@code 404} if no matching product exists.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        Product product = productService.getProductById(id);
        return ResponseEntity.ok(product);
    }

    /**
     * Fully replaces the product with the given ID (PUT semantics).
     * Responds with {@code 404} if no matching product exists.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable int id,
            @Valid @RequestBody ProductRequest request) {

        Product updated = productService.updateProduct(id, request);
        return ResponseEntity.ok(updated);
    }

    /**
     * Partially updates the product with the given ID (PATCH semantics).
     * Only fields present (non-null) in the request body are applied.
     * Responds with {@code 404} if no matching product exists.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Product> patchProduct(
            @PathVariable int id,
            @RequestBody ProductPatchRequest request) {

        Product patched = productService.patchProduct(id, request);
        return ResponseEntity.ok(patched);
    }

    /**
     * Deletes the product with the given ID.
     * Returns {@code 204 No Content} on success, {@code 404} if not found.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
