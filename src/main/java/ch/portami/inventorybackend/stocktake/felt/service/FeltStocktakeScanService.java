package ch.portami.inventorybackend.stocktake.felt.service;

import ch.portami.inventorybackend.barcode.entity.Barcode;
import ch.portami.inventorybackend.barcode.repository.BarcodeRepository;
import ch.portami.inventorybackend.core.storage.entity.Storage;
import ch.portami.inventorybackend.core.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.core.storage.repository.StorageRepository;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluation;
import ch.portami.inventorybackend.stocktake.felt.domain.FeltStocktakeItemEvaluator;
import ch.portami.inventorybackend.stocktake.felt.dto.item.FeltStocktakeItemStatus;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.CreateFeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.dto.scan.FeltStocktakeScanDto;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeScan;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeStorage;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeCompletedException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeScanLockedException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeScanNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.exception.FeltStocktakeStorageNotFoundException;
import ch.portami.inventorybackend.stocktake.felt.mapper.FeltStocktakeScanMapper;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeItemRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRollOrScrapRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeScanRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeStorageRepository;
import jakarta.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeltStocktakeScanService {

    private final FeltStocktakeRepository stocktakeRepo;
    private final FeltStocktakeItemRepository itemRepo;
    private final FeltStocktakeRollOrScrapRepository rollOrScrapRepo;
    private final FeltStocktakeScanRepository scanRepo;
    private final FeltStocktakeStorageRepository stocktakeStorageRepo;
    private final StorageRepository storageRepo;
    private final BarcodeRepository barcodeRepo;
    private final FeltStocktakeItemEvaluator evaluator;
    private final FeltStocktakeScanMapper scanMapper;

    public FeltStocktakeScanService(FeltStocktakeRepository stocktakeRepo,
            FeltStocktakeItemRepository itemRepo,
            FeltStocktakeRollOrScrapRepository rollOrScrapRepo,
            FeltStocktakeScanRepository scanRepo,
            FeltStocktakeStorageRepository stocktakeStorageRepo,
            StorageRepository storageRepo,
            BarcodeRepository barcodeRepo,
            FeltStocktakeItemEvaluator evaluator,
            FeltStocktakeScanMapper scanMapper) {
        this.stocktakeRepo = stocktakeRepo;
        this.itemRepo = itemRepo;
        this.rollOrScrapRepo = rollOrScrapRepo;
        this.scanRepo = scanRepo;
        this.stocktakeStorageRepo = stocktakeStorageRepo;
        this.storageRepo = storageRepo;
        this.barcodeRepo = barcodeRepo;
        this.evaluator = evaluator;
        this.scanMapper = scanMapper;
    }

    @Transactional
    public FeltStocktakeScanDto createScan(Long stocktakeId, CreateFeltStocktakeScanDto dto) {
        FeltStocktake stocktake = loadStocktake(stocktakeId);
        ensureNotCompleted(stocktake);

        Storage scannedStorage = storageRepo.findById(dto.scannedStorageId())
                                            .orElseThrow(
                                                    () -> new InvalidStorageReferenceException(dto.scannedStorageId()));

        ensureStorageInStocktake(stocktakeId, scannedStorage.getId());

        FeltStocktakeItem item = resolveItem(stocktake, dto.barcode());
        List<FeltStocktakeScan> scans = scanRepo.findByStocktakeIdAndStocktakeItemId(stocktakeId, item.getId());
        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, scans, false, false,
                stocktakeStorageIds(stocktakeId));

        FeltStocktakeScan scan = new FeltStocktakeScan(stocktake, item, dto.barcode(), scannedStorage);
        scan = scanRepo.save(scan);

        if (evaluation.status() == FeltStocktakeItemStatus.RESCAN_REQUIRED) {
            correctOriginalScan(scans);
        }

        return scanMapper.toDto(scan);
    }

    public FeltStocktakeScanDto getScan(Long stocktakeId, Long scanId) {
        loadStocktake(stocktakeId);
        FeltStocktakeScan scan = loadScanEntity(stocktakeId, scanId);
        return scanMapper.toDto(scan);
    }

    public List<FeltStocktakeScanDto> getScans(Long stocktakeId, @Nullable Long storageId) {
        loadStocktake(stocktakeId);
        List<FeltStocktakeScan> scans = storageId == null
                ? scanRepo.findByStocktakeId(stocktakeId)
                : scanRepo.findByStocktakeIdAndScannedStorageId(stocktakeId, storageId);
        return scans.stream()
                    .map(scanMapper::toDto)
                    .toList();
    }

    @Transactional
    public void voidScan(Long stocktakeId, Long scanId) {

        FeltStocktake stocktake = loadStocktake(stocktakeId);
        ensureNotCompleted(stocktake);

        FeltStocktakeScan scan = loadScanEntity(stocktakeId, scanId);

        if (scan.isVoided()) {
            return;
        }

        FeltStocktakeItem item = scan.getStocktakeItem();
        List<FeltStocktakeScan> scans = scanRepo.findByStocktakeIdAndStocktakeItemId(stocktakeId, item.getId());
        boolean expectedStorageClosed = isExpectedStorageClosed(item, stocktakeId);
        Set<Long> stocktakeStorageIds = stocktakeStorageIds(stocktakeId);
        FeltStocktakeItemEvaluation evaluation = evaluator.evaluate(item, scans, false, expectedStorageClosed,
                stocktakeStorageIds);

        if (evaluation.hasResolvedProblem()) {
            throw new FeltStocktakeScanLockedException(stocktakeId, scanId);
        }

        scan.setVoided(true);
    }

    private FeltStocktakeItem resolveItem(FeltStocktake stocktake, String barcodeValue) {
        Long barcodeId = tryParseBarcodeId(barcodeValue);
        if (barcodeId == null) {
            return resolveUnknownItem(stocktake, barcodeValue);
        }
        return barcodeRepo.findById(barcodeId)
                          .map(barcode -> resolveKnownItem(stocktake, barcode, barcodeValue))
                          .orElseGet(() -> resolveUnknownItem(stocktake, barcodeValue));
    }

    private FeltStocktakeItem resolveKnownItem(FeltStocktake stocktake, Barcode barcode, String barcodeValue) {
        if (barcode.getFeltRoll() != null) {
            return resolveRollItem(stocktake, barcode.getFeltRoll());
        }
        if (barcode.getScrapPiece() != null) {
            return resolveScrapItem(stocktake, barcode.getScrapPiece());
        }
        return resolveUnknownItem(stocktake, barcodeValue);
    }

    private FeltStocktakeItem resolveRollItem(FeltStocktake stocktake, FeltRoll roll) {
        return itemRepo.findByStocktakeIdAndRollId(stocktake.getId(), roll.getId())
                       .orElseGet(() -> createItemForRoll(stocktake, roll));
    }

    private FeltStocktakeItem resolveScrapItem(FeltStocktake stocktake, ScrapPiece scrap) {
        return itemRepo.findByStocktakeIdAndScrapId(stocktake.getId(), scrap.getId())
                       .orElseGet(() -> createItemForScrap(stocktake, scrap));
    }

    private FeltStocktakeItem resolveUnknownItem(FeltStocktake stocktake, String barcodeValue) {
        return itemRepo.findByStocktakeIdAndBarcode(stocktake.getId(), barcodeValue)
                       .orElseGet(() -> itemRepo.save(new FeltStocktakeItem(stocktake, barcodeValue)));
    }

    private FeltStocktakeItem createItemForRoll(FeltStocktake stocktake, FeltRoll roll) {
        FeltStocktakeItem item = itemRepo.save(new FeltStocktakeItem(stocktake));

        FeltStocktakeRollOrScrap rollOrScrap = new FeltStocktakeRollOrScrap(
                item,
                null,
                roll.getLength(),
                roll.getWidth(),
                roll.getFeltColorVariant()
                    .getColor(),
                roll.getFeltColorVariant()
                    .getFeltVariant()
                    .getThickness(),
                roll.getFeltColorVariant()
                    .getFeltVariant()
                    .getDensity(),
                roll.getFeltColorVariant()
                    .getFeltVariant()
                    .getPrice(),
                roll.getFeltColorVariant()
                    .getFeltVariant()
                    .getFelt()
                    .getArticleNumber(),
                roll.getFeltColorVariant()
                    .getFeltVariant()
                    .getFelt()
                    .getFeltType()
                    .getName(),
                roll.getFeltColorVariant()
                    .getFeltVariant()
                    .getFelt()
                    .getSupplier()
                    .getName(),
                roll
        );
        rollOrScrapRepo.save(rollOrScrap);
        item.setRollOrScrap(rollOrScrap);
        return item;
    }

    private FeltStocktakeItem createItemForScrap(FeltStocktake stocktake, ScrapPiece scrap) {
        FeltStocktakeItem item = itemRepo.save(new FeltStocktakeItem(stocktake));

        FeltStocktakeRollOrScrap rollOrScrap = new FeltStocktakeRollOrScrap(
                item,
                null,
                scrap.getLength(),
                scrap.getWidth(),
                scrap.getFeltColorVariant()
                     .getColor(),
                scrap.getFeltColorVariant()
                     .getFeltVariant()
                     .getThickness(),
                scrap.getFeltColorVariant()
                     .getFeltVariant()
                     .getDensity(),
                scrap.getFeltColorVariant()
                     .getFeltVariant()
                     .getPrice(),
                scrap.getFeltColorVariant()
                     .getFeltVariant()
                     .getFelt()
                     .getArticleNumber(),
                scrap.getFeltColorVariant()
                     .getFeltVariant()
                     .getFelt()
                     .getFeltType()
                     .getName(),
                scrap.getFeltColorVariant()
                     .getFeltVariant()
                     .getFelt()
                     .getSupplier()
                     .getName(),
                scrap
        );
        rollOrScrapRepo.save(rollOrScrap);
        item.setRollOrScrap(rollOrScrap);
        return item;
    }

    private void correctOriginalScan(List<FeltStocktakeScan> scans) {
        for (FeltStocktakeScan scan : scans) {
            if (!scan.isVoided()) {
                scan.setCorrected(true);
            }
        }
    }

    private boolean isExpectedStorageClosed(FeltStocktakeItem item, Long stocktakeId) {
        if (item.getRollOrScrap() == null || item.getRollOrScrap()
                                                 .getExpectedStorage() == null) {
            return false;
        }
        Long storageId = item.getRollOrScrap()
                             .getExpectedStorage()
                             .getId();
        return stocktakeStorageRepo.findByStocktakeIdAndStorageId(stocktakeId, storageId)
                                   .map(FeltStocktakeStorage::isClosed)
                                   .orElse(false);
    }

    private Set<Long> stocktakeStorageIds(Long stocktakeId) {
        return stocktakeStorageRepo.findByStocktakeId(stocktakeId)
                                   .stream()
                                   .map(link -> link.getStorage()
                                                    .getId())
                                   .collect(HashSet::new, Set::add, Set::addAll);
    }

    private void ensureStorageInStocktake(Long stocktakeId, Long storageId) {
        if (stocktakeStorageRepo.findByStocktakeIdAndStorageId(stocktakeId, storageId)
                                .isEmpty()) {
            throw new FeltStocktakeStorageNotFoundException(stocktakeId, storageId);
        }
    }

    private FeltStocktake loadStocktake(Long stocktakeId) {
        return stocktakeRepo.findById(stocktakeId)
                            .orElseThrow(() -> new FeltStocktakeNotFoundException(stocktakeId));
    }

    private FeltStocktakeScan loadScanEntity(Long stocktakeId, Long scanId) {
        return scanRepo.findByStocktakeIdAndId(stocktakeId, scanId)
                       .orElseThrow(
                               () -> new FeltStocktakeScanNotFoundException(stocktakeId, scanId));
    }

    private void ensureNotCompleted(FeltStocktake stocktake) {
        if (stocktake.getCompletedAt() != null) {
            throw new FeltStocktakeCompletedException(stocktake.getId());
        }
    }

    private Long tryParseBarcodeId(String barcodeValue) {
        try {
            return Long.parseLong(barcodeValue);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}