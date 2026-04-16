package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.felt.dto.CreateFeltColorVariantRequest;
import ch.portami.inventorybackend.felt.dto.FeltColorVariantResponse;
import ch.portami.inventorybackend.felt.dto.UpdateFeltColorVariantRequest;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.FeltVariant;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.FeltVariantRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
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

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class FeltColorVariantControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final String BASE_URI = "/api/felt-color-variants";
    private static final ParameterizedTypeReference<List<FeltColorVariantResponse>> COLOR_VARIANT_LIST =
            new ParameterizedTypeReference<>() {};

    @Autowired private RestTestClient restTestClient;
    @Autowired private FeltColorVariantRepository feltColorVariantRepository;
    @Autowired private FeltVariantRepository feltVariantRepository;
    @Autowired private FeltRepository feltRepository;
    @Autowired private FeltTypeRepository feltTypeRepository;
    @Autowired private SupplierRepository supplierRepository;

    private Long feltVariantId;
    private Long feltId;

    @BeforeAll
    void setupHierarchy() {
        FeltType feltType = feltTypeRepository.save(new FeltType("Wool"));
        Supplier supplier = supplierRepository.save(new Supplier("Supplier A"));
        Felt felt = feltRepository.save(new Felt(feltType, supplier, "ART-001"));
        feltId = felt.getId();
        feltVariantId = feltVariantRepository.save(new FeltVariant(felt, 5.0, 300.0, new BigDecimal("12.99"))).getId();
    }

    @BeforeEach
    void resetColorVariants() {
        feltColorVariantRepository.deleteAll();
    }

    private CreateFeltColorVariantRequest validRequest() {
        return new CreateFeltColorVariantRequest(feltVariantId, "Red", "R-001");
    }

    private Long createColorVariantAndGetId(CreateFeltColorVariantRequest request) {
        FeltColorVariantResponse body = restTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeltColorVariantResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(body).isNotNull();
        return body.id();
    }

    private void createColorVariant(CreateFeltColorVariantRequest request) {
        createColorVariantAndGetId(request);
    }

    @Nested
    @DisplayName("GET /api/felt-color-variants")
    class GetAllFeltColorVariants {

        @Test
        @DisplayName("returns empty list when no color variants exist")
        void returnsEmptyList() {
            restTestClient.get().uri(BASE_URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(COLOR_VARIANT_LIST)
                    .value(colorVariants -> assertThat(colorVariants).isEmpty());
        }

        @Test
        @DisplayName("returns all color variants")
        void returnsAllColorVariants() {
            createColorVariant(validRequest());
            createColorVariant(new CreateFeltColorVariantRequest(feltVariantId, "Blue", "B-001"));

            restTestClient.get().uri(BASE_URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(COLOR_VARIANT_LIST)
                    .value(colorVariants -> assertThat(colorVariants).hasSize(2));
        }
    }

    @Nested
    @DisplayName("POST /api/felt-color-variants")
    class CreateFeltColorVariant {

        @Test
        @DisplayName("creates color variant and returns 201 with full body")
        void createsColorVariant() {
            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(validRequest())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(FeltColorVariantResponse.class)
                    .value(cv -> {
                        assertThat(cv.id()).isGreaterThan(0);
                        assertThat(cv.color()).isEqualTo("Red");
                        assertThat(cv.supplierColor()).isEqualTo("R-001");
                        assertThat(cv.feltVariantId()).isEqualTo(feltVariantId);
                        assertThat(cv.feltId()).isEqualTo(feltId);
                        assertThat(cv.articleNumber()).isEqualTo("ART-001");
                        assertThat(cv.feltTypeName()).isEqualTo("Wool");
                        assertThat(cv.supplierName()).isEqualTo("Supplier A");
                    });
        }

        @Test
        @DisplayName("returns 400 when feltVariantId is missing")
        void rejectsMissingFeltVariantId() {
            var invalid = new CreateFeltColorVariantRequest(null, "Red", "R-001");

            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(err.message()).contains("feltVariantId");
                    });
        }

        @Test
        @DisplayName("returns 400 when color is blank")
        void rejectsBlankColor() {
            var invalid = new CreateFeltColorVariantRequest(feltVariantId, "", "R-001");

            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("color"));
        }

        @Test
        @DisplayName("returns 400 when supplierColor is blank")
        void rejectsBlankSupplierColor() {
            var invalid = new CreateFeltColorVariantRequest(feltVariantId, "Red", "");

            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("supplierColor"));
        }

        @Test
        @DisplayName("returns 404 when feltVariantId does not exist")
        void returns404ForUnknownFeltVariant() {
            var invalid = new CreateFeltColorVariantRequest(9999L, "Red", "R-001");

            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
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
    @DisplayName("GET /api/felt-color-variants/{id}")
    class GetFeltColorVariantById {

        @Test
        @DisplayName("returns color variant when it exists")
        void returnsExistingColorVariant() {
            Long id = createColorVariantAndGetId(validRequest());

            restTestClient.get().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltColorVariantResponse.class)
                    .value(cv -> {
                        assertThat(cv.id()).isEqualTo(id);
                        assertThat(cv.color()).isEqualTo("Red");
                        assertThat(cv.feltVariantId()).isEqualTo(feltVariantId);
                    });
        }

        @Test
        @DisplayName("returns 404 when color variant does not exist")
        void returns404ForMissingColorVariant() {
            restTestClient.get().uri(BASE_URI + "/{id}", 9999)
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
    @DisplayName("PUT /api/felt-color-variants/{id}")
    class UpdateFeltColorVariant {

        @Test
        @DisplayName("updates color and supplierColor")
        void updatesColorVariant() {
            Long id = createColorVariantAndGetId(validRequest());
            var update = new UpdateFeltColorVariantRequest("Green", "G-001");

            restTestClient.put().uri(BASE_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(update)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltColorVariantResponse.class)
                    .value(cv -> {
                        assertThat(cv.id()).isEqualTo(id);
                        assertThat(cv.color()).isEqualTo("Green");
                        assertThat(cv.supplierColor()).isEqualTo("G-001");
                    });
        }

        @Test
        @DisplayName("partial update leaves unchanged fields intact")
        void partialUpdatePreservesFields() {
            Long id = createColorVariantAndGetId(validRequest());
            var update = new UpdateFeltColorVariantRequest("Green", null);

            restTestClient.put().uri(BASE_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(update)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltColorVariantResponse.class)
                    .value(cv -> {
                        assertThat(cv.color()).isEqualTo("Green");
                        assertThat(cv.supplierColor()).isEqualTo("R-001");
                    });
        }

        @Test
        @DisplayName("returns 404 when color variant does not exist")
        void returns404ForMissingColorVariant() {
            restTestClient.put().uri(BASE_URI + "/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltColorVariantRequest("Green", null))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("DELETE /api/felt-color-variants/{id}")
    class DeleteFeltColorVariant {

        @Test
        @DisplayName("deletes existing color variant and returns 204")
        void deletesExistingColorVariant() {
            Long id = createColorVariantAndGetId(validRequest());

            restTestClient.delete().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isNoContent();

            restTestClient.get().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("returns 404 when color variant does not exist")
        void returns404ForMissingColorVariant() {
            restTestClient.delete().uri(BASE_URI + "/{id}", 9999)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).contains("9999");
                    });
        }
    }
}
