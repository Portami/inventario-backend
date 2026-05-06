package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.product.dto.productvariant.CreateProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.UpdateProductVariantDto;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.exception.ProductNotFoundException;
import ch.portami.inventorybackend.product.exception.ProductVariantNotFoundException;
import ch.portami.inventorybackend.product.mapper.ProductVariantMapper;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import ch.portami.inventorybackend.product.repository.ProductVariantRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;
    private final ProductRepository productRepository;

    public ProductVariantService(ProductVariantRepository productVariantRepository,
            ProductVariantMapper productVariantMapper, ProductRepository productRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductVariantDto createProductVariant(Long productId, CreateProductVariantDto createProductVariantDto) {
        Product product = getProduct(productId);
        ProductVariant productVariant = productVariantMapper.toProductVariant(createProductVariantDto, product);
        product.addProductVariant(productVariant);
        ProductVariant savedVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toProductVariantDto(savedVariant);
    }

    @Transactional(readOnly = true)
    public ProductVariantDto getProductVariantById(Long productId, Long variantId) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                                                                .orElseThrow(() -> new ProductVariantNotFoundException(
                                                                        productId, variantId));
        checkProductVariantBelongsToProduct(productId, productVariant);
        return productVariantMapper.toProductVariantDto(productVariant);
    }

    @Transactional(readOnly = true)
    public List<ProductVariantDto> getAllProductVariants(Long productId) {
        Product product = getProduct(productId);
        return product.getProductVariants()
                      .stream()
                      .map(productVariantMapper::toProductVariantDto)
                      .toList();
    }

    @Transactional
    public ProductVariantDto updateProductVariant(Long productId, Long variantId,
            UpdateProductVariantDto updateProductVariantDto) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                                                                .orElseThrow(() -> new ProductVariantNotFoundException(
                                                                        productId, variantId));
        checkProductVariantBelongsToProduct(productId, productVariant);
        productVariantMapper.updateProductVariant(updateProductVariantDto, productVariant);
        ProductVariant updatedVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toProductVariantDto(updatedVariant);
    }

    @Transactional
    public void deleteProductVariant(Long productId, Long variantId) {
        Optional<ProductVariant> productVariantOpt = productVariantRepository.findById(variantId);
        if (productVariantOpt.isEmpty()) {
            return;
        }
        ProductVariant productVariant = productVariantOpt.get();
        checkProductVariantBelongsToProduct(productId, productVariant);
        productVariantRepository.delete(productVariant);
    }

    private Product getProduct(Long productId) {
        return productRepository.findById(productId)
                                .orElseThrow(() -> new ProductNotFoundException(productId));
    }

    private void checkProductVariantBelongsToProduct(Long productId, ProductVariant productVariant) {
        if (!productVariant.getProduct()
                           .getId()
                           .equals(productId)) {
            throw new ProductVariantNotFoundException(productId, productVariant.getId());
        }
    }

}
