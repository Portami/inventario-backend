package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.product.dto.product.CreateProductDto;
import ch.portami.inventorybackend.product.dto.product.ProductDto;
import ch.portami.inventorybackend.product.dto.product.UpdateProductDto;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.exception.InvalidCategoryReferenceException;
import ch.portami.inventorybackend.product.exception.InvalidProductAttributeReferenceException;
import ch.portami.inventorybackend.product.exception.ProductNotFoundException;
import ch.portami.inventorybackend.product.mapper.ProductMapper;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for retrieving and managing products.
 */
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;

    /**
     * Creates a new ProductService with the given dependencies.
     *
     * @param productRepository  the repository for accessing product data
     * @param productMapper      the mapper for converting between Product entities and DTOs
     * @param categoryRepository the repository for accessing category data, needed for resolving category references
     *                           when creating/updating products
     */
    public ProductService(ProductRepository productRepository, ProductMapper productMapper,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
        this.categoryRepository = categoryRepository;
    }

    /**
     * Creates a new product based on the provided DTO.
     *
     * @param createProductDto the DTO containing the data for the new product
     * @return the DTO of the created product
     * @throws InvalidCategoryReferenceException if the DTO references a category that does not exist
     */
    @Transactional
    public ProductDto createProduct(CreateProductDto createProductDto) {
        Product product = productMapper.toProduct(createProductDto, categoryRepository);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDto(savedProduct);
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param id the ID of the product to retrieve
     * @return the DTO of the retrieved product
     * @throws ProductNotFoundException if no product with the given ID exists
     */
    @Transactional(readOnly = true)
    public ProductDto getProductById(Long id) {
        Product product = getProduct(id);
        return productMapper.toProductDto(product);
    }

    /**
     * Retrieves all products.
     *
     * @return a list of DTOs for all products
     */
    @Transactional(readOnly = true)
    public List<ProductDto> getAllProducts() {
        return productRepository.findAll()
                                .stream()
                                .map(productMapper::toProductDto)
                                .toList();
    }

    /**
     * Updates an existing product with the provided data.
     *
     * @param id               the ID of the product to update
     * @param updateProductDto the DTO containing the requested updates for the product. Only fields with non-null
     *                         values are updated.
     * @return the DTO of the updated product
     * @throws ProductNotFoundException                  if no product with the given ID exists
     * @throws InvalidCategoryReferenceException         if the DTO references a category that does not exist
     * @throws InvalidProductAttributeReferenceException if the DTO references a product attribute that does not exist
     *                                                   for the given product
     */
    @Transactional
    public ProductDto updateProduct(Long id, UpdateProductDto updateProductDto) {
        Product product = getProduct(id);
        productMapper.updateProduct(updateProductDto, product, categoryRepository);
        Product updatedProduct = productRepository.save(product);
        return productMapper.toProductDto(updatedProduct);
    }

    /**
     * Deletes a product by its ID. All variants will be deleted as well. If no product with the given ID exists, this
     * method does nothing (i.e. no exception is thrown).
     *
     * @param id the ID of the product to delete
     */
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                                .orElseThrow(() -> new ProductNotFoundException(id));
    }

}
