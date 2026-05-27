package ch.portami.inventorybackend.stocktake.felt.mapper;

import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluation;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluator;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemTypeResolver;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemDto;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeRollOrScrapDto;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import ch.portami.inventorybackend.storage.entity.Storage;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class FeltStocktakeItemMapper {

    private final FeltStocktakeItemEvaluator evaluator;
    private final FeltStocktakeScanMapper scanAssembler;

    public FeltStocktakeItemMapper(FeltStocktakeItemEvaluator evaluator, FeltStocktakeScanMapper scanAssembler) {
        this.evaluator = evaluator;
        this.scanAssembler = scanAssembler;
    }

    public FeltStocktakeItemDto toDto(FeltStocktakeItem item, List<FeltStocktakeScan> scans,
            boolean stocktakeCompleted, boolean expectedStorageClosed, Set<Long> stocktakeStorageIds) {
        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, scans, stocktakeCompleted,
                expectedStorageClosed, stocktakeStorageIds);

        FeltStocktakeItemStatus status = evaluation.status();
        FeltStocktakeRollOrScrap rollOrScrap = item.getRollOrScrap();
        Storage expectedStorage = rollOrScrap != null ? rollOrScrap.getExpectedStorage() : null;

        Long expectedStorageId = null;
        String expectedStorageName = null;
        if (expectedStorage != null && status != FeltStocktakeItemStatus.NOT_IN_STOCKTAKE
                && status != FeltStocktakeItemStatus.UNKNOWN) {
            expectedStorageId = expectedStorage.getId();
            expectedStorageName = expectedStorage.getName();
        }

        List<FeltStocktakeScan> scansToExpose = scans;
        if (stocktakeCompleted) {
            scansToExpose = scans.stream()
                                 .filter(scan -> !Boolean.TRUE.equals(scan.isVoided()))
                                 .toList();
        }

        return new FeltStocktakeItemDto(
                FeltStocktakeItemTypeResolver.resolve(item),
                item.getId(),
                rollOrScrap != null ? toRollOrScrapDto(rollOrScrap) : null,
                item.getBarcode(),
                expectedStorageId,
                expectedStorageName,
                status,
                evaluation.needsResolution(),
                evaluation.resolutionDto(),
                scansToExpose.stream()
                             .map(scanAssembler::toDto)
                             .toList()
        );
    }

    private FeltStocktakeRollOrScrapDto toRollOrScrapDto(FeltStocktakeRollOrScrap rollOrScrap) {

        Storage expectedStorage = rollOrScrap.getExpectedStorage();

        Long rollOrScrapId = null;
        Long feltId = null;

        if (rollOrScrap.getRoll() != null) {
            rollOrScrapId = rollOrScrap.getRoll()
                                       .getId();
            feltId = rollOrScrap.getRoll()
                                .getFelt()
                                .getId();
        } else if (rollOrScrap.getScrap() != null) {
            rollOrScrapId = rollOrScrap.getScrap()
                                       .getId();
            feltId = rollOrScrap.getScrap()
                                .getFelt()
                                .getId();
        }

        return new FeltStocktakeRollOrScrapDto(
                rollOrScrapId,
                rollOrScrap.getLength(),
                rollOrScrap.getWidth(),
                feltId,
                rollOrScrap.getColor(),
                rollOrScrap.getThickness(),
                rollOrScrap.getDensity(),
                rollOrScrap.getPrice(),
                rollOrScrap.getArticleNumber(),
                rollOrScrap.getFeltTypeName(),
                rollOrScrap.getSupplierName(),
                expectedStorage != null ? expectedStorage.getId() : null,
                expectedStorage != null ? expectedStorage.getName() : null
        );

    }

}