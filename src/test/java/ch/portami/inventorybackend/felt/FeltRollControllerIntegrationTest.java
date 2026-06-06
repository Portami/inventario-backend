package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.CutFeltRollDto;
import ch.portami.inventorybackend.felt.dto.CutResultDto;
import ch.portami.inventorybackend.felt.dto.CutScrapDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import ch.portami.inventorybackend.storage.entity.Storage;
import ch.portami.inventorybackend.storage.repository.StorageRepository;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeltRollControllerIntegrationTest extends BaseIntegrationTest {

    private static final ParameterizedTypeReference<List<FeltRollDto>> ROLL_LIST =
            new ParameterizedTypeReference<>() {
            };

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private FeltRollRepository feltRollRepository;
    @Autowired
    private ScrapPieceRepository scrapPieceRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private FeltTypeRepository feltTypeRepository;
    @Autowired
    private BatchRepository batchRepository;
    @Autowired
    private StorageRepository storageRepository;

    private Long feltId;
    private Long batchId;
    private Long storageId;

    @BeforeAll
    void setup() {
        Supplier supplier = supplierRepository.save(new Supplier("Test Supplier"));
        FeltType feltType = feltTypeRepository.save(new FeltType("Wool"));

        FeltDto felt = restTestClient.post()
                                     .uri("/api/felts")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .body(new CreateFeltDto(
                                             "Red", "Supplier Red",
                                             2.0, 300.0, new BigDecimal("12.50"),
                                             "ART-001", supplier.getId(), feltType.getId()
                                     ))
                                     .exchange()
                                     .expectStatus()
                                     .isCreated()
                                     .expectBody(FeltDto.class)
                                     .returnResult()
                                     .getResponseBody();

        assertThat(felt).isNotNull();
        feltId = felt.id();

        batchId = batchRepository.save(new Batch("Batch-001"))
                                 .getId();
        storageId = storageRepository.save(new Storage("Shelf-A"))
                                     .getId();
    }

    @BeforeEach
    void resetRolls() {
        scrapPieceRepository.deleteAll();
        feltRollRepository.deleteAll();
    }

    private CreateFeltRollDto validRequest() {
        return new CreateFeltRollDto(feltId, 10.0, 1.5, null, storageId);
    }

    private FeltRollDto postRoll(CreateFeltRollDto dto) {
        FeltRollDto body = restTestClient.post()
                                         .uri("/api/rolls")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .body(dto)
                                         .exchange()
                                         .expectStatus()
                                         .isCreated()
                                         .expectBody(FeltRollDto.class)
                                         .returnResult()
                                         .getResponseBody();
        assertThat(body).isNotNull();
        return body;
    }

    // ── GET /api/felts/{feltId}/rolls ───────────────────────────────────────

    @Nested
    @DisplayName("GET /api/felts/{feltId}/rolls")
    class GetRollsByFelt {

        @Test
        @DisplayName("returns empty list when no rolls exist")
        void returnsEmptyList() {
            restTestClient.get()
                          .uri("/api/felts/{feltId}/rolls", feltId)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ROLL_LIST)
                          .value(list -> assertThat(list).isEmpty());
        }

        @Test
        @DisplayName("returns all rolls belonging to the felt")
        void returnsAllRolls() {
            postRoll(validRequest());
            postRoll(new CreateFeltRollDto(feltId, 20.0, 2.0, null, storageId));

            restTestClient.get()
                          .uri("/api/felts/{feltId}/rolls", feltId)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ROLL_LIST)
                          .value(list -> assertThat(list).hasSize(2));
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.get()
                          .uri("/api/felts/99999/rolls")
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    // ── POST /api/rolls ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/rolls")
    class CreateRoll {

        @Test
        @DisplayName("creates roll without batch and returns 201 with Location")
        void createsRollWithoutBatch() {
            var response = restTestClient.post()
                                         .uri("/api/rolls")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .body(validRequest())
                                         .exchange();

            response.expectStatus()
                    .isCreated()
                    .expectBody(FeltRollDto.class)
                    .value(roll -> {
                        assertThat(roll.id()).isNotNull();
                        assertThat(roll.length()).isEqualTo(10.0);
                        assertThat(roll.width()).isEqualTo(1.5);
                        assertThat(roll.feltId()).isEqualTo(feltId);
                        assertThat(roll.batchId()).isNotNull();
                        assertThat(roll.storageId()).isNotNull();
                    });

            var location = response.returnResult(FeltRollDto.class)
                                   .getResponseHeaders()
                                   .getLocation();
            assertThat(location).isNotNull();
            assertThat(location.toString()).contains("/api/rolls/");
        }

        @Test
        @DisplayName("creates roll with batch and storage")
        void createsRollWithBatchAndStorage() {
            restTestClient.post()
                          .uri("/api/rolls")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltRollDto(feltId, 10.0, 1.5, batchId, storageId))
                          .exchange()
                          .expectStatus()
                          .isCreated()
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
            restTestClient.post()
                          .uri("/api/rolls")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltRollDto(99999L, 10.0, 1.5, null, null))
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("returns 400 when feltId is missing")
        void rejectsMissingFeltId() {
            restTestClient.post()
                          .uri("/api/rolls")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltRollDto(null, 10.0, 1.5, null, null))
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getDetail()).contains("feltId"));
        }

        @Test
        @DisplayName("returns 400 when length is missing")
        void rejectsMissingLength() {
            restTestClient.post()
                          .uri("/api/rolls")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltRollDto(feltId, null, 1.5, null, null))
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getDetail()).contains("length"));
        }

        @Test
        @DisplayName("returns 400 when width is missing")
        void rejectsMissingWidth() {
            restTestClient.post()
                          .uri("/api/rolls")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltRollDto(feltId, 10.0, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getDetail()).contains("width"));
        }

        @Test
        @DisplayName("returns 400 when length is not positive")
        void rejectsNonPositiveLength() {
            restTestClient.post()
                          .uri("/api/rolls")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltRollDto(feltId, -5.0, 1.5, null, null))
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getDetail()).contains("length"));
        }
    }

    // ── GET /api/rolls/{id} ─────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/rolls/{id}")
    class GetRollById {

        @Test
        @DisplayName("returns roll with full hierarchy info")
        void returnsExistingRoll() {
            FeltRollDto created = postRoll(validRequest());

            restTestClient.get()
                          .uri("/api/rolls/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.id()).isEqualTo(created.id());
                              assertThat(roll.length()).isEqualTo(10.0);
                              assertThat(roll.width()).isEqualTo(1.5);
                              assertThat(roll.feltId()).isEqualTo(feltId);
                              assertThat(roll.color()).isEqualTo("Red");
                              assertThat(roll.articleNumber()).isEqualTo("ART-001");
                              assertThat(roll.batchId()).isNotNull();
                              assertThat(roll.storageId()).isNotNull();
                          });
        }

        @Test
        @DisplayName("returns 404 when roll does not exist")
        void returns404ForMissingRoll() {
            restTestClient.get()
                          .uri("/api/rolls/99999")
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    // ── PATCH /api/rolls/{id} ───────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /api/rolls/{id}")
    class UpdateRoll {

        @Test
        @DisplayName("updates only length when only length is sent")
        void updatesLengthOnly() {
            FeltRollDto created = postRoll(validRequest());

            restTestClient.patch()
                          .uri("/api/rolls/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltRollDto(25.0, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.id()).isEqualTo(created.id());
                              assertThat(roll.length()).isEqualTo(25.0);
                              assertThat(roll.width()).isEqualTo(1.5); // unchanged
                          });
        }

        @Test
        @DisplayName("updates both dimensions when both are provided")
        void updatesDimensions() {
            FeltRollDto created = postRoll(validRequest());

            restTestClient.patch()
                          .uri("/api/rolls/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltRollDto(25.0, 3.0, null, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.id()).isEqualTo(created.id());
                              assertThat(roll.length()).isEqualTo(25.0);
                              assertThat(roll.width()).isEqualTo(3.0);
                          });
        }

        @Test
        @DisplayName("null id in PATCH body leaves existing batch unchanged")
        void nullBatchIdPreservesExistingBatch() {
            FeltRollDto created = postRoll(new CreateFeltRollDto(feltId, 10.0, 1.5, batchId, storageId));
            assertThat(created.batchId()).isEqualTo(batchId);
            assertThat(created.storageId()).isEqualTo(storageId);

            restTestClient.patch()
                          .uri("/api/rolls/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltRollDto(20.0, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltRollDto.class)
                          .value(roll -> {
                              assertThat(roll.length()).isEqualTo(20.0);
                              assertThat(roll.batchId()).isEqualTo(batchId);    // preserved
                              assertThat(roll.storageId()).isEqualTo(storageId); // preserved
                          });
        }

        @Test
        @DisplayName("returns 404 when roll does not exist")
        void returns404ForMissingRoll() {
            restTestClient.patch()
                          .uri("/api/rolls/99999")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltRollDto(10.0, 1.5, null, null))
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    // ── DELETE /api/rolls/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/rolls/{id}")
    class DeleteRoll {

        @Test
        @DisplayName("deletes existing roll and returns 204")
        void deletesExistingRoll() {
            FeltRollDto created = postRoll(validRequest());

            restTestClient.delete()
                          .uri("/api/rolls/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            restTestClient.get()
                          .uri("/api/rolls/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 204 when roll does not exist")
        void returns204ForMissingRoll() {
            restTestClient.delete()
                          .uri("/api/rolls/99999")
                          .exchange()
                          .expectStatus()
                          .isNoContent();
        }
    }

    // ── POST /api/rolls/{id}/cut ────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/rolls/{id}/cut")
    class CutRoll {

        private FeltRollDto longRoll() {
            return postRoll(new CreateFeltRollDto(feltId, 1000.0, 180.0, null, storageId));
        }

        @Test
        @DisplayName("shortens the roll, keeps width, and creates valid scraps")
        void cutsRollAndCreatesScraps() {
            FeltRollDto roll = longRoll();

            restTestClient.post()
                          .uri("/api/rolls/{id}/cut", roll.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CutFeltRollDto(200.0, List.of(
                                  new CutScrapDto(60.0, 50.0, null, null),
                                  new CutScrapDto(80.0, 44.0, null, null))))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(CutResultDto.class)
                          .value(result -> {
                              assertThat(result.roll().id()).isEqualTo(roll.id());
                              assertThat(result.roll().length()).isEqualTo(800.0);
                              assertThat(result.roll().width()).isEqualTo(180.0);
                              assertThat(result.createdScraps()).hasSize(2);
                          });

            assertThat(feltRollRepository.existsById(roll.id())).isTrue();
            assertThat(scrapPieceRepository.count()).isEqualTo(2);
        }

        @Test
        @DisplayName("silently drops a too-small scrap while keeping the valid ones")
        void dropsTooSmallScrap() {
            FeltRollDto roll = longRoll();

            restTestClient.post()
                          .uri("/api/rolls/{id}/cut", roll.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CutFeltRollDto(100.0, List.of(
                                  new CutScrapDto(60.0, 50.0, null, null),
                                  new CutScrapDto(40.0, 200.0, null, null)))) // width 40 < 44 → dropped
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(CutResultDto.class)
                          .value(result -> assertThat(result.createdScraps()).hasSize(1));

            assertThat(scrapPieceRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("shortens the roll when no scraps are supplied")
        void cutsWithoutScraps() {
            FeltRollDto roll = longRoll();

            restTestClient.post()
                          .uri("/api/rolls/{id}/cut", roll.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CutFeltRollDto(150.0, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(CutResultDto.class)
                          .value(result -> {
                              assertThat(result.roll().length()).isEqualTo(850.0);
                              assertThat(result.createdScraps()).isEmpty();
                          });

            assertThat(scrapPieceRepository.count()).isZero();
        }

        @Test
        @DisplayName("returns 409 when the cut length is not less than the roll length")
        void rejectsTooLongCut() {
            FeltRollDto roll = longRoll();

            restTestClient.post()
                          .uri("/api/rolls/{id}/cut", roll.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CutFeltRollDto(1000.0, null))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 404 when the roll does not exist")
        void returns404ForMissingRoll() {
            restTestClient.post()
                          .uri("/api/rolls/99999/cut")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CutFeltRollDto(50.0, null))
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }
}
