package ch.portami.inventorybackend.product;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.product.dto.productattributevalue.CreateProductAttributeValueDto;
import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueChangeDto;
import ch.portami.inventorybackend.product.dto.productattributevalue.ProductAttributeValueDto;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryDto;
import ch.portami.inventorybackend.product.dto.productvariant.CreateProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.ProductVariantDto;
import ch.portami.inventorybackend.product.dto.productvariant.UpdateProductVariantDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductAttribute;
import ch.portami.inventorybackend.product.entity.ProductAttributeValue;
import ch.portami.inventorybackend.product.entity.ProductInventory;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import ch.portami.inventorybackend.product.repository.ProductInventoryRepository;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import ch.portami.inventorybackend.product.repository.ProductVariantRepository;
import ch.portami.inventorybackend.storage.entity.Storage;
import ch.portami.inventorybackend.storage.repository.StorageRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

class ProductVariantControllerIntegrationTest extends BaseIntegrationTest {

    private static final String VARIANTS_URL_TEMPLATE = "/api/products/{productId}/variants";
    private static Long testProductId;

    private Product product;
    private List<ProductAttribute> attributes;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @BeforeAll
    static void beforeAll(@Autowired CategoryRepository categoryRepository,
            @Autowired ProductRepository productRepository) {
        Category category = new Category("Test Category for Variants");
        category = categoryRepository.save(category);

        Product product = new Product(category, "Test Product for Variants");

        ProductAttribute attribute1 = new ProductAttribute(product, "Color");
        product.addProductAttribute(attribute1);

        ProductAttribute attribute2 = new ProductAttribute(product, "Size");
        product.addProductAttribute(attribute2);

        product = productRepository.save(product);
        testProductId = product.getId();
    }

    @BeforeEach
    void setUp(@Autowired PlatformTransactionManager transactionManager) {
        new TransactionTemplate(transactionManager).executeWithoutResult(_ -> {
            productVariantRepository.deleteAll();
            product = productRepository.findById(testProductId)
                                       .orElseThrow();
            attributes = new ArrayList<>(product.getProductAttributes());
        });
    }

    private ProductVariant createTestVariant() {
        ProductVariant variant = new ProductVariant(product, "Test Variant", BigDecimal.valueOf(10));
        ProductAttributeValue attributeValue = new ProductAttributeValue(variant, product.getProductAttributes()
                                                                                         .getFirst(), "Red");
        variant.addProductAttributeValue(attributeValue);
        return productVariantRepository.save(variant);
    }

    private ProductVariant createTestVariantWithMultipleAttributes() {
        ProductVariant variant = new ProductVariant(product, "Multi-Attribute Variant", BigDecimal.valueOf(15));
        ProductAttributeValue color = new ProductAttributeValue(variant, attributes.get(0), "Blue");
        ProductAttributeValue size = new ProductAttributeValue(variant, attributes.get(1), "Large");
        variant.addProductAttributeValue(color);
        variant.addProductAttributeValue(size);
        return productVariantRepository.save(variant);
    }

    @Nested
    @DisplayName("POST /api/products/{productId}/variants - Create Product Variant")
    class CreateProductVariantTests {

        @Test
        @DisplayName("Should create variant with valid data")
        void testCreateVariantSuccess() {
            ProductAttribute attribute = attributes.getFirst();

            CreateProductAttributeValueDto attributeDto = new CreateProductAttributeValueDto(
                    attribute.getId(),
                    "Red"
            );
            CreateProductVariantDto createVariantDto = new CreateProductVariantDto(
                    "New Variant",
                    BigDecimal.valueOf(25),
                    List.of(attributeDto)
            );

            ProductVariantDto body = restTestClient.post()
                                                   .uri(VARIANTS_URL_TEMPLATE, testProductId)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .body(createVariantDto)
                                                   .exchange()
                                                   .expectStatus()
                                                   .isCreated()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("New Variant");
            assertThat(body.price()).isEqualByComparingTo(BigDecimal.valueOf(25));
            assertThat(body.attributes()).hasSize(1);
            assertThat(body.attributes()
                           .getFirst()
                           .value()).isEqualTo("Red");
            assertThat(body.id()).isGreaterThan(0);
        }

        @Test
        @DisplayName("Should create variant with multiple attribute values")
        void testCreateVariantWithMultipleAttributes() {
            CreateProductAttributeValueDto colorDto = new CreateProductAttributeValueDto(
                    attributes.get(0)
                              .getId(),
                    "Green"
            );
            CreateProductAttributeValueDto sizeDto = new CreateProductAttributeValueDto(
                    attributes.get(1)
                              .getId(),
                    "Medium"
            );
            CreateProductVariantDto createVariantDto = new CreateProductVariantDto(
                    "Multi-Attr Variant",
                    BigDecimal.valueOf(30),
                    List.of(colorDto, sizeDto)
            );

            ProductVariantDto body = restTestClient.post()
                                                   .uri(VARIANTS_URL_TEMPLATE, testProductId)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .body(createVariantDto)
                                                   .exchange()
                                                   .expectStatus()
                                                   .isCreated()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.attributes()).hasSize(2);
            assertThat(body.attributes()).extracting(ProductAttributeValueDto::value)
                                         .contains("Green", "Medium");
        }

        @Test
        @DisplayName("Should create variant without attributes")
        void testCreateVariantWithoutAttributes() {
            CreateProductVariantDto createVariantDto = new CreateProductVariantDto(
                    "No-Attr Variant",
                    BigDecimal.valueOf(20),
                    List.of()
            );

            ProductVariantDto body = restTestClient.post()
                                                   .uri(VARIANTS_URL_TEMPLATE, testProductId)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .body(createVariantDto)
                                                   .exchange()
                                                   .expectStatus()
                                                   .isCreated()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("No-Attr Variant");
            assertThat(body.attributes()).isEmpty();
        }

        @Test
        @DisplayName("Should return 400 when name is null")
        void testCreateVariantMissingName() {
            ProductAttribute attribute = product.getProductAttributes()
                                                .getFirst();

            CreateProductAttributeValueDto attributeDto = new CreateProductAttributeValueDto(
                    attribute.getId(),
                    "Red"
            );
            CreateProductVariantDto createVariantDto = new CreateProductVariantDto(
                    null,
                    BigDecimal.valueOf(25),
                    List.of(attributeDto)
            );

            restTestClient.post()
                          .uri(VARIANTS_URL_TEMPLATE, testProductId)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createVariantDto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when price is null")
        void testCreateVariantMissingPrice() {
            ProductAttribute attribute = product.getProductAttributes()
                                                .getFirst();

            CreateProductAttributeValueDto attributeDto = new CreateProductAttributeValueDto(
                    attribute.getId(),
                    "Red"
            );
            CreateProductVariantDto createVariantDto = new CreateProductVariantDto(
                    "Test Variant",
                    null,
                    List.of(attributeDto)
            );

            restTestClient.post()
                          .uri(VARIANTS_URL_TEMPLATE, testProductId)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createVariantDto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when price is negative")
        void testCreateVariantNegativePrice() {
            CreateProductVariantDto createVariantDto = new CreateProductVariantDto(
                    "Test Variant",
                    BigDecimal.valueOf(-5),
                    List.of()
            );

            restTestClient.post()
                          .uri(VARIANTS_URL_TEMPLATE, testProductId)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createVariantDto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 400 when attribute id is null")
        void testCreateVariantWithAttributeMissingId() {
            CreateProductAttributeValueDto attributeDto = new CreateProductAttributeValueDto(
                    null,
                    "Red"
            );
            CreateProductVariantDto createVariantDto = new CreateProductVariantDto(
                    "Test Variant",
                    BigDecimal.valueOf(25),
                    List.of(attributeDto)
            );

            restTestClient.post()
                          .uri(VARIANTS_URL_TEMPLATE, testProductId)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createVariantDto)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 404 when product does not exist")
        void testCreateVariantWithNonExistentProduct() {
            CreateProductVariantDto createVariantDto = new CreateProductVariantDto(
                    "Test Variant",
                    BigDecimal.valueOf(25),
                    List.of()
            );

            restTestClient.post()
                          .uri(VARIANTS_URL_TEMPLATE, 99999L)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(createVariantDto)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

    }

    @Nested
    @DisplayName("GET /api/products/{productId}/variants/{variantId} - Get Product Variant By ID")
    class GetProductVariantByIdTests {

        @Test
        @DisplayName("Should retrieve variant by id")
        void testGetVariantByIdSuccess() {
            ProductVariant variant = createTestVariant();

            ProductVariantDto body = restTestClient.get()
                                                   .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                           variant.getId())
                                                   .exchange()
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isEqualTo(variant.getId());
            assertThat(body.name()).isEqualTo("Test Variant");
            assertThat(body.price()).isEqualByComparingTo(BigDecimal.valueOf(10));
            assertThat(body.attributes()).hasSize(1);
        }

        @Test
        @DisplayName("Should retrieve variant with multiple attribute values")
        void testGetVariantIncludesMultipleAttributes() {
            ProductVariant variant = createTestVariantWithMultipleAttributes();

            ProductVariantDto body = restTestClient.get()
                                                   .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                           variant.getId())
                                                   .exchange()
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isEqualTo(variant.getId());
            assertThat(body.attributes()).hasSize(2);
        }

        @Test
        @DisplayName("Should return empty inventory list when no inventory records exist for variant")
        void testGetVariantIncludesEmptyInventoryList() {
            ProductVariant variant = createTestVariant();

            ProductVariantDto body = restTestClient.get()
                                                   .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                           variant.getId())
                                                   .exchange()
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.inventory()).isNotNull();
            assertThat(body.inventory()).isEmpty();
        }

        @Test
        @DisplayName("Should include inventory records in response")
        void testGetVariantIncludesInventoryRecords() {
            ProductVariant variant = createTestVariant();

            Storage storageA = storageRepository.save(new Storage("Test Storage A"));
            Storage storageB = storageRepository.save(new Storage("Test Storage B"));

            productInventoryRepository.save(new ProductInventory(variant, storageA, 100));
            productInventoryRepository.save(new ProductInventory(variant, storageB, 50));

            ProductVariantDto body = restTestClient.get()
                                                   .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                           variant.getId())
                                                   .exchange()
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.inventory()).hasSize(2);

            ProductInventoryDto inventoryRecord1 = body.inventory()
                                                       .get(0);
            ProductInventoryDto inventoryRecord2 = body.inventory()
                                                       .get(1);

            if (Objects.equals(inventoryRecord1.storageId(), storageA.getId())) {
                assertThat(inventoryRecord1.storageName()).isEqualTo(storageA.getName());
                assertThat(inventoryRecord1.quantity()).isEqualTo(100);

                assertThat(inventoryRecord2.storageId()).isEqualTo(storageB.getId());
                assertThat(inventoryRecord2.storageName()).isEqualTo(storageB.getName());
                assertThat(inventoryRecord2.quantity()).isEqualTo(50);
            } else {
                assertThat(inventoryRecord1.storageId()).isEqualTo(storageB.getId());
                assertThat(inventoryRecord1.storageName()).isEqualTo(storageB.getName());
                assertThat(inventoryRecord1.quantity()).isEqualTo(50);

                assertThat(inventoryRecord2.storageId()).isEqualTo(storageA.getId());
                assertThat(inventoryRecord2.storageName()).isEqualTo(storageA.getName());
                assertThat(inventoryRecord2.quantity()).isEqualTo(100);
            }
        }

        @Test
        @DisplayName("Should return 404 when variant does not exist")
        void testGetVariantByIdNotFound() {
            restTestClient.get()
                          .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId, 99999L)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("Should return 404 when product does not exist")
        void testGetVariantWithNonExistentProduct() {
            ProductVariant variant = createTestVariant();
            restTestClient.get()
                          .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", 99999L, variant.getId())
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

    }

    @Nested
    @DisplayName("GET /api/products/{productId}/variants - Get All Product Variants")
    class GetAllProductVariantsTests {

        @Test
        @DisplayName("Should return empty list when no variants exist")
        void testGetAllVariantsEmpty() {
            List<ProductVariantDto> body = restTestClient.get()
                                                         .uri(VARIANTS_URL_TEMPLATE, testProductId)
                                                         .exchange()
                                                         .expectStatus()
                                                         .isOk()
                                                         .returnResult(
                                                                 new ParameterizedTypeReference<List<ProductVariantDto>>() {
                                                                 })
                                                         .getResponseBody();

            assertThat(body).isEmpty();
        }

        @Test
        @DisplayName("Should return list of all variants")
        void testGetAllVariantsSuccess() {
            createTestVariant();
            createTestVariantWithMultipleAttributes();

            ProductVariant variant3 = new ProductVariant(product, "Third Variant", BigDecimal.valueOf(35));
            productVariantRepository.save(variant3);

            List<ProductVariantDto> body = restTestClient.get()
                                                         .uri(VARIANTS_URL_TEMPLATE, testProductId)
                                                         .exchange()
                                                         .expectStatus()
                                                         .isOk()
                                                         .returnResult(
                                                                 new ParameterizedTypeReference<List<ProductVariantDto>>() {
                                                                 })
                                                         .getResponseBody();

            assertThat(body).hasSize(3);
            assertThat(body).extracting(ProductVariantDto::name)
                            .contains("Test Variant", "Multi-Attribute Variant", "Third Variant");
        }

        @Test
        @DisplayName("Should return 404 when product does not exist")
        void testGetAllVariantsProductNotFound() {
            restTestClient.get()
                          .uri(VARIANTS_URL_TEMPLATE, 99999L)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

    }

    @Nested
    @DisplayName("PATCH /api/products/{productId}/variants/{variantId} - Update Product Variant")
    class UpdateProductVariantTests {

        private Long variantId;

        @BeforeEach
        void setup() {
            ProductVariant variant = createTestVariant();
            variantId = variant.getId();
        }

        @Test
        @DisplayName("Should update variant name")
        void testUpdateVariantNameSuccess() {
            UpdateProductVariantDto updateVariantDto = new UpdateProductVariantDto(
                    "Updated Name",
                    null,
                    null
            );

            ProductVariantDto body = restTestClient.patch()
                                                   .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                           variantId)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .body(updateVariantDto)
                                                   .exchange()
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("Updated Name");
            assertThat(body.id()).isEqualTo(variantId);
        }

        @Test
        @DisplayName("Should update variant price")
        void testUpdateVariantPriceSuccess() {
            UpdateProductVariantDto updateVariantDto = new UpdateProductVariantDto(
                    null,
                    BigDecimal.valueOf(99.99),
                    null
            );

            ProductVariantDto body = restTestClient.patch()
                                                   .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                           variantId)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .body(updateVariantDto)
                                                   .exchange()
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.price()).isEqualByComparingTo(BigDecimal.valueOf(99.99));
        }

        @Test
        @DisplayName("Should update both name and price")
        void testUpdateVariantNameAndPriceSuccess() {
            UpdateProductVariantDto updateVariantDto = new UpdateProductVariantDto(
                    "New Name",
                    BigDecimal.valueOf(50),
                    null
            );

            ProductVariantDto body = restTestClient.patch()
                                                   .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                           variantId)
                                                   .contentType(MediaType.APPLICATION_JSON)
                                                   .body(updateVariantDto)
                                                   .exchange()
                                                   .expectStatus()
                                                   .isOk()
                                                   .returnResult(ProductVariantDto.class)
                                                   .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.name()).isEqualTo("New Name");
            assertThat(body.price()).isEqualByComparingTo(BigDecimal.valueOf(50));
        }

        @Test
        @DisplayName("Should update variant attribute value")
        void testUpdateVariantAttributeValue() {
            ProductAttribute attribute = attributes.getFirst();

            ProductAttributeValueChangeDto attributeChange = new ProductAttributeValueChangeDto(
                    attribute.getId(),
                    "Blue"
            );
            UpdateProductVariantDto updateVariantDto = new UpdateProductVariantDto(
                    null,
                    null,
                    List.of(attributeChange)
            );

            ProductVariantDto updatedVariant = restTestClient.patch()
                                                             .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                                     variantId)
                                                             .contentType(MediaType.APPLICATION_JSON)
                                                             .body(updateVariantDto)
                                                             .exchange()
                                                             .expectStatus()
                                                             .isOk()
                                                             .returnResult(ProductVariantDto.class)
                                                             .getResponseBody();

            assertThat(updatedVariant).isNotNull();
            assertThat(updatedVariant.attributes()).hasSize(1);
            assertThat(updatedVariant.attributes()
                                     .getFirst()
                                     .value()).isEqualTo("Blue");
        }

        @Test
        @DisplayName("Should update variant with multiple attribute values")
        void testUpdateVariantWithMultipleAttributes() {
            ProductAttributeValueChangeDto colorChange = new ProductAttributeValueChangeDto(
                    attributes.get(0)
                              .getId(),
                    "Purple"
            );
            ProductAttributeValueChangeDto sizeChange = new ProductAttributeValueChangeDto(
                    attributes.get(1)
                              .getId(),
                    "Small"
            );
            UpdateProductVariantDto updateVariantDto = new UpdateProductVariantDto(
                    null,
                    null,
                    List.of(colorChange, sizeChange)
            );

            ProductVariantDto updatedVariant = restTestClient.patch()
                                                             .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                                     variantId)
                                                             .contentType(MediaType.APPLICATION_JSON)
                                                             .body(updateVariantDto)
                                                             .exchange()
                                                             .expectStatus()
                                                             .isOk()
                                                             .returnResult(ProductVariantDto.class)
                                                             .getResponseBody();

            assertThat(updatedVariant).isNotNull();
            assertThat(updatedVariant.attributes()).hasSize(2);
            assertThat(updatedVariant.attributes()).extracting(ProductAttributeValueDto::value)
                                                   .contains("Purple", "Small");
        }

        @Test
        @DisplayName("Should remove all attributes when empty list provided")
        void testUpdateVariantRemoveAttributes() {
            UpdateProductVariantDto updateVariantDto = new UpdateProductVariantDto(
                    null,
                    null,
                    List.of()
            );

            ProductVariantDto updatedVariant = restTestClient.patch()
                                                             .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId,
                                                                     variantId)
                                                             .contentType(MediaType.APPLICATION_JSON)
                                                             .body(updateVariantDto)
                                                             .exchange()
                                                             .expectStatus()
                                                             .isOk()
                                                             .returnResult(ProductVariantDto.class)
                                                             .getResponseBody();

            assertThat(updatedVariant).isNotNull();
            assertThat(updatedVariant.attributes()).isEmpty();
        }

        @Test
        @DisplayName("Should return 404 when variant does not exist")
        void testUpdateVariantNotFound() {
            UpdateProductVariantDto updateVariantDto = new UpdateProductVariantDto(
                    "Updated Name",
                    null,
                    null
            );

            restTestClient.patch()
                          .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId, 99999L)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(updateVariantDto)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("Should return 404 when product does not exist")
        void testUpdateVariantProductNotFound() {
            UpdateProductVariantDto updateVariantDto = new UpdateProductVariantDto(
                    "Updated Name",
                    null,
                    null
            );

            restTestClient.patch()
                          .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", 99999L, variantId)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(updateVariantDto)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

    }

    @Nested
    @DisplayName("DELETE /api/products/{productId}/variants/{variantId} - Delete Product Variant")
    class DeleteProductVariantTests {

        private Long variantId;

        @BeforeEach
        void setup() {
            ProductVariant variant = createTestVariant();
            variantId = variant.getId();
        }

        @Test
        @DisplayName("Should delete variant successfully")
        void testDeleteVariantSuccess() {
            restTestClient.delete()
                          .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId, variantId)
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            assertThat(productVariantRepository.existsById(variantId)).isFalse();
        }

        @Test
        @DisplayName("Should do nothing when deleting non-existent variant")
        void testDeleteVariantNotFound() {
            restTestClient.delete()
                          .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", testProductId, 99999L)
                          .exchange()
                          .expectStatus()
                          .isNoContent();
        }

        @Test
        @DisplayName("Should return 404 when product does not exist")
        void testDeleteVariantProductNotFound() {
            restTestClient.delete()
                          .uri(VARIANTS_URL_TEMPLATE + "/{variantId}", 99999L, variantId)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

    }

}

