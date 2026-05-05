package ch.portami.inventorybackend.product;

import ch.portami.inventorybackend.core.entity.Storage;
import ch.portami.inventorybackend.core.repository.StorageRepository;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryDto;
import ch.portami.inventorybackend.product.dto.productinventory.UpdateProductInventoryDto;
import ch.portami.inventorybackend.product.entity.Category;
import ch.portami.inventorybackend.product.entity.Product;
import ch.portami.inventorybackend.product.entity.ProductInventory;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.repository.CategoryRepository;
import ch.portami.inventorybackend.product.repository.ProductInventoryRepository;
import ch.portami.inventorybackend.product.repository.ProductRepository;
import ch.portami.inventorybackend.product.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class ProductInventoryControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final String INVENTORY_CHANGES_URL = "/api/products/inventory/changes";

    private static Long variantId1;
    private static Long variantId2;
    private static Long storageId1;
    private static Long storageId2;

    private ProductVariant variant1;
    private Storage storage1;

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ProductVariantRepository productVariantRepository;

    @Autowired
    private StorageRepository storageRepository;

    @Autowired
    private ProductInventoryRepository productInventoryRepository;

    @BeforeAll
    static void beforeAll(@Autowired CategoryRepository categoryRepository,
            @Autowired ProductRepository productRepository,
            @Autowired ProductVariantRepository productVariantRepository,
            @Autowired StorageRepository storageRepository) {
        Category category = new Category("Test Category for Inventory");
        category = categoryRepository.save(category);

        Product product = new Product(category, "Test Product for Inventory");
        product = productRepository.save(product);

        ProductVariant variant1 = new ProductVariant(product, "Variant 1", BigDecimal.valueOf(10));
        variant1 = productVariantRepository.save(variant1);
        variantId1 = variant1.getId();

        ProductVariant variant2 = new ProductVariant(product, "Variant 2", BigDecimal.valueOf(20));
        variant2 = productVariantRepository.save(variant2);
        variantId2 = variant2.getId();

        Storage storage1 = new Storage("Test Storage 1");
        storage1 = storageRepository.save(storage1);
        storageId1 = storage1.getId();

        Storage storage2 = new Storage("Test Storage 2");
        storage2 = storageRepository.save(storage2);
        storageId2 = storage2.getId();
    }

    @BeforeEach
    void setUp(@Autowired PlatformTransactionManager transactionManager) {
        new TransactionTemplate(transactionManager).executeWithoutResult(_ -> {
            productInventoryRepository.deleteAll();
            variant1 = productVariantRepository.findById(variantId1)
                                               .orElseThrow();
            storage1 = storageRepository.findById(storageId1)
                                        .orElseThrow();
        });
    }

    @Nested
    @DisplayName("Only one inventory change")
    class SingleChangeTests {

        @Test
        @DisplayName("Should create inventory entry with positive quantity change when item not in storage")
        void testPositiveChangeItemNotInStorage() {
            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, 50);

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(List.of(change))
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(1);
            assertThat(response.getFirst()
                               .storageId()).isEqualTo(storageId1);
            assertThat(response.getFirst()
                               .storageName()).isEqualTo("Test Storage 1");
            assertThat(response.getFirst()
                               .quantity()).isEqualTo(50);

            Optional<ProductInventory> inventory = productInventoryRepository.findByProductVariantIdAndStorageId(
                    variantId1, storageId1);
            assertThat(inventory).isPresent();
            assertThat(inventory.get()
                                .getCount()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should update inventory entry with positive quantity change when item in storage")
        void testPositiveChangeItemInStorage() {
            productInventoryRepository.save(
                    new ProductInventory(variant1, storage1, 30));

            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, 20);

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(List.of(change))
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(1);
            assertThat(response.getFirst()
                               .quantity()).isEqualTo(50);

            Optional<ProductInventory> inventory = productInventoryRepository.findByProductVariantIdAndStorageId(
                    variantId1, storageId1);
            assertThat(inventory).isPresent();
            assertThat(inventory.get()
                                .getCount()).isEqualTo(50);
        }

        @Test
        @DisplayName("Should not create inventory entry when item not in storage and change is 0")
        void testZeroChangeItemNotInStorage() {
            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, 0);

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(List.of(change))
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(1);
            assertThat(response.getFirst()
                               .quantity()).isZero();

            Optional<ProductInventory> inventory = productInventoryRepository.findByProductVariantIdAndStorageId(
                    variantId1, storageId1);
            assertThat(inventory).isEmpty();
        }

        @Test
        @DisplayName("Should delete inventory entry when change results in 0 quantity")
        void testZeroChangeItemInStorage() {
            productInventoryRepository.save(new ProductInventory(variant1, storage1, 30));

            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, -30);

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(List.of(change))
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(1);
            assertThat(response.getFirst()
                               .quantity()).isZero();

            Optional<ProductInventory> inventory = productInventoryRepository.findByProductVariantIdAndStorageId(
                    variantId1, storageId1);
            assertThat(inventory).isEmpty();
        }

        @Test
        @DisplayName("Should return 409 when trying to decrease inventory below 0 with item not in storage")
        void testNegativeChangeItemNotInStorage() {
            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, -50);

            restTestClient.post()
                          .uri(INVENTORY_CHANGES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(List.of(change))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(409);

            Optional<ProductInventory> inventory = productInventoryRepository.findByProductVariantIdAndStorageId(
                    variantId1, storageId1);
            assertThat(inventory).isEmpty();
        }

        @Test
        @DisplayName("Should return 409 when trying to decrease inventory below 0 with insufficient stock")
        void testNegativeChangeInsufficientStock() {
            productInventoryRepository.save(new ProductInventory(variant1, storage1, 20));

            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, -50);

            restTestClient.post()
                          .uri(INVENTORY_CHANGES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(List.of(change))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(409);

            Optional<ProductInventory> inventory = productInventoryRepository.findByProductVariantIdAndStorageId(
                    variantId1, storageId1);
            assertThat(inventory).isPresent();
            assertThat(inventory.get()
                                .getCount()).isEqualTo(20);
        }

        @Test
        @DisplayName("Should decrease inventory when stock is sufficient")
        void testNegativeChangeWithSufficientStock() {
            productInventoryRepository.save(new ProductInventory(variant1, storage1, 100));

            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, -30);

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(List.of(change))
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(1);
            assertThat(response.getFirst()
                               .quantity()).isEqualTo(70);

            Optional<ProductInventory> inventory = productInventoryRepository.findByProductVariantIdAndStorageId(
                    variantId1, storageId1);
            assertThat(inventory).isPresent();
            assertThat(inventory.get()
                                .getCount()).isEqualTo(70);
        }

        @Test
        @DisplayName("Should delete inventory entry when negative change exactly empties the stock")
        void testNegativeChangeExactlyEmptyStock() {
            productInventoryRepository.save(new ProductInventory(variant1, storage1, 50));

            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, -50);

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(List.of(change))
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(1);
            assertThat(response.getFirst()
                               .quantity()).isZero();

            Optional<ProductInventory> inventory = productInventoryRepository.findByProductVariantIdAndStorageId(
                    variantId1, storageId1);
            assertThat(inventory).isEmpty();
        }

        @Test
        @DisplayName("Should return 400 when quantity change is null")
        void testNullQuantityChange() {
            List<UpdateProductInventoryDto> changes = List.of(
                    new UpdateProductInventoryDto(variantId1, storageId1, null)
            );

            restTestClient.post()
                          .uri(INVENTORY_CHANGES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(changes)
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should return 422 when storage does not exist")
        void testNonExistentStorage() {
            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, 99999L, 10);

            restTestClient.post()
                          .uri(INVENTORY_CHANGES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(List.of(change))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        }

        @Test
        @DisplayName("Should return 422 when product variant does not exist")
        void testNonExistentProductVariant() {
            UpdateProductInventoryDto change = new UpdateProductInventoryDto(99999L, storageId1, 10);

            restTestClient.post()
                          .uri(INVENTORY_CHANGES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(List.of(change))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        }

    }

    @Nested
    @DisplayName("Multiple changes in one request")
    class MultipleChangesTests {

        @Test
        @DisplayName("Should change inventory of multiple variants at once")
        void testMultipleVariantsAtOnce() {
            List<UpdateProductInventoryDto> changes = List.of(
                    new UpdateProductInventoryDto(variantId1, storageId1, 100),
                    new UpdateProductInventoryDto(variantId2, storageId1, 200)
            );

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(changes)
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(2);
            assertThat(response.get(0)
                               .quantity()).isEqualTo(100);
            assertThat(response.get(1)
                               .quantity()).isEqualTo(200);

            Optional<ProductInventory> inv1 = productInventoryRepository.findByProductVariantIdAndStorageId(variantId1,
                    storageId1);
            Optional<ProductInventory> inv2 = productInventoryRepository.findByProductVariantIdAndStorageId(variantId2,
                    storageId1);
            assertThat(inv1).isPresent()
                            .hasValueSatisfying(inv -> assertThat(inv.getCount()).isEqualTo(100));
            assertThat(inv2).isPresent()
                            .hasValueSatisfying(inv -> assertThat(inv.getCount()).isEqualTo(200));
        }

        @Test
        @DisplayName("Should change inventory of one variant in multiple storages")
        void testOneVariantMultipleStorages() {
            List<UpdateProductInventoryDto> changes = List.of(
                    new UpdateProductInventoryDto(variantId1, storageId1, 50),
                    new UpdateProductInventoryDto(variantId1, storageId2, 75)
            );

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(changes)
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(2);
            assertThat(response).extracting(ProductInventoryDto::quantity)
                                .contains(50, 75);

            Optional<ProductInventory> inv1 = productInventoryRepository.findByProductVariantIdAndStorageId(variantId1,
                    storageId1);
            Optional<ProductInventory> inv2 = productInventoryRepository.findByProductVariantIdAndStorageId(variantId1,
                    storageId2);
            assertThat(inv1).isPresent()
                            .hasValueSatisfying(inv -> assertThat(inv.getCount()).isEqualTo(50));
            assertThat(inv2).isPresent()
                            .hasValueSatisfying(inv -> assertThat(inv.getCount()).isEqualTo(75));
        }

        @Test
        @DisplayName("Should isolate inventory changes between storages")
        void testInventoryIsolationBetweenStorages() {
            Storage storage2 = storageRepository.findById(storageId2)
                                                .orElseThrow();

            productInventoryRepository.save(new ProductInventory(variant1, storage1, 100));
            productInventoryRepository.save(new ProductInventory(variant1, storage2, 50));

            UpdateProductInventoryDto change = new UpdateProductInventoryDto(variantId1, storageId1, -30);

            restTestClient.post()
                          .uri(INVENTORY_CHANGES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(List.of(change))
                          .exchange()
                          .expectStatus()
                          .isOk();

            Optional<ProductInventory> inv1 = productInventoryRepository.findByProductVariantIdAndStorageId(variantId1,
                    storageId1);
            Optional<ProductInventory> inv2 = productInventoryRepository.findByProductVariantIdAndStorageId(variantId1,
                    storageId2);
            assertThat(inv1).isPresent()
                            .hasValueSatisfying(inv -> assertThat(inv.getCount()).isEqualTo(70));
            assertThat(inv2).isPresent()
                            .hasValueSatisfying(inv -> assertThat(inv.getCount()).isEqualTo(50));
        }

        @Test
        @DisplayName("Should return responses in same order as input changes")
        void testResponseOrderMatchesInput() {
            List<UpdateProductInventoryDto> changes = List.of(
                    new UpdateProductInventoryDto(variantId1, storageId1, 10),
                    new UpdateProductInventoryDto(variantId2, storageId2, 20),
                    new UpdateProductInventoryDto(variantId1, storageId2, 30)
            );

            List<ProductInventoryDto> response = restTestClient.post()
                                                               .uri(INVENTORY_CHANGES_URL)
                                                               .contentType(MediaType.APPLICATION_JSON)
                                                               .body(changes)
                                                               .exchange()
                                                               .expectStatus()
                                                               .isOk()
                                                               .returnResult(
                                                                       new ParameterizedTypeReference<List<ProductInventoryDto>>() {
                                                                       })
                                                               .getResponseBody();

            assertThat(response).hasSize(3);
            assertThat(response.get(0)
                               .storageId()).isEqualTo(storageId1);
            assertThat(response.get(0)
                               .quantity()).isEqualTo(10);
            assertThat(response.get(1)
                               .storageId()).isEqualTo(storageId2);
            assertThat(response.get(1)
                               .quantity()).isEqualTo(20);
            assertThat(response.get(2)
                               .storageId()).isEqualTo(storageId2);
            assertThat(response.get(2)
                               .quantity()).isEqualTo(30);
        }

        @Test
        @DisplayName("Should rollback all changes if one change fails in a batch")
        void testAtomicityRollbackOnError() {
            productInventoryRepository.save(new ProductInventory(variant1, storage1, 100));

            List<UpdateProductInventoryDto> changes = List.of(
                    new UpdateProductInventoryDto(variantId1, storageId1, 50),
                    new UpdateProductInventoryDto(variantId2, storageId2, -100)
            );

            restTestClient.post()
                          .uri(INVENTORY_CHANGES_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(changes)
                          .exchange()
                          .expectStatus()
                          .isEqualTo(409);

            Optional<ProductInventory> inv1 = productInventoryRepository.findByProductVariantIdAndStorageId(variantId1,
                    storageId1);
            assertThat(inv1).isPresent();
            assertThat(inv1.get()
                           .getCount()).isEqualTo(100);  // Original value, not updated to 150
        }

    }

}
