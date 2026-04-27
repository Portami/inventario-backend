package ch.portami.inventorybackend.product.api;

import ch.portami.inventorybackend.product.dto.productvariant.CreateProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.UpdateProductVariantDto;
import ch.portami.inventorybackend.product.service.ProductVariantService;
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

@RestController
@RequestMapping("/api/products/{productId}/variants")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    public ProductVariantController(ProductVariantService productVariantService) {
        this.productVariantService = productVariantService;
    }

    @PostMapping
    public ResponseEntity<ProductVariantDto> createProductVariant(@PathVariable long productId,
            @RequestBody @Valid CreateProductVariantDto createProductVariantDto) {
        ProductVariantDto productVariantDto = productVariantService.createProductVariant(productId,
                createProductVariantDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(productVariantDto);
    }

    @GetMapping("/{variantId}")
    public ResponseEntity<ProductVariantDto> getProductVariantById(@PathVariable long productId,
            @PathVariable long variantId) {
        ProductVariantDto productVariantDto = productVariantService.getProductVariantById(productId, variantId);
        return ResponseEntity.ok(productVariantDto);
    }

    @GetMapping
    public ResponseEntity<List<ProductVariantDto>> getAllProductVariants(@PathVariable long productId) {
        List<ProductVariantDto> variants = productVariantService.getAllProductVariants(productId);
        return ResponseEntity.ok(variants);
    }

    @PatchMapping("/{variantId}")
    public ResponseEntity<ProductVariantDto> updateProductVariant(@PathVariable long productId,
            @PathVariable long variantId, @RequestBody @Valid UpdateProductVariantDto updateProductVariantDto) {
        ProductVariantDto productVariantDto = productVariantService.updateProductVariant(productId, variantId,
                updateProductVariantDto);
        return ResponseEntity.ok(productVariantDto);
    }

    @DeleteMapping("/{variantId}")
    public ResponseEntity<Void> deleteProductVariant(@PathVariable long productId, @PathVariable long variantId) {
        productVariantService.deleteProductVariant(productId, variantId);
        return ResponseEntity.noContent()
                             .build();
    }

}
