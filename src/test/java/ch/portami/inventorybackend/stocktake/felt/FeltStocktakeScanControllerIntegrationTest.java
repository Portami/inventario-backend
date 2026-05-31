package ch.portami.inventorybackend.stocktake.felt;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.barcode.entity.Barcode;
import ch.portami.inventorybackend.barcode.repository.BarcodeRepository;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.dto.item.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.CreateFeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.CreateFeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
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
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeltStocktakeScanControllerIntegrationTest extends BaseIntegrationTest {

    private static final ParameterizedTypeReference<List<FeltStocktakeScanDto>> SCAN_LIST =
            new ParameterizedTypeReference<>() {
            };

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private SupplierRepository supplierRepository;
    @Autowired
    private FeltTypeRepository feltTypeRepository;
    @Autowired
    private FeltRepository feltRepository;
    @Autowired
    private FeltRollRepository feltRollRepository;
    @Autowired
    private ScrapPieceRepository scrapPieceRepository;
    @Autowired
    private BarcodeRepository barcodeRepository;
    @Autowired
    private FeltStocktakeRepository stocktakeRepository;

    private Long storageAId;
    private Long storageBId;
    private Long supplierId;
    private Long feltTypeId;

    private Long rollBarcodeId;

    @BeforeAll
    void beforeAll() {
        storageAId = storageRepository.save(new Storage("Storage A"))
                                      .getId();
        storageBId = storageRepository.save(new Storage("Storage B"))
                                      .getId();

        supplierId = supplierRepository.save(new Supplier("Test Supplier"))
                                       .getId();
        feltTypeId = feltTypeRepository.save(new FeltType("Wool"))
                                       .getId();
    }

    @BeforeEach
    void reset() {
        stocktakeRepository.deleteAll();
        barcodeRepository.deleteAll();
        scrapPieceRepository.deleteAll();
        feltRollRepository.deleteAll();
        feltRepository.deleteAll();

        Felt felt = feltRepository.save(new Felt(
                feltTypeRepository.getReferenceById(feltTypeId),
                supplierRepository.getReferenceById(supplierId),
                "ART-001",
                2.0,
                300.0,
                new BigDecimal("12.50"),
                "Red",
                "Supplier Red"
        ));

        FeltRoll roll = feltRollRepository.save(
                new FeltRoll(felt, null, storageRepository.getReferenceById(storageAId), 10.0, 1.5));
        ScrapPiece scrap = scrapPieceRepository.save(
                new ScrapPiece(felt, null, storageRepository.getReferenceById(storageBId), 50.0, 50.0));

        rollBarcodeId = barcodeRepository.save(Barcode.forRoll(roll))
                                         .getId();
        barcodeRepository.save(Barcode.forScrap(scrap));
    }

    private FeltStocktakeDto postStocktake(boolean singleStorage) {
        FeltStocktakeDto body = restTestClient.post()
                                              .uri("/api/stocktakes")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .body(new CreateFeltStocktakeDto("Scan run", true,
                                                      singleStorage ? List.of(storageAId)
                                                              : List.of(storageAId, storageBId)))
                                              .exchange()
                                              .expectStatus()
                                              .isCreated()
                                              .expectBody(FeltStocktakeDto.class)
                                              .returnResult()
                                              .getResponseBody();
        assertThat(body).isNotNull();
        return body;
    }

    private FeltStocktakeScanDto postScan(Long stocktakeId, String barcode, Long storageId) {
        FeltStocktakeScanDto body = restTestClient.post()
                                                  .uri("/api/stocktakes/{id}/scans", stocktakeId)
                                                  .contentType(MediaType.APPLICATION_JSON)
                                                  .body(new CreateFeltStocktakeScanDto(barcode, storageId))
                                                  .exchange()
                                                  .expectStatus()
                                                  .isCreated()
                                                  .expectBody(FeltStocktakeScanDto.class)
                                                  .returnResult()
                                                  .getResponseBody();
        assertThat(body).isNotNull();
        return body;
    }

    private void closeStorage(Long stocktakeId, Long storageId) {
        restTestClient.post()
                      .uri("/api/stocktakes/{id}/storages/{storageId}/close", stocktakeId, storageId)
                      .exchange()
                      .expectStatus()
                      .isNoContent();
    }

    @Nested
    @DisplayName("POST /api/stocktakes/{id}/scans")
    class CreateScan {

        @Test
        @DisplayName("creates scan for known roll barcode")
        void createsScan() {
            FeltStocktakeDto stocktake = postStocktake(false);

            FeltStocktakeScanDto scan = postScan(stocktake.id(), String.valueOf(rollBarcodeId), storageAId);

            assertThat(scan.scanId()).isNotNull();
            assertThat(scan.barcode()).isEqualTo(String.valueOf(rollBarcodeId));
            assertThat(scan.scannedStorageId()).isEqualTo(storageAId);
            assertThat(scan.isVoided()).isFalse();
        }

        @Test
        @DisplayName("returns 404 when stocktake does not exist")
        void returns404ForUnknownStocktake() {
            restTestClient.post()
                          .uri("/api/stocktakes/99999/scans")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltStocktakeScanDto(String.valueOf(rollBarcodeId), storageAId))
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 422 when storage is not part of stocktake")
        void rejectsStorageOutsideStocktake() {
            FeltStocktakeDto stocktake = postStocktake(true);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/scans", stocktake.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltStocktakeScanDto(String.valueOf(rollBarcodeId), storageBId))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT);
        }

        @Test
        @DisplayName("returns 409 when stocktake is completed")
        void rejectsScanOnCompleted() {
            FeltStocktakeDto stocktake = postStocktake(true);
            postScan(stocktake.id(), String.valueOf(rollBarcodeId), storageAId);

            closeStorage(stocktake.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", stocktake.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/scans", stocktake.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateFeltStocktakeScanDto(String.valueOf(rollBarcodeId), storageAId))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("GET /api/stocktakes/{id}/scans")
    class GetScans {

        @Test
        @DisplayName("returns scans for stocktake")
        void returnsScans() {
            FeltStocktakeDto stocktake = postStocktake(false);
            postScan(stocktake.id(), String.valueOf(rollBarcodeId), storageAId);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/scans", stocktake.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(SCAN_LIST)
                          .value(list -> assertThat(list).hasSize(1));
        }

        @Test
        @DisplayName("filters scans by storage")
        void filtersScansByStorage() {
            FeltStocktakeDto stocktake = postStocktake(false);
            postScan(stocktake.id(), String.valueOf(rollBarcodeId), storageAId);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/scans?storageId={storageId}", stocktake.id(), storageAId)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(SCAN_LIST)
                          .value(list -> assertThat(list).hasSize(1));

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/scans?storageId={storageId}", stocktake.id(), storageBId)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(SCAN_LIST)
                          .value(list -> assertThat(list).isEmpty());
        }

        @Test
        @DisplayName("returns 404 when stocktake does not exist")
        void returns404ForUnknownStocktake() {
            restTestClient.get()
                          .uri("/api/stocktakes/99999/scans")
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }

    @Nested
    @DisplayName("GET /api/stocktakes/{id}/scans/{scanId}")
    class GetScanById {

        @Test
        @DisplayName("returns scan by id")
        void returnsScan() {
            FeltStocktakeDto stocktake = postStocktake(false);
            FeltStocktakeScanDto created = postScan(stocktake.id(), String.valueOf(rollBarcodeId), storageAId);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/scans/{scanId}", stocktake.id(), created.scanId())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeScanDto.class)
                          .value(scan -> assertThat(scan.scanId()).isEqualTo(created.scanId()));
        }

        @Test
        @DisplayName("returns 404 when scan does not exist")
        void returns404ForUnknownScan() {
            FeltStocktakeDto stocktake = postStocktake(false);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/scans/{scanId}", stocktake.id(), 99999)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 404 when scan does not belong to stocktake")
        void returns404ForScanOutsideStocktake() {
            FeltStocktakeDto stocktake1 = postStocktake(false);
            FeltStocktakeDto stocktake2 = postStocktake(false);
            FeltStocktakeScanDto created = postScan(stocktake1.id(), String.valueOf(rollBarcodeId), storageAId);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/scans/{scanId}", stocktake2.id(), created.scanId())
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }

    @Nested
    @DisplayName("POST /api/stocktakes/{id}/scans/{scanId}/void")
    class VoidScan {

        @Test
        @DisplayName("voids scan and returns 204")
        void voidsScan() {
            FeltStocktakeDto stocktake = postStocktake(false);
            FeltStocktakeScanDto created = postScan(stocktake.id(), String.valueOf(rollBarcodeId), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/scans/{scanId}/void", stocktake.id(), created.scanId())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/scans/{scanId}", stocktake.id(), created.scanId())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeScanDto.class)
                          .value(scan -> assertThat(scan.isVoided()).isTrue());
        }

        @Test
        @DisplayName("returns 404 when scan does not exist")
        void returns404ForUnknownScan() {
            FeltStocktakeDto stocktake = postStocktake(false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/scans/{scanId}/void", stocktake.id(), 99999)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 409 when stocktake is completed")
        void rejectsVoidOnCompleted() {
            FeltStocktakeDto stocktake = postStocktake(true);
            FeltStocktakeScanDto created = postScan(stocktake.id(), String.valueOf(rollBarcodeId), storageAId);

            closeStorage(stocktake.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", stocktake.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/scans/{scanId}/void", stocktake.id(), created.scanId())
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 when scan is locked")
        void rejectsLockedScan() {
            FeltStocktakeDto stocktake = postStocktake(false);
            FeltStocktakeScanDto created = postScan(stocktake.id(), "UNKNOWN-1", storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), created.itemId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE, null))
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/scans/{scanId}/void", stocktake.id(), created.scanId())
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }
    }
}
