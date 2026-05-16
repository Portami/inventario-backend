package ch.portami.inventorybackend.product;

import ch.portami.inventorybackend.product.dto.product.CreateProductDto;
import ch.portami.inventorybackend.product.dto.product.ProductDto;
import ch.portami.inventorybackend.product.dto.product.UpdateProductDto;
import ch.portami.inventorybackend.product.dto.productattribute.CreateProductAttributeDto;
import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeChangeDto;
import ch.portami.inventorybackend.product.dto.productattribute.ProductAttributeDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductAttribute;
import ch.portami.inventorybackend.product.entity.ProductAttributeValue;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class ProductControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final String PRODUCTS_URL = "/api/products";
    private static Long testCategory1Id;
    private static Long testCategory2Id;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    static void beforeAll(@Autowired CategoryRepository categoryRepository) {
        Category category1 = new Category("Test Category");
        category1 = categoryRepository.save(category1);
        testCategory1Id = category1.getId();

        Category category2 = new Category("Another Test Category");
        category2 = categoryRepository.save(category2);
        testCategory2Id = category2.getId();
    }

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();
    }

    private Product createTestProduct() {
        Product product = new Product(categoryRepository.findById(testCategory1Id)
                                                        .orElseThrow(), "Test Product");
        return productRepository.save(product);
    }

    private Product createTestProductWithAttributes() {
        Product product = new Product(categoryRepository.findById(testCategory1Id)
                                                        .orElseThrow(), "Product with Attributes");
        ProductAttribute attribute1 = new ProductAttribute(product, "Material");
        ProductAttribute attribute2 = new ProductAttribute(product, "Weight");

        product.addProductAttribute(attribute1);
        product.addProductAttribute(attribute2);

        return productRepository.save(product);
    }

    private Product createTestProductWithVariants() {
        Product product = new Product(categoryRepository.findById(testCategory1Id)
                                                        .orElseThrow(), "Product with Variants");

        ProductAttribute attribute = new ProductAttribute(product, "Color");
        product.addProductAttribute(attribute);

        ProductVariant variant1 = new ProductVariant(product, "Red Variant", BigDecimal.valueOf(10));
        variant1.addProductAttributeValue(new ProductAttributeValue(variant1, attribute, "Red"));
        product.addProductVariant(variant1);

        ProductVariant variant2 = new ProductVariant(product, "Blue Variant", BigDecimal.valueOf(12));
        variant2.addProductAttributeValue(new ProductAttributeValue(variant2, attribute, "Blue"));
        product.addProductVariant(variant2);

        return productRepository.save(product);
    }

    @Nested
    @DisplayName("POST /api/products - Create Product")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product with valid data")
        void testCreateProductSuccess() {
            CreateProductDto createProductDto = new CreateProductDto(
                    "Test Product",
                    testCategory1Id,
                    List.of()
            );

            ProductDto body = restTestClient.post()
                                            .uri(PRODUCTS_URL)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(createProductDto)
                                            .exchange()
                                            .expectStatus()
                                            .isCreated()
                                            .returnResult(ProductDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Test Product");
            assertThat(body.category()
                           .id()).isEqualTo(testCategory1Id);
            assertThat(body.attributes()).isEmpty();
            assertThat(body.variants()).isEmpty();
            assertThat(body.id()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should create product with attributes")
        void testCreateProductWithAttributes() {
            List<CreateProductAttributeDto> attributes = List.of(
                    new CreateProductAttributeDto("Color"),
                    new CreateProductAttributeDto("Size")
            );
            CreateProductDto createProductDto = new CreateProductDto(
                    "Product with Attributes",
                    testCategory1Id,
                    attributes
            );

            ProductDto body = restTestClient.post()
                                            .uri(PRODUCTS_URL)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(createProductDto)
                                            .exchange()
                                            .expectStatus()
                                            .isCreated()
                                            .returnResult(ProductDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.attributes()).hasSize(2);
            assertThat(body.attributes()).extracting(ProductAttributeDto::name)
                                         .contains("Color", "Size");
        }

        @Test
        @DisplayName("Should return 400 when name is null")
        void testCreateProductMissingName() {
            CreateProductDto createProductDto = new CreateProductDto(
                    null,
                    testCategory1Id,
                    List.of()
            );

            restTestClient.post()
                          .uri(PRODUCTS_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createProductDto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when categoryId is null")
        void testCreateProductMissingCategoryId() {
            CreateProductDto createProductDto = new CreateProductDto(
                    "Test Product",
                    null,
                    List.of()
            );

            restTestClient.post()
                          .uri(PRODUCTS_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createProductDto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when name of an attribute is null")
        void testCreateProductWithAttributeMissingName() {
            List<CreateProductAttributeDto> attributes = List.of(
                    new CreateProductAttributeDto(null)
            );
            CreateProductDto createProductDto = new CreateProductDto(
                    "Test Product",
                    testCategory1Id,
                    attributes
            );

            restTestClient.post()
                          .uri(PRODUCTS_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createProductDto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 when category does not exist")
        void testCreateProductWithNonExistentCategory() {
            CreateProductDto createProductDto = new CreateProductDto(
                    "Test Product",
                    99999L,
                    List.of()
            );

            restTestClient.post()
                          .uri(PRODUCTS_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createProductDto)
                          .exchange()
                          .expectStatus()
                          .isEqualTo(422);
        }

    }

    @Nested
    @DisplayName("GET /api/products/{id} - Get Product By ID")
    class GetProductByIdTests {

        @Test
        @DisplayName("Should retrieve product by id")
        void testGetProductByIdSuccess() {
            Product product = createTestProduct();
            Long productId = product.getId();

            ProductDto body = restTestClient.get()
                                            .uri(PRODUCTS_URL + "/{id}", productId)
                                            .exchange()
                                            .expectStatus()
                                            .isOk()
                                            .returnResult(ProductDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isEqualTo(productId);
            assertThat(body.name()).isEqualTo("Test Product");
            assertThat(body.category()
                           .id()).isEqualTo(testCategory1Id);
            assertThat(body.attributes()).isEmpty();
            assertThat(body.variants()).isEmpty();
        }

        @Test
        @DisplayName("Should retrieve product with attributes and variants as nested properties")
        void testGetProductIncludesAttributesAndVariants() {
            Product product = createTestProductWithAttributes();

            ProductDto body = restTestClient.get()
                                            .uri(PRODUCTS_URL + "/{id}", product.getId())
                                            .exchange()
                                            .expectStatus()
                                            .isOk()
                                            .returnResult(ProductDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isEqualTo(product.getId());
            assertThat(body.attributes()).isNotEmpty();
            assertThat(body.attributes()).extracting(ProductAttributeDto::name)
                                         .contains("Material", "Weight");
        }

        @Test
        @DisplayName("Should retrieve product with variants as nested properties")
        void testGetProductIncludesVariants() {
            Product product = createTestProductWithVariants();

            ProductDto body = restTestClient.get()
                                            .uri(PRODUCTS_URL + "/{id}", product.getId())
                                            .exchange()
                                            .expectStatus()
                                            .isOk()
                                            .returnResult(ProductDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isEqualTo(product.getId());
            assertThat(body.variants()).hasSize(2);
            assertThat(body.variants()).extracting(ProductVariantDto::name)
                                       .contains("Red Variant", "Blue Variant");
        }

        @Test
        @DisplayName("Should return 404 when product does not exist")
        void testGetProductByIdNotFound() {
            restTestClient.get()
                          .uri(PRODUCTS_URL + "/{id}", 99999L)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }

    @Nested
    @DisplayName("GET /api/products - Get All Products")
    class GetAllProductsTests {

        @Test
        @DisplayName("Should return empty list when no products exist")
        void testGetAllProductsEmpty() {
            List<ProductDto> body = restTestClient.get()
                                                  .uri(PRODUCTS_URL)
                                                  .exchange()
                                                  .expectStatus()
                                                  .isOk()
                                                  .returnResult(new ParameterizedTypeReference<List<ProductDto>>() {
                                                  })
                                                  .getResponseBody();

            assertThat(body).isEmpty();
        }

        @Test
        @DisplayName("Should return list of all products")
        void testGetAllProductsSuccess() {
            Product product1 = createTestProduct();
            Product product2 = createTestProductWithAttributes();
            Product product3 = createTestProductWithVariants();
            productRepository.saveAll(List.of(product1, product2, product3));

            List<ProductDto> body = restTestClient.get()
                                                  .uri(PRODUCTS_URL)
                                                  .exchange()
                                                  .expectStatus()
                                                  .isOk()
                                                  .returnResult(new ParameterizedTypeReference<List<ProductDto>>() {
                                                  })
                                                  .getResponseBody();

            assertThat(body).hasSize(3);
            assertThat(body).extracting(ProductDto::name)
                            .contains("Test Product", "Product with Attributes", "Product with Variants");
        }

    }

    @Nested
    @DisplayName("PATCH /api/products/{id} - Update Product")
    class UpdateProductTests {

        private Long productId;

        @BeforeEach
        void setup() {
            Product product = createTestProduct();
            productId = product.getId();
        }

        @Test
        @DisplayName("Should update product name")
        void testUpdateProductNameSuccess() {
            UpdateProductDto updateProductDto = new UpdateProductDto(
                    "Updated Name",
                    null,
                    null
            );

            ProductDto body = restTestClient.patch()
                                            .uri(PRODUCTS_URL + "/{id}", productId)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(updateProductDto)
                                            .exchange()
                                            .expectStatus()
                                            .isOk()
                                            .returnResult(ProductDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Updated Name");
            assertThat(body.id()).isEqualTo(productId);
        }

        @Test
        @DisplayName("Should update product category")
        void testUpdateProductCategorySuccess() {
            UpdateProductDto updateProductDto = new UpdateProductDto(
                    null,
                    testCategory2Id,
                    null
            );

            ProductDto body = restTestClient.patch()
                                            .uri(PRODUCTS_URL + "/{id}", productId)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(updateProductDto)
                                            .exchange()
                                            .expectStatus()
                                            .isOk()
                                            .returnResult(ProductDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.category()
                           .id()).isEqualTo(testCategory2Id);
        }

        @Test
        @DisplayName("Should update both name and category")
        void testUpdateProductNameAndCategorySuccess() {
            UpdateProductDto updateProductDto = new UpdateProductDto(
                    "New Name",
                    testCategory2Id,
                    null
            );

            ProductDto body = restTestClient.patch()
                                            .uri(PRODUCTS_URL + "/{id}", productId)
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(updateProductDto)
                                            .exchange()
                                            .expectStatus()
                                            .isOk()
                                            .returnResult(ProductDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("New Name");
            assertThat(body.category()
                           .id()).isEqualTo(testCategory2Id);
        }

        @Test
        @DisplayName("Should update product with new attributes")
        void testUpdateProductWithAttributes() {
            ProductAttributeChangeDto newAttribute = new ProductAttributeChangeDto(null, "Size");
            UpdateProductDto updateDto = new UpdateProductDto(
                    null,
                    null,
                    List.of(newAttribute)
            );

            ProductDto updatedProduct = restTestClient.patch()
                                                      .uri(PRODUCTS_URL + "/{id}", productId)
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .body(updateDto)
                                                      .exchange()
                                                      .expectStatus()
                                                      .isOk()
                                                      .returnResult(ProductDto.class)
                                                      .getResponseBody();

            assertThat(updatedProduct).isNotNull();
            assertThat(updatedProduct.attributes()).hasSize(1);
            assertThat(updatedProduct.attributes()
                                     .getFirst()
                                     .name()).isEqualTo("Size");
        }

        @Test
        @DisplayName("Should update existing product attribute")
        void testUpdateProductExistingAttribute() {
            Product product = createTestProductWithAttributes();
            ProductAttribute attributeToModify = product.getProductAttributes()
                                                        .getFirst();
            ProductAttribute otherAttribute = product.getProductAttributes()
                                                     .getLast();

            ProductAttributeChangeDto attributeToModifyDto = new ProductAttributeChangeDto(attributeToModify.getId(),
                    "Updated Attribute Name");
            ProductAttributeChangeDto otherAttributeDto = new ProductAttributeChangeDto(otherAttribute.getId(),
                    otherAttribute.getName());
            UpdateProductDto updateDto = new UpdateProductDto(
                    null,
                    null,
                    List.of(attributeToModifyDto, otherAttributeDto)
            );

            ProductDto updatedProduct = restTestClient.patch()
                                                      .uri(PRODUCTS_URL + "/{id}", product.getId())
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .body(updateDto)
                                                      .exchange()
                                                      .expectStatus()
                                                      .isOk()
                                                      .returnResult(ProductDto.class)
                                                      .getResponseBody();

            assertThat(updatedProduct).isNotNull();
            assertThat(updatedProduct.attributes()).hasSize(2);
            assertThat(updatedProduct.attributes()).extracting(ProductAttributeDto::name)
                                                   .contains("Updated Attribute Name", otherAttribute.getName());
        }

        @Test
        @DisplayName("Should remove attributes when not included in update")
        void testUpdateProductRemoveAttributes() {
            Product product = createTestProductWithAttributes();
            UpdateProductDto updateDto = new UpdateProductDto(
                    null,
                    null,
                    List.of()
            );

            ProductDto updatedProduct = restTestClient.patch()
                                                      .uri(PRODUCTS_URL + "/{id}", product.getId())
                                                      .contentType(MediaType.APPLICATION_JSON)
                                                      .body(updateDto)
                                                      .exchange()
                                                      .expectStatus()
                                                      .isOk()
                                                      .returnResult(ProductDto.class)
                                                      .getResponseBody();

            assertThat(updatedProduct).isNotNull();
            assertThat(updatedProduct.attributes()).isEmpty();
        }

        @Test
        @DisplayName("Should return 404 when product does not exist")
        void testUpdateProductNotFound() {
            UpdateProductDto updateProductDto = new UpdateProductDto(
                    "Updated Name",
                    null,
                    null
            );

            restTestClient.patch()
                          .uri(PRODUCTS_URL + "/{id}", 99999L)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(updateProductDto)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("Should return 404 when updating with non-existent category")
        void testUpdateProductWithNonExistentCategory() {
            UpdateProductDto updateProductDto = new UpdateProductDto(
                    null,
                    99999L,
                    null
            );

            restTestClient.patch()
                          .uri(PRODUCTS_URL + "/{id}", productId)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(updateProductDto)
                          .exchange()
                          .expectStatus()
                          .isEqualTo(422);
        }

    }

    @Nested
    @DisplayName("DELETE /api/products/{id} - Delete Product")
    class DeleteProductTests {

        private Long productId;

        @BeforeEach
        void setup() {
            Product product = new Product(categoryRepository.findById(testCategory1Id)
                                                            .orElseThrow(), "Test Product");
            Product savedProduct = productRepository.save(product);
            productId = savedProduct.getId();
        }

        @Test
        @DisplayName("Should delete product successfully")
        void testDeleteProductSuccess() {
            restTestClient.delete()
                          .uri(PRODUCTS_URL + "/{id}", productId)
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            assertThat(productRepository.existsById(productId)).isFalse();
        }

        @Test
        @DisplayName("Should do nothing when deleting non-existent product")
        void testDeleteProductNotFound() {
            restTestClient.delete()
                          .uri(PRODUCTS_URL + "/{id}", 99999L)
                          .exchange()
                          .expectStatus()
                          .isNoContent();
        }

    }

}


