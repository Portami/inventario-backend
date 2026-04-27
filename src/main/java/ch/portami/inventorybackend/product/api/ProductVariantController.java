package ch.portami.inventorybackend.product.api;

import ch.portami.inventorybackend.product.dto.productvariant.CreateProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.UpdateProductVariantDto;
import ch.portami.inventorybackend.product.service.ProductVariantService;
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

@Tag(name = "Product Variants", description = "Manage product variants. Each variant belongs to exactly one product and has a price and different attribute values.")
@RestController
@RequestMapping("/api/products/{productId}/variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @Operation(summary = "Create a variant of the provided product")
    @ApiResponse(responseCode = "201", description = "Product variant successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "The product with the given ID does not exist")
    @ApiResponse(responseCode = "404", description = "One or more of the provided attributes do not exist or do not belong to the product")
    @PostMapping
    public ResponseEntity<ProductVariantDto> createProductVariant(@PathVariable long productId,
            @RequestBody @Valid CreateProductVariantDto createProductVariantDto) {
        ProductVariantDto productVariantDto = productVariantService.createProductVariant(productId,
                createProductVariantDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(productVariantDto);
    }

    @Operation(summary = "Get a product variant by ID")
    @ApiResponse(responseCode = "200", description = "Product variant found and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No product variant exists with the given ID for the specified product")
    @GetMapping("/{variantId}")
    public ResponseEntity<ProductVariantDto> getProductVariantById(@PathVariable long productId,
            @PathVariable long variantId) {
        ProductVariantDto productVariantDto = productVariantService.getProductVariantById(productId, variantId);
        return ResponseEntity.ok(productVariantDto);
    }

    @Operation(summary = "List all product variants of the provided product")
    @ApiResponse(responseCode = "200", description = "List of all product variants for the specified product (may be empty)")
    @ApiResponse(responseCode = "404", description = "No product exists with the given ID")
    @GetMapping
    public ResponseEntity<List<ProductVariantDto>> getAllProductVariants(@PathVariable long productId) {
        List<ProductVariantDto> variants = productVariantService.getAllProductVariants(productId);
        return ResponseEntity.ok(variants);
    }

    @Operation(summary = "Update a product variant", description = "Updates all provided fields of a product variant. Omitted fields are not updated. If the attributes field is provided, all attributes that should be kept must be included in the request body, otherwise they will be removed from the product variant.")
    @ApiResponse(responseCode = "200", description = "Product variant successfully updated. Response body contains the updated product variant.")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "No product variant exists with the given ID for the specified product")
    @ApiResponse(responseCode = "404", description = "One or more of the provided attributes do not exist or do not belong to the product")
    @PatchMapping("/{variantId}")
    public ResponseEntity<ProductVariantDto> updateProductVariant(@PathVariable long productId,
            @PathVariable long variantId, @RequestBody @Valid UpdateProductVariantDto updateProductVariantDto) {
        ProductVariantDto productVariantDto = productVariantService.updateProductVariant(productId, variantId,
                updateProductVariantDto);
        return ResponseEntity.ok(productVariantDto);
    }

    @Operation(summary = "Delete a product variant")
    @ApiResponse(responseCode = "204", description = "Product variant successfully deleted or is not existing (anymore)")
    @ApiResponse(responseCode = "404", description = "The product with the given ID does not exist")
    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> deleteProductVariant(@PathVariable long productId, @PathVariable long variantId) {
        productVariantService.deleteProductVariant(productId, variantId);
        return ResponseEntity.noContent()
                             .build();
    }

}
