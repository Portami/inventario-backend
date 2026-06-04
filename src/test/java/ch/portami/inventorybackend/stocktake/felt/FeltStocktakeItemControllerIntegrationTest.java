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
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemApiStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.dto.item.ResolveFeltStocktakeProblemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.CreateFeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.CreateFeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeItemRepository;
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
class FeltStocktakeItemControllerIntegrationTest extends BaseIntegrationTest {

    private static final ParameterizedTypeReference<List<FeltStocktakeItemDto>> ITEM_LIST =
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
    @Autowired
    private FeltStocktakeItemRepository stocktakeItemRepository;

    private Long storageAId;
    private Long storageBId;
    private Long supplierId;
    private Long feltTypeId;

    private Long rollBarcodeId;
    private Long rollId;

    private record RollBarcodeIds(Long rollId, Long barcodeId) {

    }

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
        scrapPieceRepository.save(
                new ScrapPiece(felt, null, storageRepository.getReferenceById(storageBId), 50.0, 50.0));

        rollId = roll.getId();
        rollBarcodeId = barcodeRepository.save(Barcode.forRoll(roll))
                                         .getId();
    }

    private FeltStocktakeDto postStocktake(boolean includeScrap, List<Long> storageIds) {
        FeltStocktakeDto body = restTestClient.post()
                                              .uri("/api/stocktakes")
                                              .contentType(MediaType.APPLICATION_JSON)
                                              .body(new CreateFeltStocktakeDto("Item run", includeScrap, storageIds))
                                              .exchange()
                                              .expectStatus()
                                              .isCreated()
                                              .expectBody(FeltStocktakeDto.class)
                                              .returnResult()
                                              .getResponseBody();
        assertThat(body).isNotNull();
        return body;
    }

    private FeltStocktakeDto postStocktake(boolean includeScrap) {
        return postStocktake(includeScrap, List.of(storageAId, storageBId));
    }

    private void createScan(Long stocktakeId, Long storageId, String barcode) {
        restTestClient.post()
                      .uri("/api/stocktakes/{id}/scans", stocktakeId)
                      .contentType(MediaType.APPLICATION_JSON)
                      .body(new CreateFeltStocktakeScanDto(barcode, storageId))
                      .exchange()
                      .expectStatus()
                      .isCreated();
    }

    private FeltStocktakeItem findRollItem(Long stocktakeId) {
        return stocktakeItemRepository.findByStocktakeIdAndRollId(stocktakeId, rollId)
                                      .orElseThrow();
    }

    private FeltStocktakeItem findBarcodeItem(Long stocktakeId, String barcode) {
        return stocktakeItemRepository.findByStocktakeIdAndBarcode(stocktakeId, barcode)
                                      .orElseThrow();
    }

    private RollBarcodeIds createAdditionalRollWithBarcode(Long storageId) {
        Felt felt = feltRepository.save(new Felt(
                feltTypeRepository.getReferenceById(feltTypeId),
                supplierRepository.getReferenceById(supplierId),
                "ART-NEW",
                2.2,
                280.0,
                new BigDecimal("9.90"),
                "Blue",
                "Supplier Blue"
        ));
        FeltRoll roll = feltRollRepository.save(
                new FeltRoll(felt, null, storageRepository.getReferenceById(storageId), 8.0, 1.2));
        Barcode barcode = barcodeRepository.save(Barcode.forRoll(roll));
        return new RollBarcodeIds(roll.getId(), barcode.getId());
    }

    private void closeStorage(Long stocktakeId, Long storageId) {
        restTestClient.post()
                      .uri("/api/stocktakes/{id}/storages/{storageId}/close", stocktakeId, storageId)
                      .exchange()
                      .expectStatus()
                      .isNoContent();
    }

    @Nested
    @DisplayName("GET /api/stocktakes/{id}/items")
    class GetItems {

        @Test
        @DisplayName("returns items for stocktake")
        void returnsItems() {
            FeltStocktakeDto stocktake = postStocktake(true);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items", stocktake.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ITEM_LIST)
                          .value(list -> assertThat(list).hasSize(2));
        }

        @Test
        @DisplayName("filters items by storage")
        void filtersItemsByStorage() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, "UNKNOWN-1");

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items?storageId={storageId}", stocktake.id(), storageBId)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ITEM_LIST)
                          .value(list -> assertThat(list).hasSize(1));

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items?storageId={storageId}", stocktake.id(), storageAId)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ITEM_LIST)
                          .value(list -> assertThat(list).hasSize(1));
        }

        @Test
        @DisplayName("returns item scanned in wrong storage both for expected storage and actual storage")
        void returnsWrongStorageItemForBothStorages() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, String.valueOf(rollBarcodeId));

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items?storageId={storageId}", stocktake.id(), storageBId)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ITEM_LIST)
                          .value(list -> assertThat(list).hasSize(1)
                                                         .extracting(FeltStocktakeItemDto::status)
                                                         .containsExactly(FeltStocktakeItemApiStatus.WRONG_STORAGE));

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items?storageId={storageId}", stocktake.id(), storageAId)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ITEM_LIST)
                          .value(list -> assertThat(list).hasSize(1)
                                                         .extracting(FeltStocktakeItemDto::status)
                                                         .containsExactly(FeltStocktakeItemApiStatus.WRONG_STORAGE));
        }

        @Test
        @DisplayName("returns empty list when no items found")
        void returnsEmptyList() {
            FeltStocktakeDto stocktake = postStocktake(false);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items?storageId={storageId}", stocktake.id(), 999L)
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ITEM_LIST)
                          .value(list -> assertThat(list).isEmpty());
        }

        @Test
        @DisplayName("does not return items for storages not in stocktake")
        void doesNotReturnItemsForNonIncludedStorages() {
            FeltStocktakeDto stocktake = postStocktake(false, List.of(storageBId));

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items", stocktake.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(ITEM_LIST)
                          .value(list -> assertThat(list).isEmpty());
        }

        @Test
        @DisplayName("returns 404 for non-existing stocktake")
        void returnsNotFound() {
            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items", 999L)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }

    @Nested
    @DisplayName("GET /api/stocktakes/{id}/items/{itemId}")
    class GetItemById {

        @Test
        @DisplayName("returns stocktake item")
        void returnsItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items/{itemId}", stocktake.id(), item.getId())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> assertThat(dto.itemId()).isEqualTo(item.getId()));
        }

        @Test
        @DisplayName("returns 404 for non-existing item")
        void returnsNotFound() {
            FeltStocktakeDto stocktake = postStocktake(false);

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items/{itemId}", stocktake.id(), 999L)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 404 for item not belonging to stocktake")
        void returnsNotFoundForWrongStocktake() {
            FeltStocktakeDto stocktake1 = postStocktake(false);
            FeltStocktakeDto stocktake2 = postStocktake(false);
            FeltStocktakeItem item = findRollItem(stocktake1.id());

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items/{itemId}", stocktake2.id(), item.getId())
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }

    @Nested
    @DisplayName("POST /api/stocktakes/{id}/items/{itemId}/resolve")
    class ResolveItem {

        @Test
        @DisplayName("acknowledges unknown barcode item")
        void acknowledgesUnknownItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, "UNKNOWN-1");
            FeltStocktakeItem item = findBarcodeItem(stocktake.id(), "UNKNOWN-1");

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items/{itemId}", stocktake.id(), item.getId())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> assertThat(dto.status()).isEqualTo(FeltStocktakeItemApiStatus.UNKNOWN));

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE, "Checked"))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> {
                              assertThat(dto.resolution()).isNotNull();
                              assertThat(dto.resolution()
                                            .comment()).isEqualTo("Checked");
                          });
        }

        @Test
        @DisplayName("resolves wrong storage by adjusting storage")
        void resolvesWrongStorageWithAdjustStorage() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, String.valueOf(rollBarcodeId));
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ADJUST_STORAGE, "Fix"))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> {
                              assertThat(dto.status()).isEqualTo(FeltStocktakeItemApiStatus.WRONG_STORAGE);
                              assertThat(dto.resolution()).isNotNull();
                              assertThat(dto.resolution()
                                            .resolution()).isEqualTo(FeltStocktakeResolutionType.ADJUST_STORAGE);
                              assertThat(dto.resolution()
                                            .newStorageId()).isEqualTo(storageBId);
                          });
        }

        @Test
        @DisplayName("resolves wrong storage by moving physically")
        void resolvesWrongStorageWithMovePhysically() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, String.valueOf(rollBarcodeId));
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.MOVE_PHYSICALLY, "Move"))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> {
                              assertThat(dto.status()).isEqualTo(FeltStocktakeItemApiStatus.RESCAN_REQUIRED);
                              assertThat(dto.resolution()).isNotNull();
                              assertThat(dto.resolution()
                                            .resolution()).isEqualTo(FeltStocktakeResolutionType.MOVE_PHYSICALLY);
                              assertThat(dto.resolution()
                                            .newStorageId()).isEqualTo(storageAId);
                          });
        }

        @Test
        @DisplayName("resolves missing item by ignoring it")
        void resolvesMissingWithIgnore() {
            FeltStocktakeDto stocktake = postStocktake(false);
            closeStorage(stocktake.id(), storageAId);
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.IGNORE_MISSING,
                                  "Ignore"))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> {
                              assertThat(dto.status()).isEqualTo(FeltStocktakeItemApiStatus.MISSING);
                              assertThat(dto.resolution()).isNotNull();
                              assertThat(dto.resolution()
                                            .resolution()).isEqualTo(FeltStocktakeResolutionType.IGNORE_MISSING);
                          });
        }

        @Test
        @DisplayName("resolves missing item by removing it")
        void resolvesMissingWithRemove() {
            FeltStocktakeDto stocktake = postStocktake(false);
            closeStorage(stocktake.id(), storageAId);
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.REMOVE_MISSING,
                                  "Remove"))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> {
                              assertThat(dto.status()).isEqualTo(FeltStocktakeItemApiStatus.MISSING);
                              assertThat(dto.resolution()).isNotNull();
                              assertThat(dto.resolution()
                                            .resolution()).isEqualTo(FeltStocktakeResolutionType.REMOVE_MISSING);
                          });
        }

        @Test
        @DisplayName("acknowledges not-in-stocktake item")
        void acknowledgesNotInStocktakeItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            RollBarcodeIds extraRoll = createAdditionalRollWithBarcode(storageAId);
            createScan(stocktake.id(), storageAId, String.valueOf(extraRoll.barcodeId()));
            FeltStocktakeItem item = stocktakeItemRepository.findByStocktakeIdAndRollId(stocktake.id(),
                                                                    extraRoll.rollId())
                                                            .orElseThrow();

            restTestClient.get()
                          .uri("/api/stocktakes/{id}/items/{itemId}", stocktake.id(), item.getId())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> assertThat(dto.status()).isEqualTo(
                                  FeltStocktakeItemApiStatus.NOT_IN_STOCKTAKE));

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE, "Ack"))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> {
                              assertThat(dto.resolution()).isNotNull();
                              assertThat(dto.resolution()
                                            .resolution()).isEqualTo(FeltStocktakeResolutionType.ACKNOWLEDGE);
                          });
        }

        @Test
        @DisplayName("returns 404 for non-existing item")
        void returnsNotFound() {
            FeltStocktakeDto stocktake = postStocktake(false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), 999L)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE, "Checked"))
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 409 for invalid resolution")
        void rejectsInvalidResolution() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, "UNKNOWN-1");
            FeltStocktakeItem item = findBarcodeItem(stocktake.id(), "UNKNOWN-1");

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.REMOVE_MISSING, "Wrong"))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 for unscanned item in non-closed storage")
        void rejectsNonProblemItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE, "Checked"))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 when problem is already resolved")
        void rejectsAlreadyResolvedItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, "UNKNOWN-1");
            FeltStocktakeItem item = findBarcodeItem(stocktake.id(), "UNKNOWN-1");

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE, "Checked"))
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE,
                                  "Checked again"))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 for ok item")
        void rejectsOkItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageAId, String.valueOf(rollBarcodeId));
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE, "No issue"))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 for duplicate scan item")
        void rejectsDuplicateScanItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageAId, String.valueOf(rollBarcodeId));
            createScan(stocktake.id(), storageAId, String.valueOf(rollBarcodeId));
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE,
                                  "Duplicate"))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 for rescan-required item")
        void rejectsRescanRequiredItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, String.valueOf(rollBarcodeId));
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.MOVE_PHYSICALLY,
                                  "Move"))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> assertThat(dto.status()).isEqualTo(FeltStocktakeItemApiStatus.RESCAN_REQUIRED));

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.MOVE_PHYSICALLY,
                                  "Move again"))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 for acknowledge resolution for item in wrong storage")
        void rejectsAcknowledgeForWrongStorage() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, String.valueOf(rollBarcodeId));
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE,
                                  "Ack"))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

        @Test
        @DisplayName("returns 409 for acknowledge resolution for missing item")
        void rejectsAcknowledgeForMissingItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            closeStorage(stocktake.id(), storageAId);
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE,
                                  "Ack"))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }

    }

    @Nested
    @DisplayName("POST /api/stocktakes/{id}/items/{itemId}/unresolve")
    class UnresolveItem {

        @Test
        @DisplayName("clears resolution data")
        void clearsResolution() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, "UNKNOWN-1");
            FeltStocktakeItem item = findBarcodeItem(stocktake.id(), "UNKNOWN-1");

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/resolve", stocktake.id(), item.getId())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new ResolveFeltStocktakeProblemDto(FeltStocktakeResolutionType.ACKNOWLEDGE, "Checked"))
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/unresolve", stocktake.id(), item.getId())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> {
                              assertThat(dto.resolution()).isNull();
                          });
        }

        @Test
        @DisplayName("does nothing when item problem is not resolved")
        void unresolveNonResolvedItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageBId, "UNKNOWN-1");
            FeltStocktakeItem item = findBarcodeItem(stocktake.id(), "UNKNOWN-1");

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/unresolve", stocktake.id(), item.getId())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> assertThat(dto.resolution()).isNull());
        }

        @Test
        @DisplayName("does nothing when the item has no problem")
        void unresolveNonProblemItem() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageAId, String.valueOf(rollBarcodeId));
            FeltStocktakeItem item = findRollItem(stocktake.id());

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/unresolve", stocktake.id(), item.getId())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltStocktakeItemDto.class)
                          .value(dto -> assertThat(dto.resolution()).isNull());
        }

        @Test
        @DisplayName("returns 404 for non-existing item")
        void returnsNotFound() {
            FeltStocktakeDto stocktake = postStocktake(false);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/unresolve", stocktake.id(), 999L)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 409 when stocktake is completed")
        void rejectsUnresolveOnCompleted() {
            FeltStocktakeDto stocktake = postStocktake(false);
            createScan(stocktake.id(), storageAId, String.valueOf(rollBarcodeId));
            FeltStocktakeItem item = findRollItem(stocktake.id());

            closeStorage(stocktake.id(), storageAId);
            closeStorage(stocktake.id(), storageBId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", stocktake.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/items/{itemId}/unresolve", stocktake.id(), item.getId())
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }
    }
}

