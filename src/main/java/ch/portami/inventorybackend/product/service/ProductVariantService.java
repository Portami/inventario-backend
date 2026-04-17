package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.product.dto.productvariant.CreateProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantMapper;
import ch.portami.inventorybackend.product.dto.productvariant.UpdateProductVariantDto;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import ch.portami.inventorybackend.product.repository.ProductVariantRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;
    private final ProductRepository productRepository;

    public ProductVariantService(ProductVariantRepository productVariantRepository, ProductVariantMapper productVariantMapper, ProductRepository productRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductVariantDto createProductVariant(long productId, CreateProductVariantDto createProductVariantDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        ProductVariant productVariant = productVariantMapper.toProductVariant(createProductVariantDto, product);
        product.addProductVariant(productVariant);
        ProductVariant savedVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toProductVariantDto(savedVariant);
    }

    @Transactional(readOnly = true)
    public ProductVariantDto getProductVariantById(long productId, long variantId) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + variantId));
        if (!productVariant.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Product variant does not belong to product with id: " + productId);
        }
        return productVariantMapper.toProductVariantDto(productVariant);
    }

    @Transactional(readOnly = true)
    public List<ProductVariantDto> getAllProductVariants(long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        return product.getProductVariants().stream()
                .map(productVariantMapper::toProductVariantDto)
                .toList();
    }

    @Transactional
    public ProductVariantDto updateProductVariant(long productId, long variantId, UpdateProductVariantDto updateProductVariantDto) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + variantId));
        if (!productVariant.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Product variant does not belong to product with id: " + productId);
        }
        productVariantMapper.updateProductVariant(updateProductVariantDto, productVariant, product);
        ProductVariant updatedVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toProductVariantDto(updatedVariant);
    }

    @Transactional
    public void deleteProductVariant(long productId, long variantId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + productId));
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Product variant not found with id: " + variantId));
        if (!productVariant.getProduct().getId().equals(productId)) {
            throw new RuntimeException("Product variant does not belong to product with id: " + productId);
        }
        product.removeProductVariant(productVariant);
        productVariantRepository.delete(productVariant);
    }

}
