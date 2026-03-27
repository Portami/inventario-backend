package ch.portami.inventorybackend.products;

import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.products.dto.ProductPatchRequest;
import ch.portami.inventorybackend.products.dto.ProductRequest;
import ch.portami.inventorybackend.products.model.Color;
import ch.portami.inventorybackend.products.model.Product;
import ch.portami.inventorybackend.products.model.ProductType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductControllerIntegrationTest {


    @Autowired
    private ProductController productController;

    private RestTestClient restTestClient;

    @Autowired
    ProductRepository productRepository;

    @BeforeEach
    void beforeEach() {
        restTestClient = RestTestClient.bindToController(productController)
                .build();
    }

    private static final ProductRequest VALID_REQUEST = new ProductRequest(
            "Premium Felt Sheet",
            "ART-00123",
            ProductType.WOOL,
            Color.RED,
            5,
            200
    );

    @BeforeEach
    void resetStore() {
        productRepository.findAll().forEach(p -> productRepository.deleteById(p.getId()));
    }

    @Nested
    @DisplayName("GET /products")
    class GetAllProducts {

        @Test
        @DisplayName("returns empty list when no products exist")
        void returnsEmptyList() {
            webTestClient.get().uri("/products")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Product.class)
                    .hasSize(0);
        }

        @Test
        @DisplayName("returns all products when no filter is applied")
        void returnsAllProducts() {
            createProduct(VALID_REQUEST);
            createProduct(new ProductRequest("Synthetic Sheet", "ART-00124",
                    ProductType.SYNTHETIC, Color.BLUE, 3, 150));

            webTestClient.get().uri("/products")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Product.class)
                    .hasSize(2);
        }

        @Test
        @DisplayName("filters by type correctly")
        void filtersByType() {
            createProduct(VALID_REQUEST);
            createProduct(new ProductRequest("Synthetic Sheet", "ART-00124",
                    ProductType.SYNTHETIC, Color.BLUE, 3, 150));

            List<Product> body = webTestClient.get()
                    .uri(u -> u.path("/products").queryParam("type", "WOOL").build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Product.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(body)
                    .hasSize(1)
                    .allMatch(p -> p.getType() == ProductType.WOOL);
        }

        @Test
        @DisplayName("filters by color correctly")
        void filtersByColor() {
            createProduct(VALID_REQUEST);
            createProduct(new ProductRequest("Blue Sheet", "ART-00125",
                    ProductType.BLENDED, Color.BLUE, 4, 180));

            List<Product> body = webTestClient.get()
                    .uri(u -> u.path("/products").queryParam("color", "BLUE").build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Product.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(body)
                    .hasSize(1)
                    .allMatch(p -> p.getColor() == Color.BLUE);
        }

        @Test
        @DisplayName("filters by both type and color")
        void filtersByTypeAndColor() {
            createProduct(VALID_REQUEST);                                             // WOOL + RED
            createProduct(new ProductRequest("Wool Blue",  "ART-00126",
                    ProductType.WOOL,      Color.BLUE, 5, 200));                     // WOOL + BLUE
            createProduct(new ProductRequest("Synth Red",  "ART-00127",
                    ProductType.SYNTHETIC, Color.RED,  3, 150));                     // SYNTHETIC + RED

            List<Product> body = webTestClient.get()
                    .uri(u -> u.path("/products")
                            .queryParam("type",  "WOOL")
                            .queryParam("color", "RED")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Product.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(body)
                    .hasSize(1)
                    .first()
                    .extracting(Product::getArticleNumber)
                    .isEqualTo("ART-00123");
        }

        @Test
        @DisplayName("returns empty list when filter matches nothing")
        void filterMatchesNothing() {
            createProduct(VALID_REQUEST); // WOOL

            webTestClient.get()
                    .uri(u -> u.path("/products").queryParam("type", "INDUSTRIAL").build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBodyList(Product.class)
                    .hasSize(0);
        }
    }

    @Nested
    @DisplayName("POST /products")
    class CreateProduct {

        @Test
        @DisplayName("creates product and returns 201 with full body")
        void createsProduct() {
            webTestClient.post().uri("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_REQUEST)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Product.class)
                    .value(p -> {
                        assertThat(p.getId()).isGreaterThan(0);
                        assertThat(p.getArticleNumber()).isEqualTo("ART-00123");
                        assertThat(p.getName()).isEqualTo("Premium Felt Sheet");
                        assertThat(p.getType()).isEqualTo(ProductType.WOOL);
                        assertThat(p.getColor()).isEqualTo(Color.RED);
                        assertThat(p.getThickness()).isEqualTo(5);
                        assertThat(p.getDensity()).isEqualTo(200);
                    });
        }

        @Test
        @DisplayName("returns 400 when articleNumber is missing")
        void rejectsMissingArticleNumber() {
            var invalid = new ProductRequest("Name", null, ProductType.WOOL, Color.RED, 5, 200);

            webTestClient.post().uri("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalid)
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

            webTestClient.post().uri("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("type"));
        }

        @Test
        @DisplayName("returns 400 when color is missing")
        void rejectsMissingColor() {
            var invalid = new ProductRequest("Name", "ART-001", ProductType.WOOL, null, 5, 200);

            webTestClient.post().uri("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("color"));
        }

        @Test
        @DisplayName("creates product with only required fields")
        void createsWithOnlyRequiredFields() {
            var minimal = new ProductRequest(null, "ART-MIN", ProductType.WOOL, Color.GREEN, null, null);

            webTestClient.post().uri("/products")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(minimal)
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(Product.class)
                    .value(p -> {
                        assertThat(p.getId()).isGreaterThan(0);
                        assertThat(p.getArticleNumber()).isEqualTo("ART-MIN");
                    });
        }
    }

    @Nested
    @DisplayName("GET /products/{id}")
    class GetProductById {

        @Test
        @DisplayName("returns product when it exists")
        void returnsExistingProduct() {
            int id = createProductAndGetId(VALID_REQUEST);

            webTestClient.get().uri("/products/{id}", id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .value(p -> {
                        assertThat(p.getId()).isEqualTo(id);
                        assertThat(p.getArticleNumber()).isEqualTo("ART-00123");
                    });
        }

        @Test
        @DisplayName("returns 404 when product does not exist")
        void returns404ForMissingProduct() {
            webTestClient.get().uri("/products/{id}", 9999)
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
    @DisplayName("PUT /products/{id}")
    class UpdateProduct {

        @Test
        @DisplayName("fully replaces an existing product")
        void replacesProduct() {
            int id = createProductAndGetId(VALID_REQUEST);

            var replacement = new ProductRequest(
                    "Industrial Sheet", "ART-IND-01",
                    ProductType.INDUSTRIAL, Color.OTHER, 10, 300);

            webTestClient.put().uri("/products/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(replacement)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .value(p -> {
                        assertThat(p.getId()).isEqualTo(id);
                        assertThat(p.getName()).isEqualTo("Industrial Sheet");
                        assertThat(p.getArticleNumber()).isEqualTo("ART-IND-01");
                        assertThat(p.getType()).isEqualTo(ProductType.INDUSTRIAL);
                        assertThat(p.getColor()).isEqualTo(Color.OTHER);
                    });
        }

        @Test
        @DisplayName("returns 404 when product does not exist")
        void returns404ForMissingProduct() {
            webTestClient.put().uri("/products/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(VALID_REQUEST)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("returns 400 when required fields are missing")
        void returns400ForInvalidBody() {
            int id = createProductAndGetId(VALID_REQUEST);
            var invalid = new ProductRequest(null, null, null, null, null, null);

            webTestClient.put().uri("/products/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.BAD_REQUEST.value()));
        }
    }

    @Nested
    @DisplayName("PATCH /products/{id}")
    class PatchProduct {

        @Test
        @DisplayName("updates only the provided fields")
        void patchesPartialFields() {
            int id = createProductAndGetId(VALID_REQUEST);

            var patch = new ProductPatchRequest("Updated Name", null, null, null, null, null);

            webTestClient.patch().uri("/products/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patch)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .value(p -> {
                        assertThat(p.getName()).isEqualTo("Updated Name");
                        assertThat(p.getArticleNumber()).isEqualTo("ART-00123"); // unchanged
                        assertThat(p.getType()).isEqualTo(ProductType.WOOL);     // unchanged
                        assertThat(p.getColor()).isEqualTo(Color.RED);           // unchanged
                    });
        }

        @Test
        @DisplayName("no-op patch leaves product unchanged")
        void noOpPatchLeavesProductUnchanged() {
            int id = createProductAndGetId(VALID_REQUEST);

            var emptyPatch = new ProductPatchRequest(null, null, null, null, null, null);

            webTestClient.patch().uri("/products/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(emptyPatch)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(Product.class)
                    .value(p -> {
                        assertThat(p.getArticleNumber()).isEqualTo("ART-00123");
                        assertThat(p.getType()).isEqualTo(ProductType.WOOL);
                        assertThat(p.getColor()).isEqualTo(Color.RED);
                    });
        }

        @Test
        @DisplayName("returns 404 when product does not exist")
        void returns404ForMissingProduct() {
            var patch = new ProductPatchRequest("New Name", null, null, null, null, null);

            webTestClient.patch().uri("/products/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(patch)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("DELETE /products/{id}")
    class DeleteProduct {

        @Test
        @DisplayName("deletes an existing product and returns 204")
        void deletesExistingProduct() {
            int id = createProductAndGetId(VALID_REQUEST);

            webTestClient.delete().uri("/products/{id}", id)
                    .exchange()
                    .expectStatus().isNoContent();

            // confirm it is truly gone
            webTestClient.get().uri("/products/{id}", id)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("returns 404 when product does not exist")
        void returns404ForMissingProduct() {
            webTestClient.delete().uri("/products/{id}", 9999)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).contains("9999");
                    });
        }
    }

    private int createProductAndGetId(ProductRequest request) {
        Product body = webTestClient.post().uri("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
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
