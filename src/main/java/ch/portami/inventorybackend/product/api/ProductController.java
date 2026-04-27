package ch.portami.inventorybackend.product.api;

import ch.portami.inventorybackend.product.dto.product.CreateProductDto;
import ch.portami.inventorybackend.product.dto.product.ProductDto;
import ch.portami.inventorybackend.product.dto.product.UpdateProductDto;
import ch.portami.inventorybackend.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Products", description = "Manage products and their attributes. Each product represents a unique product type and usually has multiple variants with different attribute values and prices.")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a product")
    @ApiResponse(responseCode = "201", description = "Product successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "The provided category ID does not exist")
    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody @Valid CreateProductDto createProductDto) {
        ProductDto productDto = productService.createProduct(createProductDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(productDto);
    }

    @Operation(summary = "Get a product by ID")
    @ApiResponse(responseCode = "200", description = "Product found and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No product exists with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable long id) {
        ProductDto productDto = productService.getProductById(id);
        return ResponseEntity.ok(productDto);
    }

    @Operation(summary = "List all products")
    @ApiResponse(responseCode = "200", description = "List of all products (may be empty)")
    @GetMapping
    public ResponseEntity<List<ProductDto>> getAllProducts() {
        List<ProductDto> products = productService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    @Operation(summary = "Update a product", description = "Updates all provided fields of a product. Omitted fields are not updated. If the attributes field is provided, all attributes that should be kept must be included in the request body, otherwise they will be removed from the product. For existing attributes, the ID must be provided, otherwise a new attribute will be created.")
    @ApiResponse(responseCode = "200", description = "Product successfully updated and returned in the response body")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "No product exists with the given ID")
    @ApiResponse(responseCode = "404", description = "One or more provided attribute IDs do not exist")
    @ApiResponse(responseCode = "404", description = "The provided category ID does not exist")
    @PatchMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable long id,
            @RequestBody @Valid UpdateProductDto updateProductDto) {
        ProductDto productDto = productService.updateProduct(id, updateProductDto);
        return ResponseEntity.ok(productDto);
    }

    @Operation(summary = "Delete a product", description = "Deletes a product and all its variants.")
    @ApiResponse(responseCode = "204", description = "Product successfully deleted or is not existing (anymore)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent()
                             .build();
    }

}
