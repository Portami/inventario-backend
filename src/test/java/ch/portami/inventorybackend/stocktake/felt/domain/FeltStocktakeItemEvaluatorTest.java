package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeResolutionType;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import ch.portami.inventorybackend.storage.entity.Storage;
import java.math.BigDecimal;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class FeltStocktakeItemEvaluatorTest {

    private final FeltStocktakeItemEvaluator evaluator = new FeltStocktakeItemEvaluator();

    private Storage createStorage(Long id) {
        Storage storage = new Storage("S-" + id);
        ReflectionTestUtils.setField(storage, "id", id);
        return storage;
    }

    private FeltStocktakeItem createItemWithRoll(Storage expectedStorage, Storage currentStorage) {
        FeltStocktakeItem item = new FeltStocktakeItem(new FeltStocktake("test"));
        FeltRoll roll = new FeltRoll(null, null, currentStorage, 10.0, 1.0);
        FeltStocktakeRollOrScrap rollOrScrap = new FeltStocktakeRollOrScrap(item, expectedStorage, 10.0, 1.0,
                "red", 2.0, 0.8, BigDecimal.ONE, "ART", "TYPE", "SUP", roll);
        item.setRollOrScrap(rollOrScrap);
        return item;
    }

    private FeltStocktakeItem createItemWithScrap(Storage expectedStorage, Storage currentStorage) {
        FeltStocktakeItem item = new FeltStocktakeItem(new FeltStocktake("test"));
        ScrapPiece scrap = new ScrapPiece(null, null, currentStorage, 50.0, 50.0);
        FeltStocktakeRollOrScrap rollOrScrap = new FeltStocktakeRollOrScrap(item, expectedStorage, 50.0, 50.0,
                "blue", 3.0, 1.0, BigDecimal.TEN, "ART2", "TYPE2", "SUP2", scrap);
        item.setRollOrScrap(rollOrScrap);
        return item;
    }

    private FeltStocktakeScan createScan(FeltStocktakeItem item, Storage scannedStorage, String barcode) {
        return new FeltStocktakeScan(item.getStocktake(), item, barcode, scannedStorage);
    }

    @Test
    void returnsDuplicateScanWhenMultipleActiveScans() {
        Storage storage = createStorage(1L);
        FeltStocktakeItem item = createItemWithRoll(storage, storage);

        item.addScan(createScan(item, storage, "A"));
        item.addScan(createScan(item, storage, "A"));

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(storage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.DUPLICATE_SCAN);
        assertThat(evaluation.needsResolution()).isTrue();
        assertThat(evaluation.resolutionType()).isNull();
    }

    @Test
    void ignoresVoidedAndCorrectedScans() {
        Storage storage = createStorage(1L);
        FeltStocktakeItem item = createItemWithRoll(storage, storage);

        FeltStocktakeScan voidedScan = createScan(item, storage, "VOID");
        voidedScan.setVoided(true);
        FeltStocktakeScan correctedScan = createScan(item, storage, "CORR");
        correctedScan.setCorrected(true);
        item.addScan(voidedScan);
        item.addScan(correctedScan);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(storage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.INITIAL);
        assertThat(evaluation.needsResolution()).isFalse();
        assertThat(evaluation.resolutionType()).isNull();
    }

    @Test
    void returnsUnknownForUnknownItem() {
        FeltStocktakeItem item = new FeltStocktakeItem(new FeltStocktake("test"), "UNKNOWN_BARCODE");

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(1L, false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.UNKNOWN);
        assertThat(evaluation.resolutionType()).isNull();
        assertThat(evaluation.needsResolution()).isTrue();
    }

    @Test
    void acknowledgesUnknownItemWithResolutionWhenRequested() {
        Storage storage = createStorage(1L);
        FeltStocktakeItem item = new FeltStocktakeItem(new FeltStocktake("test"));
        item.setProblemAcknowledged(true);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(storage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.UNKNOWN);
        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.ACKNOWLEDGE);
        assertThat(evaluation.mutationApplied()).isFalse();
    }

    @Test
    void returnsNotInStocktakeWithoutResolutionWhenExpectedStorageMissing() {
        Storage currentStorage = createStorage(2L);
        FeltStocktakeItem item = createItemWithRoll(null, currentStorage);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(1L, false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.NOT_IN_STOCKTAKE);
        assertThat(evaluation.resolutionType()).isNull();
        assertThat(evaluation.needsResolution()).isTrue();
    }

    @Test
    void returnsNotInStocktakeWithResolutionWhenExpectedStorageMissing() {
        Storage currentStorage = createStorage(2L);
        FeltStocktakeItem item = createItemWithRoll(null, currentStorage);
        item.setProblemAcknowledged(true);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(1L, false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.NOT_IN_STOCKTAKE);
        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.ACKNOWLEDGE);
        assertThat(evaluation.needsResolution()).isFalse();
        assertThat(evaluation.mutationApplied()).isFalse();
    }

    @Test
    void returnsInitialWhenNoScansAndStorageOpen() {
        Storage storage = createStorage(1L);
        FeltStocktakeItem item = createItemWithRoll(storage, storage);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(storage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.INITIAL);
        assertThat(evaluation.needsResolution()).isFalse();
    }

    @Test
    void returnsMissingWhenNoScansAndStorageClosed() {
        Storage storage = createStorage(1L);
        FeltStocktakeItem item = createItemWithRoll(storage, storage);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(storage.getId(), true));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.MISSING);
        assertThat(evaluation.resolutionType()).isNull();
        assertThat(evaluation.needsResolution()).isTrue();
    }

    @Test
    void returnsMissingWithIgnoreWhenNoScansAndStorageClosed() {
        Storage storage = createStorage(1L);
        FeltStocktakeItem item = createItemWithRoll(storage, storage);
        item.setProblemAcknowledged(true);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(storage.getId(), true));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.MISSING);
        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.IGNORE_MISSING);
        assertThat(evaluation.needsResolution()).isFalse();
        assertThat(evaluation.mutationApplied()).isFalse();
    }

    @Test
    void returnsMissingWithRemoveWhenNoScansAndStorageClosed() {
        Storage storage = createStorage(1L);
        FeltStocktakeItem item = createItemWithRoll(storage, storage);
        item.setProblemAcknowledged(true);
        item.setMutationWanted(true);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(storage.getId(), true));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.MISSING);
        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.REMOVE_MISSING);
        assertThat(evaluation.needsResolution()).isFalse();
        assertThat(evaluation.mutationApplied()).isTrue();
    }

    @Test
    void returnsOkWhenScanMatchesExpectedStorage() {
        Storage storage = createStorage(1L);
        FeltStocktakeItem item = createItemWithRoll(storage, storage);
        item.addScan(createScan(item, storage, "OK"));

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(storage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.OK);
        assertThat(evaluation.needsResolution()).isFalse();
    }

    @Test
    void returnsWrongStorageWithoutResolutionWhenScanDoesNotMatchExpectedStorage() {
        Storage expectedStorage = createStorage(1L);
        Storage scannedStorage = createStorage(2L);
        FeltStocktakeItem item = createItemWithRoll(expectedStorage, expectedStorage);
        item.addScan(createScan(item, scannedStorage, "WRONG"));

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false,
                Map.of(scannedStorage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.WRONG_STORAGE);
        assertThat(evaluation.resolutionType()).isNull();
        assertThat(evaluation.needsResolution()).isTrue();
    }

    @Test
    void returnsWrongStorageWithAdjustStorageResolution() {
        Storage expectedStorage = createStorage(1L);
        Storage scannedStorage = createStorage(2L);
        Storage newStorage = createStorage(3L);
        FeltStocktakeItem item = createItemWithRoll(expectedStorage, expectedStorage);
        item.addScan(createScan(item, scannedStorage, "WRONG"));
        item.setProblemAcknowledged(true);
        item.setMutationWanted(true);
        item.setNewStorage(newStorage);
        item.setResolutionComment("Move in system");

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false,
                Map.of(scannedStorage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.WRONG_STORAGE);
        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.ADJUST_STORAGE);
        assertThat(evaluation.newStorage()).isEqualTo(newStorage);
        assertThat(evaluation.mutationApplied()).isTrue();
        assertThat(evaluation.resolutionComment()).isEqualTo("Move in system");
    }

    @Test
    void convertsWrongStorageToRescanRequiredWhenExpectedStorageInStocktake() {
        Storage expectedStorage = createStorage(1L);
        Storage scannedStorage = createStorage(2L);
        FeltStocktakeItem item = createItemWithScrap(expectedStorage, expectedStorage);
        item.addScan(createScan(item, scannedStorage, "WRONG"));
        item.setProblemAcknowledged(true);
        item.setMutationWanted(false);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false,
                Map.of(scannedStorage.getId(), false, expectedStorage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.RESCAN_REQUIRED);
        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.MOVE_PHYSICALLY);
        assertThat(evaluation.newStorage()).isEqualTo(expectedStorage);
    }

    @Test
    void keepsWrongStorageWhenExpectedStorageNotInStocktake() {
        Storage expectedStorage = createStorage(1L);
        Storage scannedStorage = createStorage(2L);
        FeltStocktakeItem item = createItemWithRoll(expectedStorage, expectedStorage);
        item.addScan(createScan(item, scannedStorage, "WRONG"));
        item.setProblemAcknowledged(true);
        item.setMutationWanted(false);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false, Map.of(scannedStorage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.WRONG_STORAGE);
        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.MOVE_PHYSICALLY);
        assertThat(evaluation.newStorage()).isEqualTo(expectedStorage);
    }

    @Test
    void convertsToWrongStorageWithResolutionWhenItemWasRescanned() {
        Storage expectedStorage = createStorage(1L);
        Storage scannedStorage = createStorage(2L);
        FeltStocktakeItem item = createItemWithRoll(expectedStorage, expectedStorage);
        FeltStocktakeScan initialScan = createScan(item, scannedStorage, "WRONG");
        item.addScan(initialScan);
        item.setProblemAcknowledged(true);
        item.setMutationWanted(false);

        item.addScan(createScan(item, expectedStorage, "RESCAN"));
        initialScan.setCorrected(true);

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, false,
                Map.of(expectedStorage.getId(), false, scannedStorage.getId(), false));

        assertThat(evaluation.status()).isEqualTo(FeltStocktakeItemStatus.WRONG_STORAGE);
        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.MOVE_PHYSICALLY);
        assertThat(evaluation.newStorage()).isEqualTo(expectedStorage);
        assertThat(evaluation.mutationApplied()).isFalse();
        assertThat(evaluation.needsResolution()).isFalse();
    }

    @Test
    void usesMutationAppliedFlagWhenStocktakeCompleted() {
        Storage expectedStorage = createStorage(1L);
        Storage scannedStorage = createStorage(2L);
        Storage newStorage = createStorage(3L);
        FeltStocktakeItem item = createItemWithRoll(expectedStorage, expectedStorage);
        item.addScan(createScan(item, scannedStorage, "WRONG"));
        item.setProblemAcknowledged(true);
        item.setMutationWanted(true);
        item.setNewStorage(newStorage);
        item.setMutationApplied(false); // Mutation was not applied although it was wanted

        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, true, Map.of(expectedStorage.getId(), false, scannedStorage.getId(), false, newStorage.getId(), false));

        assertThat(evaluation.resolutionType()).isEqualTo(FeltStocktakeResolutionType.ADJUST_STORAGE);
        assertThat(evaluation.mutationApplied()).isFalse();
    }

}

