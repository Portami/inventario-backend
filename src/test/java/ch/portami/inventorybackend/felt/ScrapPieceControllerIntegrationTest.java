package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.CreateScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.ScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.UpdateScrapPieceDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import ch.portami.inventorybackend.storage.entity.Storage;
import ch.portami.inventorybackend.storage.repository.StorageRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ScrapPieceControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;
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
    void resetScraps() {
        scrapPieceRepository.deleteAll();
    }

    private ScrapPieceDto postScrap(CreateScrapPieceDto dto) {
        ScrapPieceDto body = restTestClient.post()
                                           .uri("/api/scraps")
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .body(dto)
                                           .exchange()
                                           .expectStatus()
                                           .isCreated()
                                           .expectBody(ScrapPieceDto.class)
                                           .returnResult()
                                           .getResponseBody();
        assertThat(body).isNotNull();
        return body;
    }

    // ── POST /api/scraps ────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/scraps")
    class CreateScrap {

        @Test
        @DisplayName("creates a scrap whose sides meet the minimum and returns 201")
        void createsValidScrap() {
            ScrapPieceDto created = postScrap(new CreateScrapPieceDto(feltId, 60.0, 50.0, batchId, storageId));

            assertThat(created.id()).isNotNull();
            assertThat(created.length()).isEqualTo(60.0);
            assertThat(created.width()).isEqualTo(50.0);
            assertThat(scrapPieceRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("returns 400 and persists nothing when a side is below the minimum")
        void rejectsTooSmallScrap() {
            restTestClient.post()
                          .uri("/api/scraps")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateScrapPieceDto(feltId, 40.0, 50.0, null, null)) // length 40 < 44
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()));

            assertThat(scrapPieceRepository.count()).isZero();
        }

        @Test
        @DisplayName("returns 400 when a side is zero or negative")
        void rejectsNonPositiveSide() {
            restTestClient.post()
                          .uri("/api/scraps")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateScrapPieceDto(feltId, -5.0, 50.0, null, null))
                          .exchange()
                          .expectStatus()
                          .isBadRequest();

            assertThat(scrapPieceRepository.count()).isZero();
        }
    }

    // ── PATCH /api/scraps/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /api/scraps/{id}")
    class UpdateScrap {

        @Test
        @DisplayName("updates dimensions when the result still meets the minimum")
        void updatesValidDimensions() {
            ScrapPieceDto created = postScrap(new CreateScrapPieceDto(feltId, 60.0, 50.0, null, null));

            restTestClient.patch()
                          .uri("/api/scraps/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateScrapPieceDto(70.0, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ScrapPieceDto.class)
                          .value(scrap -> {
                              assertThat(scrap.length()).isEqualTo(70.0);
                              assertThat(scrap.width()).isEqualTo(50.0); // unchanged
                          });
        }

        @Test
        @DisplayName("returns 400 and leaves the scrap unchanged when an edit drops a side below the minimum")
        void rejectsShrinkingBelowMinimum() {
            ScrapPieceDto created = postScrap(new CreateScrapPieceDto(feltId, 60.0, 50.0, null, null));

            restTestClient.patch()
                          .uri("/api/scraps/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateScrapPieceDto(40.0, null, null, null)) // 40 < 44
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value()));

            assertThat(scrapPieceRepository.findById(created.id()))
                    .get()
                    .satisfies(scrap -> assertThat(scrap.getLength()).isEqualTo(60.0));
        }

        @Test
        @DisplayName("returns 404 when the scrap does not exist")
        void returns404ForMissingScrap() {
            restTestClient.patch()
                          .uri("/api/scraps/99999")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateScrapPieceDto(70.0, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }
}
