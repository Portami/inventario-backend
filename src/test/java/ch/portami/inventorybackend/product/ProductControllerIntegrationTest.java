package ch.portami.inventorybackend.product;

import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.product.dto.ProductPatchRequest;
import ch.portami.inventorybackend.product.dto.ProductRequest;
import ch.portami.inventorybackend.product.model.Color;
import ch.portami.inventorybackend.product.model.Product;
import ch.portami.inventorybackend.product.model.ProductType;
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
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import java.util.List;

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

    private static final String PRODUCTS_URI = "/api/products";

    private static final ProductRequest VALID_REQUEST = new ProductRequest(
            "Premium Felt Sheet",
            "ART-00123",
            ProductType.WOOL,
            Color.RED,
            5,
            200
    );

    private static final ParameterizedTypeReference<List<Product>> PRODUCT_LIST =
            new ParameterizedTypeReference<>() {};

    @Autowired
    private RestTestClient restTestClient;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void resetStore() {
        productRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/products")
    class GetAllProducts {

        @Test
        @DisplayName("returns empty list when no products exist")
        void returnsEmptyList() {
            restTestClient.get().uri(PRODUCTS_URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PRODUCT_LIST)
                    .value(products -> assertThat(products).isEmpty());
        }

        @Test
        @DisplayName("returns all products when no filter is applied")
        void returnsAllProducts() {
            createProduct(VALID_REQUEST);
            createProduct(new ProductRequest(
                    "Synthetic Sheet",
                    "ART-00124",
                    ProductType.SYNTHETIC,
                    Color.BLUE,
                    3,
                    150));

            restTestClient.get().uri(PRODUCTS_URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PRODUCT_LIST)
                    .value(products -> assertThat(products).hasSize(2));
        }

        @Test
        @DisplayName("filters by type correctly")
        void filtersByType() {
            createProduct(VALID_REQUEST);
            createProduct(new ProductRequest(
                    "Synthetic Sheet",
                    "ART-00124",
                    ProductType.SYNTHETIC,
                    Color.BLUE,
                    3,
                    150));

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(PRODUCTS_URI).queryParam("type", "WOOL").build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PRODUCT_LIST)
                    .value(products -> assertThat(products)
                            .hasSize(1)
                            .allMatch(product -> product.getType() == ProductType.WOOL));
        }

        @Test
        @DisplayName("filters by color correctly")
        void filtersByColor() {
            createProduct(VALID_REQUEST);
            createProduct(new ProductRequest(
                    "Blue Sheet",
                    "ART-00125",
                    ProductType.BLENDED,
                    Color.BLUE,
                    4,
                    180));

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(PRODUCTS_URI).queryParam("color", "BLUE").build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PRODUCT_LIST)
                    .value(products -> assertThat(products)
                            .hasSize(1)
                            .allMatch(product -> product.getColor() == Color.BLUE));
        }

        @Test
        @DisplayName("filters by both type and color")
        void filtersByTypeAndColor() {
            createProduct(VALID_REQUEST);
            createProduct(new ProductRequest(
                    "Wool Blue",
                    "ART-00126",
                    ProductType.WOOL,
                    Color.BLUE,
                    5,
                    200));
            createProduct(new ProductRequest(
                    "Synth Red",
                    "ART-00127",
                    ProductType.SYNTHETIC,
                    Color.RED,
                    3,
                    150));

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(PRODUCTS_URI)
                            .queryParam("type", "WOOL")
                            .queryParam("color", "RED")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PRODUCT_LIST)
                    .value(products -> assertThat(products)
                            .hasSize(1)
                            .first()
                            .extracting(Product::getArticleNumber)
                            .isEqualTo("ART-00123"));
        }

        @Test
        @DisplayName("returns empty list when filter matches nothing")
        void filterMatchesNothing() {
            createProduct(VALID_REQUEST);

            restTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path(PRODUCTS_URI).queryParam("type", "INDUSTRIAL").build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(PRODUCT_LIST)
                    .value(products -> assertThat(products).isEmpty());
        }
    }

    @Nested
    @DisplayName("POST /api/products")
    class CreateProduct {

        @Test
        @DisplayName("creates product and returns 201 with full body")
        void createsProduct() {
            restTestClient.post().uri(PRODUCTS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(VALID_REQUEST)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Product.class)
                    .value(product -> {
                        assertThat(product.getId()).isGreaterThan(0);
                        assertThat(product.getArticleNumber()).isEqualTo("ART-00123");
                        assertThat(product.getName()).isEqualTo("Premium Felt Sheet");
                        assertThat(product.getType()).isEqualTo(ProductType.WOOL);
                        assertThat(product.getColor()).isEqualTo(Color.RED);
                        assertThat(product.getThickness()).isEqualTo(5);
                        assertThat(product.getDensity()).isEqualTo(200);
                    });
        }

        @Test
        @DisplayName("returns 400 when articleNumber is missing")
        void rejectsMissingArticleNumber() {
            var invalid = new ProductRequest("Name", null, ProductType.WOOL, Color.RED, 5, 200);

            restTestClient.post().uri(PRODUCTS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(err.message()).contains("articleNumber");
                    });
        }

        @Test
        @DisplayName("returns 400 when type is missing")
        void rejectsMissingType() {
            var invalid = new ProductRequest("Name", "ART-001", null, Color.RED, 5, 200);

            restTestClient.post().uri(PRODUCTS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("type"));
        }

        @Test
        @DisplayName("returns 400 when color is missing")
        void rejectsMissingColor() {
            var invalid = new ProductRequest("Name", "ART-001", ProductType.WOOL, null, 5, 200);

            restTestClient.post().uri(PRODUCTS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("color"));
        }

        @Test
        @DisplayName("creates product with only required fields")
        void createsWithOnlyRequiredFields() {
            var minimal = new ProductRequest(null, "ART-MIN", ProductType.WOOL, Color.GREEN, null, null);

            restTestClient.post().uri(PRODUCTS_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(minimal)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Product.class)
                    .value(product -> {
                        assertThat(product.getId()).isGreaterThan(0);
                        assertThat(product.getArticleNumber()).isEqualTo("ART-MIN");
                    });
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetProductById {

        @Test
        @DisplayName("returns product when it exists")
        void returnsExistingProduct() {
            Integer id = createProductAndGetId(VALID_REQUEST);

            restTestClient.get().uri(PRODUCTS_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .value(product -> {
                        assertThat(product.getId()).isEqualTo(id);
                        assertThat(product.getArticleNumber()).isEqualTo("ART-00123");
                    });
        }

        @Test
        @DisplayName("returns 404 when product does not exist")
        void returns404ForMissingProduct() {
            restTestClient.get().uri(PRODUCTS_URI + "/{id}", 9999)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).contains("9999");
                    });
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class UpdateProduct {

        @Test
        @DisplayName("fully replaces an existing product")
        void replacesProduct() {
            Integer id = createProductAndGetId(VALID_REQUEST);

            var replacement = new ProductRequest(
                    "Industrial Sheet",
                    "ART-IND-01",
                    ProductType.INDUSTRIAL,
                    Color.OTHER,
                    10,
                    300);

            restTestClient.put().uri(PRODUCTS_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(replacement)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .value(product -> {
                        assertThat(product.getId()).isEqualTo(id);
                        assertThat(product.getName()).isEqualTo("Industrial Sheet");
                        assertThat(product.getArticleNumber()).isEqualTo("ART-IND-01");
                        assertThat(product.getType()).isEqualTo(ProductType.INDUSTRIAL);
                        assertThat(product.getColor()).isEqualTo(Color.OTHER);
                    });
        }

        @Test
        @DisplayName("returns 404 when product does not exist")
        void returns404ForMissingProduct() {
            restTestClient.put().uri(PRODUCTS_URI + "/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(VALID_REQUEST)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("returns 400 when required fields are missing")
        void returns400ForInvalidBody() {
            Integer id = createProductAndGetId(VALID_REQUEST);
            var invalid = new ProductRequest(null, null, null, null, null, null);

            restTestClient.put().uri(PRODUCTS_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Nested
    @DisplayName("PATCH /api/products/{id}")
    class PatchProduct {

        @Test
        @DisplayName("updates only the provided fields")
        void patchesPartialFields() {
            Integer id = createProductAndGetId(VALID_REQUEST);
            var patch = new ProductPatchRequest("Updated Name", null, null, null, null, null);

            restTestClient.patch().uri(PRODUCTS_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(patch)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .value(product -> {
                        assertThat(product.getName()).isEqualTo("Updated Name");
                        assertThat(product.getArticleNumber()).isEqualTo("ART-00123");
                        assertThat(product.getType()).isEqualTo(ProductType.WOOL);
                        assertThat(product.getColor()).isEqualTo(Color.RED);
                    });
        }

        @Test
        @DisplayName("no-op patch leaves product unchanged")
        void noOpPatchLeavesProductUnchanged() {
            Integer id = createProductAndGetId(VALID_REQUEST);
            var emptyPatch = new ProductPatchRequest(null, null, null, null, null, null);

            restTestClient.patch().uri(PRODUCTS_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(emptyPatch)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .value(product -> {
                        assertThat(product.getArticleNumber()).isEqualTo("ART-00123");
                        assertThat(product.getType()).isEqualTo(ProductType.WOOL);
                        assertThat(product.getColor()).isEqualTo(Color.RED);
                    });
        }

        @Test
        @DisplayName("returns 404 when product does not exist")
        void returns404ForMissingProduct() {
            var patch = new ProductPatchRequest("New Name", null, null, null, null, null);

            restTestClient.patch().uri(PRODUCTS_URI + "/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(patch)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class DeleteProduct {

        @Test
        @DisplayName("deletes an existing product and returns 204")
        void deletesExistingProduct() {
            Integer id = createProductAndGetId(VALID_REQUEST);

            restTestClient.delete().uri(PRODUCTS_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isNoContent();

            restTestClient.get().uri(PRODUCTS_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("returns 404 when product does not exist")
        void returns404ForMissingProduct() {
            restTestClient.delete().uri(PRODUCTS_URI + "/{id}", 9999)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).contains("9999");
                    });
        }
    }

    private Integer createProductAndGetId(ProductRequest request) {
        Product body = restTestClient.post().uri(PRODUCTS_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Product.class)
                .returnResult()
                .getResponseBody();

        assertThat(body).isNotNull();
        return body.getId();
    }

    private void createProduct(ProductRequest request) {
        createProductAndGetId(request);
    }
}