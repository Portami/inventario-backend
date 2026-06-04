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
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.ExtendStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeItemRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeScanRepository;
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
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeltStocktakeControllerIntegrationTest extends BaseIntegrationTest {

    private static final ParameterizedTypeReference<List<FeltStocktakeDto>> STOCKTAKE_LIST =
            new ParameterizedTypeReference<>() {
            };

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private PlatformTransactionManager transactionManager;
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
    @Autowired
    private FeltStocktakeItemRepository stocktakeItemRepository;
    @Autowired
    private FeltStocktakeScanRepository stocktakeScanRepository;

    private Long storageAId;
    private Long storageBId;
    private Long supplierId;
    private Long feltTypeId;

    private Long rollId;
    private Long scrapId;
    private Long rollBarcodeId;
    private Long scrapBarcodeId;

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
    }

    private void createFeltInventory() {
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

        rollId = roll.getId();
        scrapId = scrap.getId();
        rollBarcodeId = barcodeRepository.save(Barcode.forRoll(roll))
                                         .getId();
        scrapBarcodeId = barcodeRepository.save(Barcode.forScrap(scrap))
                                          .getId();
    }

    private FeltStocktakeDto postStocktake(List<Long> storageIds, boolean includeScrap) {
        FeltStocktakeDto body = restTestClient.post()
                                              .uri("/api/stocktakes")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .body(new CreateFeltStocktakeDto("Initial count", includeScrap,
                                                      storageIds))
                                              .exchange()
                                              .expectStatus()
                                              .isCreated()
                                              .expectBody(FeltStocktakeDto.class)
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

    private void resolveProblem(Long stocktakeId, Long itemId, FeltStocktakeResolutionType resolutionType) {
        restTestClient.post()
                      .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktakeId, itemId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(new ResolveFeltStocktakeProblemDto(resolutionType, "Resolved"))
                      .exchange()
                      .expectStatus()
                      .isOk();
    }

    private Long findRollItemId(Long stocktakeId) {
        return stocktakeItemRepository.findByStocktakeIdAndRollId(stocktakeId, rollId)
                                      .orElseThrow()
                                      .getId();
    }

    private Long findScrapItemId(Long stocktakeId) {
        return stocktakeItemRepository.findByStocktakeIdAndScrapId(stocktakeId, scrapId)
                                      .orElseThrow()
                                      .getId();
    }

    @Nested
    @DisplayName("POST /api/stocktakes")
    class CreateStocktake {

        @Test
        @DisplayName("creates stocktake for selected storages")
        void createsStocktakeForSelectedStorage() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            assertThat(created.id()).isNotNull();
            assertThat(created.description()).isEqualTo("Initial count");
            assertThat(created.storageLists()).hasSize(1);
            assertThat(created.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("creates stocktake for all storages")
        void createsStocktakeForAllStorages() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(null, false);

            assertThat(created.id()).isNotNull();
            assertThat(created.description()).isEqualTo("Initial count");
            assertThat(created.storageLists()).hasSize(2);
            assertThat(created.isCompleted()).isFalse();
        }

        @Test
        @DisplayName("returns 400 when description is missing")
        void rejectsMissingDescription() {
            var invalid = new CreateFeltStocktakeDto(null, true, List.of(storageAId));

            restTestClient.post()
                          .uri("/api/stocktakes")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(invalid)
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getDetail()).contains("description"));
        }

        @Test
        @DisplayName("returns 422 when storage id is invalid")
        void rejectsUnknownStorage() {
            var invalid = new CreateFeltStocktakeDto("Count", true, List.of(99999L));

            restTestClient.post()
                          .uri("/api/stocktakes")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(invalid)
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                          .expectBody(ProblemDetail.class);
        }
    }

    @Nested
    @DisplayName("GET /api/stocktakes")
    class GetStocktakes {

        @Test
        @DisplayName("returns empty list when none exist")
        void returnsEmptyList() {
            restTestClient.get()
                          .uri("/api/stocktakes")
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(STOCKTAKE_LIST)
                          .value(list -> assertThat(list).isEmpty());
        }

        @Test
        @DisplayName("returns list of stocktakes")
        void returnsAllStocktakes() {
            postStocktake(List.of(storageAId), false);
            postStocktake(List.of(storageBId), false);

            restTestClient.get()
                          .uri("/api/stocktakes")
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(STOCKTAKE_LIST)
                          .value(list -> assertThat(list).hasSize(2));
        }
    }

    @Nested
    @DisplayName("GET /api/stocktakes/{id}")
    class GetStocktakeById {

        @Test
        @DisplayName("returns stocktake when it exists")
        void returnsStocktake() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeDto.class)
                          .value(dto -> assertThat(dto.id()).isEqualTo(created.id()));
        }

        @Test
        @DisplayName("returns 404 when stocktake does not exist")
        void returns404ForUnknown() {
            restTestClient.get()
                          .uri("/api/stocktakes/99999")
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ProblemDetail.class);
        }
    }

    @Nested
    @DisplayName("POST /api/stocktakes/{id}/extend")
    class ExtendStocktake {

        @Test
        @DisplayName("adds new storage to stocktake")
        void extendsStocktake() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/extend", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ExtendStocktakeDto(List.of(storageBId)))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeDto.class)
                          .value(dto -> assertThat(dto.storageLists()).hasSize(2));
        }

        @Test
        @DisplayName("adds all remaining storages when list is null")
        void extendsStocktakeWithNullBody() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/extend", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ExtendStocktakeDto(null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeDto.class)
                          .value(dto -> assertThat(dto.storageLists()).hasSize(2));
        }

        @Test
        @DisplayName("does nothing for empty storage list")
        void extendsStocktakeWithEmptyList() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/extend", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ExtendStocktakeDto(List.of()))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeDto.class)
                          .value(dto -> assertThat(dto.storageLists()).hasSize(1));
        }

        @Test
        @DisplayName("does nothing if the storage is already part of the stocktake")
        void extendsStocktakeWithExistingStorage() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/extend", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ExtendStocktakeDto(List.of(storageAId)))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeDto.class)
                          .value(dto -> assertThat(dto.storageLists()).hasSize(1));
        }

        @Test
        @DisplayName("returns 422 when storage id is invalid")
        void rejectsUnknownStorage() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/extend", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ExtendStocktakeDto(List.of(99999L)))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                          .expectBody(ProblemDetail.class);
        }

        @Test
        @DisplayName("returns 404 for unknown stocktake")
        void returns404ForUnknownStocktake() {
            restTestClient.post()
                          .uri("/api/stocktakes/99999/extend")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ExtendStocktakeDto(List.of(storageAId)))
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 409 when stocktake is completed")
        void rejectsExtendOnCompleted() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            closeStorage(created.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/extend", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ExtendStocktakeDto(List.of(storageBId)))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("POST /api/stocktakes/{id}/complete")
    class CompleteStocktake {

        @Test
        @DisplayName("returns 409 when storages are still open")
        void rejectsCompletionWithOpenStorage() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("completes stocktake once storages are closed")
        void completesStocktake() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);
            closeStorage(created.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeDto.class)
                          .value(dto -> assertThat(dto.isCompleted()).isTrue());
        }

        @Test
        @DisplayName("returns 409 when unresolved wrong storage problem exists")
        void rejectsCompletionWithUnresolvedWrongStorage() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId, storageBId), false);
            postScan(created.id(), String.valueOf(rollBarcodeId), storageBId);

            closeStorage(created.id(), storageAId);
            closeStorage(created.id(), storageBId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 when unresolved missing roll exists")
        void rejectsCompletionWithUnresolvedMissingRoll() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);
            closeStorage(created.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("applies adjust storage resolution for rolls")
        void appliesAdjustStorageForRoll() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId, storageBId), false);
            postScan(created.id(), String.valueOf(rollBarcodeId), storageBId);

            resolveProblem(created.id(), findRollItemId(created.id()), FeltStocktakeResolutionType.ADJUST_STORAGE);

            closeStorage(created.id(), storageAId);
            closeStorage(created.id(), storageBId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
                FeltRoll updated = feltRollRepository.findById(rollId)
                                                     .orElseThrow();
                assertThat(updated.getStorage()
                                  .getId()).isEqualTo(storageBId);
            });
        }

        @Test
        @DisplayName("does not change storage when move physically resolution is chosen for rolls")
        void doesNotChangeStorageOnMovePhysicallyForRoll() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId, storageBId), false);
            postScan(created.id(), String.valueOf(rollBarcodeId), storageBId);

            resolveProblem(created.id(), findRollItemId(created.id()), FeltStocktakeResolutionType.MOVE_PHYSICALLY);

            closeStorage(created.id(), storageAId);
            closeStorage(created.id(), storageBId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
                FeltRoll updated = feltRollRepository.findById(rollId)
                                                     .orElseThrow();
                assertThat(updated.getStorage()
                                  .getId()).isEqualTo(storageAId);
            });
        }

        @Test
        @DisplayName("applies remove missing resolution for rolls")
        void appliesRemoveMissingForRoll() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);
            closeStorage(created.id(), storageAId);

            resolveProblem(created.id(), findRollItemId(created.id()), FeltStocktakeResolutionType.REMOVE_MISSING);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            assertThat(feltRollRepository.existsById(rollId)).isFalse();
        }

        @Test
        @DisplayName("does not remove roll when ignore resolution is chosen")
        void doesNotRemoveRollOnIgnore() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);
            closeStorage(created.id(), storageAId);

            resolveProblem(created.id(), findRollItemId(created.id()), FeltStocktakeResolutionType.IGNORE_MISSING);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            assertThat(feltRollRepository.existsById(rollId)).isTrue();
        }

        @Test
        @DisplayName("applies adjust storage resolution for scrap")
        void appliesAdjustStorageForScrap() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId, storageBId), true);
            postScan(created.id(), String.valueOf(rollBarcodeId), storageAId);
            postScan(created.id(), String.valueOf(scrapBarcodeId), storageAId);

            resolveProblem(created.id(), findScrapItemId(created.id()), FeltStocktakeResolutionType.ADJUST_STORAGE);

            closeStorage(created.id(), storageAId);
            closeStorage(created.id(), storageBId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            new TransactionTemplate(transactionManager).executeWithoutResult(status -> {
                ScrapPiece updated = scrapPieceRepository.findById(scrapId)
                                                         .orElseThrow();
                assertThat(updated.getStorage()
                                  .getId()).isEqualTo(storageAId);
            });
        }

        @Test
        @DisplayName("applies remove missing resolution for scrap")
        void appliesRemoveMissingForScrap() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageBId), true);
            closeStorage(created.id(), storageBId);

            resolveProblem(created.id(), findScrapItemId(created.id()), FeltStocktakeResolutionType.REMOVE_MISSING);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            assertThat(scrapPieceRepository.existsById(scrapId)).isFalse();
        }

        @Test
        @DisplayName("marks wrong-storage scans corrected when move physically is chosen")
        void marksWrongStorageScansCorrectedOnMovePhysically() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageBId), false);
            FeltStocktakeScanDto scan = postScan(created.id(), String.valueOf(rollBarcodeId), storageBId);

            resolveProblem(created.id(), findRollItemId(created.id()), FeltStocktakeResolutionType.MOVE_PHYSICALLY);

            closeStorage(created.id(), storageBId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            assertThat(stocktakeScanRepository.findByStocktakeIdAndId(created.id(), scan.scanId()))
                    .hasValueSatisfying(saved -> assertThat(saved.isCorrected()).isTrue());
        }

        @Test
        @DisplayName("removes voided scans on resolution")
        void removesVoidedScansOnResolution() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);
            FeltStocktakeScanDto scanToVoid = postScan(created.id(), String.valueOf(rollBarcodeId), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/scans/{scanId}/void", created.id(), scanToVoid.scanId())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            postScan(created.id(), String.valueOf(rollBarcodeId), storageAId);

            closeStorage(created.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            assertThat(stocktakeScanRepository.findByStocktakeIdAndId(created.id(), scanToVoid.scanId())).isEmpty();
        }

        @Test
        @DisplayName("removes items not used in stocktake on completion")
        void removesUnusedItems() {
            createFeltInventory();

            FeltStocktakeDto created = postStocktake(List.of(storageBId), false);

            closeStorage(created.id(), storageBId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            assertThat(stocktakeItemRepository.findByStocktakeIdAndRollId(created.id(), rollId)).isEmpty();
        }
    }

    @Nested
    @DisplayName("DELETE /api/stocktakes/{id}")
    class DeleteStocktake {

        @Test
        @DisplayName("deletes stocktake and returns 204")
        void deletesStocktake() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId), false);

            restTestClient.delete()
                          .uri("/api/stocktakes/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            restTestClient.get()
                          .uri("/api/stocktakes/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }
}

