package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.felt.dto.CreateFeltRequest;
import ch.portami.inventorybackend.felt.dto.FeltResponse;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRequest;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
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
class FeltControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final String BASE_URI = "/api/felts";
    private static final ParameterizedTypeReference<List<FeltResponse>> FELT_LIST =
            new ParameterizedTypeReference<>() {};

    @Autowired private RestTestClient restTestClient;
    @Autowired private FeltRepository feltRepository;
    @Autowired private FeltTypeRepository feltTypeRepository;
    @Autowired private SupplierRepository supplierRepository;

    private Long feltTypeId;
    private Long altFeltTypeId;
    private Long supplierId;

    @BeforeAll
    void setupHierarchy() {
        feltTypeId = feltTypeRepository.save(new FeltType("Wool")).getId();
        altFeltTypeId = feltTypeRepository.save(new FeltType("Synthetic")).getId();
        supplierId = supplierRepository.save(new Supplier("Supplier A")).getId();
    }

    @BeforeEach
    void resetFelts() {
        feltRepository.deleteAll();
    }

    private CreateFeltRequest validRequest() {
        return new CreateFeltRequest(feltTypeId, supplierId, "ART-001");
    }

    private Long createFeltAndGetId(CreateFeltRequest request) {
        FeltResponse body = restTestClient.post().uri(BASE_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeltResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(body).isNotNull();
        return body.id();
    }

    private void createFelt(CreateFeltRequest request) {
        createFeltAndGetId(request);
    }

    @Nested
    @DisplayName("GET /api/felts")
    class GetAllFelts {

        @Test
        @DisplayName("returns empty list when no felts exist")
        void returnsEmptyList() {
            restTestClient.get().uri(BASE_URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FELT_LIST)
                    .value(felts -> assertThat(felts).isEmpty());
        }

        @Test
        @DisplayName("returns all felts")
        void returnsAllFelts() {
            createFelt(validRequest());
            createFelt(new CreateFeltRequest(feltTypeId, supplierId, "ART-002"));

            restTestClient.get().uri(BASE_URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FELT_LIST)
                    .value(felts -> assertThat(felts).hasSize(2));
        }
    }

    @Nested
    @DisplayName("POST /api/felts")
    class CreateFelt {

        @Test
        @DisplayName("creates felt and returns 201 with full body")
        void createsFelt() {
            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(validRequest())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(FeltResponse.class)
                    .value(felt -> {
                        assertThat(felt.id()).isGreaterThan(0);
                        assertThat(felt.articleNumber()).isEqualTo("ART-001");
                        assertThat(felt.feltTypeId()).isEqualTo(feltTypeId);
                        assertThat(felt.feltTypeName()).isEqualTo("Wool");
                        assertThat(felt.supplierId()).isEqualTo(supplierId);
                        assertThat(felt.supplierName()).isEqualTo("Supplier A");
                    });
        }

        @Test
        @DisplayName("returns 400 when feltTypeId is missing")
        void rejectsMissingFeltTypeId() {
            var invalid = new CreateFeltRequest(null, supplierId, "ART-001");

            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(err.message()).contains("feltTypeId");
                    });
        }

        @Test
        @DisplayName("returns 400 when supplierId is missing")
        void rejectsMissingSupplierId() {
            var invalid = new CreateFeltRequest(feltTypeId, null, "ART-001");

            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("supplierId"));
        }

        @Test
        @DisplayName("returns 400 when articleNumber is blank")
        void rejectsBlankArticleNumber() {
            var invalid = new CreateFeltRequest(feltTypeId, supplierId, "");

            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("articleNumber"));
        }

        @Test
        @DisplayName("returns 404 when feltTypeId does not exist")
        void returns404ForUnknownFeltType() {
            var invalid = new CreateFeltRequest(9999L, supplierId, "ART-001");

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

        @Test
        @DisplayName("returns 404 when supplierId does not exist")
        void returns404ForUnknownSupplier() {
            var invalid = new CreateFeltRequest(feltTypeId, 9999L, "ART-001");

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
    @DisplayName("GET /api/felts/{id}")
    class GetFeltById {

        @Test
        @DisplayName("returns felt when it exists")
        void returnsExistingFelt() {
            Long id = createFeltAndGetId(validRequest());

            restTestClient.get().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltResponse.class)
                    .value(felt -> {
                        assertThat(felt.id()).isEqualTo(id);
                        assertThat(felt.articleNumber()).isEqualTo("ART-001");
                    });
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForMissingFelt() {
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
    @DisplayName("PUT /api/felts/{id}")
    class UpdateFelt {

        @Test
        @DisplayName("updates articleNumber")
        void updatesArticleNumber() {
            Long id = createFeltAndGetId(validRequest());
            var update = new UpdateFeltRequest(null, null, "ART-999");

            restTestClient.put().uri(BASE_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(update)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltResponse.class)
                    .value(felt -> {
                        assertThat(felt.id()).isEqualTo(id);
                        assertThat(felt.articleNumber()).isEqualTo("ART-999");
                        assertThat(felt.feltTypeId()).isEqualTo(feltTypeId);
                    });
        }

        @Test
        @DisplayName("updates feltTypeId")
        void updatesFeltType() {
            Long id = createFeltAndGetId(validRequest());
            var update = new UpdateFeltRequest(altFeltTypeId, null, null);

            restTestClient.put().uri(BASE_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(update)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltResponse.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeId()).isEqualTo(altFeltTypeId);
                        assertThat(felt.feltTypeName()).isEqualTo("Synthetic");
                    });
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForMissingFelt() {
            restTestClient.put().uri(BASE_URI + "/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltRequest(null, null, "ART-999"))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("returns 404 when updated feltTypeId does not exist")
        void returns404ForUnknownFeltType() {
            Long id = createFeltAndGetId(validRequest());

            restTestClient.put().uri(BASE_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltRequest(9999L, null, null))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("9999"));
        }
    }

    @Nested
    @DisplayName("DELETE /api/felts/{id}")
    class DeleteFelt {

        @Test
        @DisplayName("deletes existing felt and returns 204")
        void deletesExistingFelt() {
            Long id = createFeltAndGetId(validRequest());

            restTestClient.delete().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isNoContent();

            restTestClient.get().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForMissingFelt() {
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
