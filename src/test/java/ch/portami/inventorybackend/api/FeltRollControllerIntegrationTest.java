package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.core.entity.Storage;
import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.core.repository.StorageRepository;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
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
class FeltRollControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final ParameterizedTypeReference<List<FeltRollDto>> ROLL_LIST =
            new ParameterizedTypeReference<>() {};

    @Autowired private RestTestClient restTestClient;
    @Autowired private FeltRollRepository feltRollRepository;
    @Autowired private SupplierRepository supplierRepository;
    @Autowired private BatchRepository batchRepository;
    @Autowired private StorageRepository storageRepository;

    private Long feltId;
    private Long batchId;
    private Long storageId;

    @BeforeAll
    void setup() {
        Supplier supplier = supplierRepository.save(new Supplier("Test Supplier"));

        FeltDto felt = restTestClient.post().uri("/api/felts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateFeltDto(
                        "Red", "Supplier Red",
                        2.0, 300.0, new BigDecimal("12.50"),
                        "ART-001", supplier.getId(), "Wool"
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeltDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(felt).isNotNull();
        feltId = felt.id();

        batchId = batchRepository.save(new Batch("Batch-001")).getId();
        storageId = storageRepository.save(new Storage("Shelf-A")).getId();
    }

    @BeforeEach
    void resetRolls() {
        feltRollRepository.deleteAll();
    }

    private CreateFeltRollDto validRequest() {
        return new CreateFeltRollDto(feltId, 10.0, 1.5, null, null);
    }

    private FeltRollDto postRoll(CreateFeltRollDto dto) {
        FeltRollDto body = restTestClient.post().uri("/api/rolls")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeltRollDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(body).isNotNull();
        return body;
    }

    @Nested
    @DisplayName("GET /api/felts/{feltId}/rolls")
    class GetRollsByFelt {

        @Test
        @DisplayName("returns empty list when no rolls exist")
        void returnsEmptyList() {
            restTestClient.get().uri("/api/felts/{feltId}/rolls", feltId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(ROLL_LIST)
                    .value(list -> assertThat(list).isEmpty());
        }

        @Test
        @DisplayName("returns all rolls belonging to the felt")
        void returnsAllRolls() {
            postRoll(validRequest());
            postRoll(new CreateFeltRollDto(feltId, 20.0, 2.0, null, null));

            restTestClient.get().uri("/api/felts/{feltId}/rolls", feltId)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(ROLL_LIST)
                    .value(list -> assertThat(list).hasSize(2));
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.get().uri("/api/felts/99999/rolls")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("POST /api/rolls")
    class CreateRoll {

        @Test
        @DisplayName("creates roll without batch or storage and returns 201 with Location")
        void createsRollWithoutBatchAndStorage() {
            var response = restTestClient.post().uri("/api/rolls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(validRequest())
                    .exchange();

            response.expectStatus().isCreated()
                    .expectBody(FeltRollDto.class)
                    .value(roll -> {
                        assertThat(roll.id()).isNotNull();
                        assertThat(roll.length()).isEqualTo(10.0);
                        assertThat(roll.width()).isEqualTo(1.5);
                        assertThat(roll.feltColorVariantId()).isEqualTo(feltId);
                        assertThat(roll.batchId()).isNull();
                        assertThat(roll.storageId()).isNull();
                    });

            var location = response.returnResult(FeltRollDto.class).getResponseHeaders().getLocation();
            assertThat(location).isNotNull();
            assertThat(location.toString()).contains("/api/rolls/");
        }

        @Test
        @DisplayName("creates roll with batch and storage")
        void createsRollWithBatchAndStorage() {
            restTestClient.post().uri("/api/rolls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltRollDto(feltId, 10.0, 1.5, batchId, storageId))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(FeltRollDto.class)
                    .value(roll -> {
                        assertThat(roll.batchId()).isEqualTo(batchId);
                        assertThat(roll.batchName()).isEqualTo("Batch-001");
                        assertThat(roll.storageId()).isEqualTo(storageId);
                        assertThat(roll.storageName()).isEqualTo("Shelf-A");
                    });
        }

        @Test
        @DisplayName("returns 404 when feltId does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.post().uri("/api/rolls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltRollDto(99999L, 10.0, 1.5, null, null))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("returns 400 when feltId is missing")
        void rejectsMissingFeltId() {
            restTestClient.post().uri("/api/rolls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltRollDto(null, 10.0, 1.5, null, null))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("feltId"));
        }

        @Test
        @DisplayName("returns 400 when length is missing")
        void rejectsMissingLength() {
            restTestClient.post().uri("/api/rolls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltRollDto(feltId, null, 1.5, null, null))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("length"));
        }

        @Test
        @DisplayName("returns 400 when width is missing")
        void rejectsMissingWidth() {
            restTestClient.post().uri("/api/rolls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltRollDto(feltId, 10.0, null, null, null))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("width"));
        }

        @Test
        @DisplayName("returns 400 when length is not positive")
        void rejectsNonPositiveLength() {
            restTestClient.post().uri("/api/rolls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltRollDto(feltId, -5.0, 1.5, null, null))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("length"));
        }
    }

    @Nested
    @DisplayName("GET /api/rolls/{id}")
    class GetRollById {

        @Test
        @DisplayName("returns roll with full hierarchy info")
        void returnsExistingRoll() {
            FeltRollDto created = postRoll(validRequest());

            restTestClient.get().uri("/api/rolls/{id}", created.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltRollDto.class)
                    .value(roll -> {
                        assertThat(roll.id()).isEqualTo(created.id());
                        assertThat(roll.length()).isEqualTo(10.0);
                        assertThat(roll.width()).isEqualTo(1.5);
                        assertThat(roll.feltColorVariantId()).isEqualTo(feltId);
                        assertThat(roll.color()).isEqualTo("Red");
                        assertThat(roll.articleNumber()).isEqualTo("ART-001");
                        assertThat(roll.batchId()).isNull();
                        assertThat(roll.storageId()).isNull();
                    });
        }

        @Test
        @DisplayName("returns 404 when roll does not exist")
        void returns404ForMissingRoll() {
            restTestClient.get().uri("/api/rolls/99999")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("PUT /api/rolls/{id}")
    class UpdateRoll {

        @Test
        @DisplayName("updates dimensions")
        void updatesDimensions() {
            FeltRollDto created = postRoll(validRequest());

            restTestClient.put().uri("/api/rolls/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltRollDto(25.0, 3.0, null, null))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltRollDto.class)
                    .value(roll -> {
                        assertThat(roll.id()).isEqualTo(created.id());
                        assertThat(roll.length()).isEqualTo(25.0);
                        assertThat(roll.width()).isEqualTo(3.0);
                    });
        }

        @Test
        @DisplayName("clears batch and storage when ids are null")
        void clearsBatchAndStorage() {
            FeltRollDto created = postRoll(new CreateFeltRollDto(feltId, 10.0, 1.5, batchId, storageId));
            assertThat(created.batchId()).isEqualTo(batchId);
            assertThat(created.storageId()).isEqualTo(storageId);

            restTestClient.put().uri("/api/rolls/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltRollDto(10.0, 1.5, null, null))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltRollDto.class)
                    .value(roll -> {
                        assertThat(roll.batchId()).isNull();
                        assertThat(roll.storageId()).isNull();
                    });
        }

        @Test
        @DisplayName("returns 404 when roll does not exist")
        void returns404ForMissingRoll() {
            restTestClient.put().uri("/api/rolls/99999")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltRollDto(10.0, 1.5, null, null))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("DELETE /api/rolls/{id}")
    class DeleteRoll {

        @Test
        @DisplayName("deletes existing roll and returns 204")
        void deletesExistingRoll() {
            FeltRollDto created = postRoll(validRequest());

            restTestClient.delete().uri("/api/rolls/{id}", created.id())
                    .exchange()
                    .expectStatus().isNoContent();

            restTestClient.get().uri("/api/rolls/{id}", created.id())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("returns 404 when roll does not exist")
        void returns404ForMissingRoll() {
            restTestClient.delete().uri("/api/rolls/99999")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }
}
