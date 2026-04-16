package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.core.entity.Storage;
import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.core.repository.StorageRepository;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.FeltVariant;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
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
class FeltRollControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
        .withDatabaseName("inventory_test")
        .withUsername("test")
        .withPassword("test");

    private static final String BASE_URI = "/api/felt-rolls";
    private static final ParameterizedTypeReference<List<FeltRollDto>> ROLL_LIST =
        new ParameterizedTypeReference<>() {
        };

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private FeltRollRepository feltRollRepository;
    @Autowired
    private FeltColorVariantRepository feltColorVariantRepository;
    @Autowired
    private FeltVariantRepository feltVariantRepository;
    @Autowired
    private FeltRepository feltRepository;
    @Autowired
    private FeltTypeRepository feltTypeRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private StorageRepository storageRepository;

    private Long colorVariantId;
    private Long batchId;
    private Long storageId;

    @BeforeAll
    void setupHierarchy() {
        FeltType feltType = feltTypeRepository.save(new FeltType("Wool"));
        Supplier supplier = supplierRepository.save(new Supplier("Supplier A"));
        Felt felt = feltRepository.save(new Felt(feltType, supplier, "ART-001"));
        FeltVariant variant = feltVariantRepository.save(
            new FeltVariant(felt, 5.0, 300.0, new BigDecimal("12.99")));

        FeltColorVariant colorVariant = new FeltColorVariant(variant, "Red");
        colorVariant.setSupplierColor("R001");
        colorVariantId = feltColorVariantRepository.save(colorVariant)
                                                   .getId();

        batchId = batchRepository.save(new Batch("Batch-1"))
                                 .getId();
        storageId = storageRepository.save(new Storage("Shelf A"))
                                     .getId();
    }

    @BeforeEach
    void resetRolls() {
        feltRollRepository.deleteAll();
    }

    private CreateFeltRollDto validRequest() {
        return new CreateFeltRollDto(colorVariantId, 100.0, 200.0, null, null);
    }

    private Long createRollAndGetId(CreateFeltRollDto request) {
        FeltRollDto body = restTestClient.post()
                                         .uri(BASE_URI)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .body(request)
                                         .exchange()
                                         .expectStatus()
                                         .isCreated()
                                         .expectBody(FeltRollDto.class)
                                         .returnResult()
                                         .getResponseBody();

        assertThat(body).isNotNull();
        return body.id();
    }

    private void createRoll(CreateFeltRollDto request) {
        createRollAndGetId(request);
    }

    @Nested
    @DisplayName("GET /api/felt-rolls")
    class GetAllFeltRolls {

        @Test
        @DisplayName("returns empty list when no rolls exist")
        void returnsEmptyList() {
            restTestClient.get()
                          .uri(BASE_URI)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ROLL_LIST)
                          .value(rolls -> assertThat(rolls).isEmpty());
        }

        @Test
        @DisplayName("returns all rolls")
        void returnsAllRolls() {
            createRoll(validRequest());
            createRoll(new CreateFeltRollDto(colorVariantId, 50.0, 75.0, null, null));

            restTestClient.get()
                          .uri(BASE_URI)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ROLL_LIST)
                          .value(rolls -> assertThat(rolls).hasSize(2));
        }
    }

    @Nested
    @DisplayName("POST /api/felt-rolls")
    class CreateFeltRoll {

        @Test
        @DisplayName("creates roll and returns 201 with full body")
        void createsRoll() {
            restTestClient.post()
                          .uri(BASE_URI)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(validRequest())
                          .exchange()
                          .expectStatus()
                          .isCreated()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.id()).isGreaterThan(0);
                              assertThat(roll.length()).isEqualTo(100.0);
                              assertThat(roll.width()).isEqualTo(200.0);
                              assertThat(roll.feltColorVariantId()).isEqualTo(colorVariantId);
                              assertThat(roll.color()).isEqualTo("Red");
                              assertThat(roll.supplierColor()).isEqualTo("R001");
                              assertThat(roll.articleNumber()).isEqualTo("ART-001");
                              assertThat(roll.batchId()).isNull();
                              assertThat(roll.storageId()).isNull();
                          });
        }

        @Test
        @DisplayName("creates roll with optional batch and storage")
        void createsRollWithBatchAndStorage() {
            var request = new CreateFeltRollDto(colorVariantId, 100.0, 200.0, batchId, storageId);

            restTestClient.post()
                          .uri(BASE_URI)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(request)
                          .exchange()
                          .expectStatus()
                          .isCreated()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.batchId()).isEqualTo(batchId);
                              assertThat(roll.batchName()).isEqualTo("Batch-1");
                              assertThat(roll.storageId()).isEqualTo(storageId);
                              assertThat(roll.storageName()).isEqualTo("Shelf A");
                          });
        }

        @Test
        @DisplayName("returns 400 when feltColorVariantId is missing")
        void rejectsMissingColorVariantId() {
            var invalid = new CreateFeltRollDto(null, 100.0, 200.0, null, null);

            restTestClient.post()
                          .uri(BASE_URI)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(invalid)
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ErrorResponse.class)
                          .value(err -> {
                              assertThat(err.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                              assertThat(err.message()).contains("feltColorVariantId");
                          });
        }

        @Test
        @DisplayName("returns 400 when length is missing")
        void rejectsMissingLength() {
            var invalid = new CreateFeltRollDto(colorVariantId, null, 200.0, null, null);

            restTestClient.post()
                          .uri(BASE_URI)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(invalid)
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ErrorResponse.class)
                          .value(err -> assertThat(err.message()).contains("length"));
        }

        @Test
        @DisplayName("returns 400 when width is missing")
        void rejectsMissingWidth() {
            var invalid = new CreateFeltRollDto(colorVariantId, 100.0, null, null, null);

            restTestClient.post()
                          .uri(BASE_URI)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(invalid)
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ErrorResponse.class)
                          .value(err -> assertThat(err.message()).contains("width"));
        }

        @Test
        @DisplayName("returns 400 when length is not positive")
        void rejectsNonPositiveLength() {
            var invalid = new CreateFeltRollDto(colorVariantId, -1.0, 200.0, null, null);

            restTestClient.post()
                          .uri(BASE_URI)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(invalid)
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ErrorResponse.class)
                          .value(err -> assertThat(err.message()).contains("length"));
        }

        @Test
        @DisplayName("returns 404 when feltColorVariantId does not exist")
        void returns404ForUnknownColorVariant() {
            var invalid = new CreateFeltRollDto(9999L, 100.0, 200.0, null, null);

            restTestClient.post()
                          .uri(BASE_URI)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(invalid)
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ErrorResponse.class)
                          .value(err -> {
                              assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                              assertThat(err.message()).contains("9999");
                          });
        }
    }

    @Nested
    @DisplayName("GET /api/felt-rolls/{id}")
    class GetFeltRollById {

        @Test
        @DisplayName("returns roll when it exists")
        void returnsExistingRoll() {
            Long id = createRollAndGetId(validRequest());

            restTestClient.get()
                          .uri(BASE_URI + "/{id}", id)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.id()).isEqualTo(id);
                              assertThat(roll.feltColorVariantId()).isEqualTo(colorVariantId);
                          });
        }

        @Test
        @DisplayName("returns 404 when roll does not exist")
        void returns404ForMissingRoll() {
            restTestClient.get()
                          .uri(BASE_URI + "/{id}", 9999)
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ErrorResponse.class)
                          .value(err -> {
                              assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                              assertThat(err.message()).contains("9999");
                          });
        }
    }

    @Nested
    @DisplayName("PUT /api/felt-rolls/{id}")
    class UpdateFeltRoll {

        @Test
        @DisplayName("updates length and width")
        void updatesLengthAndWidth() {
            Long id = createRollAndGetId(validRequest());
            var update = new UpdateFeltRollDto(75.0, 125.0, null, null);

            restTestClient.put()
                          .uri(BASE_URI + "/{id}", id)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(update)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.id()).isEqualTo(id);
                              assertThat(roll.length()).isEqualTo(75.0);
                              assertThat(roll.width()).isEqualTo(125.0);
                          });
        }

        @Test
        @DisplayName("assigns batch and storage")
        void assignsBatchAndStorage() {
            Long id = createRollAndGetId(validRequest());
            var update = new UpdateFeltRollDto(null, null, batchId, storageId);

            restTestClient.put()
                          .uri(BASE_URI + "/{id}", id)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(update)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.batchId()).isEqualTo(batchId);
                              assertThat(roll.storageId()).isEqualTo(storageId);
                          });
        }

        @Test
        @DisplayName("clears batch and storage when ids are null")
        void clearsBatchAndStorage() {
            Long id = createRollAndGetId(
                new CreateFeltRollDto(colorVariantId, 100.0, 200.0, batchId, storageId));
            var update = new UpdateFeltRollDto(null, null, null, null);

            restTestClient.put()
                          .uri(BASE_URI + "/{id}", id)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(update)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.batchId()).isNull();
                              assertThat(roll.storageId()).isNull();
                          });
        }

        @Test
        @DisplayName("returns 404 when roll does not exist")
        void returns404ForMissingRoll() {
            var update = new UpdateFeltRollDto(100.0, 200.0, null, null);

            restTestClient.put()
                          .uri(BASE_URI + "/{id}", 9999)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(update)
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ErrorResponse.class)
                          .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("DELETE /api/felt-rolls/{id}")
    class DeleteFeltRoll {

        @Test
        @DisplayName("deletes existing roll and returns 204")
        void deletesExistingRoll() {
            Long id = createRollAndGetId(validRequest());

            restTestClient.delete()
                          .uri(BASE_URI + "/{id}", id)
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            restTestClient.get()
                          .uri(BASE_URI + "/{id}", id)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 404 when roll does not exist")
        void returns404ForMissingRoll() {
            restTestClient.delete()
                          .uri(BASE_URI + "/{id}", 9999)
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ErrorResponse.class)
                          .value(err -> {
                              assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                              assertThat(err.message()).contains("9999");
                          });
        }
    }
}
