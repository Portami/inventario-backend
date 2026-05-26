package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.product.dto.productvariant.CreateProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.UpdateProductVariantDto;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.exception.InvalidProductAttributeReferenceException;
import ch.portami.inventorybackend.product.exception.ProductNotFoundException;
import ch.portami.inventorybackend.product.exception.ProductVariantNotFoundException;
import ch.portami.inventorybackend.product.mapper.ProductVariantMapper;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import ch.portami.inventorybackend.product.repository.ProductVariantRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for retrieving and managing product variants.
 */
@Service
public class ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductVariantMapper productVariantMapper;
    private final ProductRepository productRepository;

    /**
     * Creates a new ProductVariantService with the given dependencies.
     *
     * @param productVariantRepository the repository for accessing product variant data
     * @param productVariantMapper     the mapper for converting between ProductVariant entities and DTOs
     * @param productRepository        the repository for accessing product data, needed for resolving product
     *                                 references
     */
    public ProductVariantService(ProductVariantRepository productVariantRepository,
            ProductVariantMapper productVariantMapper, ProductRepository productRepository) {
        this.productVariantRepository = productVariantRepository;
        this.productVariantMapper = productVariantMapper;
        this.productRepository = productRepository;
    }

    /**
     * Creates a new product variant for the specified product based on the provided DTO.
     *
     * @param productId               the ID of the product to which the variant belongs
     * @param createProductVariantDto the DTO containing the data for the new product variant
     * @return the DTO of the created product variant
     * @throws ProductNotFoundException                  if no product with the given ID exists
     * @throws InvalidProductAttributeReferenceException if the DTO references one or more attributes that do not exist
     *                                                   for the specified product
     */
    @Transactional
    public ProductVariantDto createProductVariant(Long productId, CreateProductVariantDto createProductVariantDto) {
        Product product = getProduct(productId);
        ProductVariant productVariant = productVariantMapper.toProductVariant(createProductVariantDto, product);
        product.addProductVariant(productVariant);
        ProductVariant savedVariant = productVariantRepository.save(productVariant);
        return productVariantMapper.toProductVariantDto(savedVariant);
    }

    /**
     * Retrieves a product variant by its ID for the specified product.
     *
     * @param productId the ID of the product to which the variant belongs
     * @param variantId the ID of the product variant to retrieve
     * @return the DTO of the retrieved product variant
     * @throws ProductVariantNotFoundException if no product variant with the given ID exists for the specified product
     */
    @Transactional(readOnly = true)
    public ProductVariantDto getProductVariantById(Long productId, Long variantId) {
        ProductVariant productVariant = productVariantRepository.findById(variantId)
                                                                .orElseThrow(() -> new ProductVariantNotFoundException(
                                                                        productId, variantId));
        checkProductVariantBelongsToProduct(productId, productVariant);
        return productVariantMapper.toProductVariantDto(productVariant);
    }

    /**
     * Retrieves all product variants for the specified product.
     *
     * @param productId the ID of the product for which to retrieve the variants
     * @return a list of DTOs for all product variants of the specified product (may be empty)
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @Transactional(readOnly = true)
    public List<ProductVariantDto> getAllProductVariants(Long productId) {
        Product product = getProduct(productId);
        return product.getProductVariants()
                      .stream()
                      .map(productVariantMapper::toProductVariantDto)
                      .toList();
    }

    /**
     * Updates a product variant with the provided data.
     *
     * @param productId               the ID of the product to which the variant belongs
     * @param variantId               the ID of the product variant to update
     * @param updateProductVariantDto the DTO containing the requested updates for the product variant. Only fields with
     *                                non-null values are updated.
     * @return the DTO of the updated product variant
     * @throws ProductVariantNotFoundException           if no product variant with the given ID exists for the
     *                                                   specified product
     * @throws InvalidProductAttributeReferenceException if the DTO references one or more attributes that do not exist
     *                                                   for the specified product
     */
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

    /**
     * Deletes a product variant by its product and variant ID. If no product variant with the given ID exists for the
     * specified product, this method does nothing (i.e. no exception is thrown). All inventory information for the
     * deleted variant will be removed as well.
     *
     * @param productId the ID of the product to which the variant belongs
     * @param variantId the ID of the product variant to delete
     * @throws ProductNotFoundException if no product with the given ID exists
     */
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
