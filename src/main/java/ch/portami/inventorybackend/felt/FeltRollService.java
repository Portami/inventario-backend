package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.barcode.BarcodeService;
import ch.portami.inventorybackend.core.storage.entity.Storage;
import ch.portami.inventorybackend.core.storage.repository.StorageRepository;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.FeltVariant;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.exception.FeltRollNotFoundException;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.core.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.felt.exception.InvalidBatchReferenceException;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.restocking.RestockingService;
import ch.portami.inventorybackend.restocking.entity.Supply;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeltRollService {

    private final FeltColorVariantRepository feltColorVariantRepo;
    private final FeltRollRepository feltRollRepo;
    private final BatchRepository batchRepo;
    private final StorageRepository storageRepo;
    private final BarcodeService barcodeService;

    public FeltRollService(FeltColorVariantRepository feltColorVariantRepo, FeltRollRepository feltRollRepo,
            BatchRepository batchRepo, StorageRepository storageRepo, BarcodeService barcodeService) {
        this.feltColorVariantRepo = feltColorVariantRepo;
        this.feltRollRepo = feltRollRepo;
        this.batchRepo = batchRepo;
        this.storageRepo = storageRepo;
        this.barcodeService = barcodeService;
    }

    public List<FeltRollDto> findAllByFelt(Long feltId) {
        if (!feltColorVariantRepo.existsById(feltId)) {
            throw new FeltRollNotFoundException(feltId);
        }
        return feltRollRepo.findByFeltColorVariantId(feltId)
                           .stream()
                           .map(this::toDto)
                           .toList();
    }

    public FeltRollDto findById(Long id) {
        return feltRollRepo.findById(id)
                           .map(this::toDto)
                           .orElseThrow(() -> new FeltRollNotFoundException(id));
    }

    @Transactional
    public FeltRollDto create(CreateFeltRollDto dto) {
        FeltColorVariant colorVariant = feltColorVariantRepo.findById(dto.feltId())
                                                            .orElseThrow(() -> new FeltRollNotFoundException(dto.feltId()));

        Batch batch = resolveOptionalBatch(dto.batchId());
        Storage storage = resolveOptionalStorage(dto.storageId());

        FeltRoll roll = new FeltRoll(colorVariant, batch, storage, dto.length(), dto.width());
        roll = feltRollRepo.save(roll);

        barcodeService.createForRoll(roll);

        return toDto(roll);
    }

    @Transactional
    public FeltRollDto update(Long id, UpdateFeltRollDto dto) {
        FeltRoll roll = feltRollRepo.findById(id)
                                    .orElseThrow(() -> new FeltRollNotFoundException(id));

        if (dto.length() != null) {
            roll.setLength(dto.length());
        }
        if (dto.width() != null) {
            roll.setWidth(dto.width());
        }
        if (dto.batchId() != null) {
            roll.setBatch(resolveOptionalBatch(dto.batchId()));
        }
        if (dto.storageId() != null) {
            roll.setStorage(resolveOptionalStorage(dto.storageId()));
        }

        return toDto(roll);
    }

    @Transactional
    public void delete(Long id) {
        if (!feltRollRepo.existsById(id)) {
            throw new FeltRollNotFoundException(id);
        }
        feltRollRepo.deleteById(id);
    }

    private Batch resolveOptionalBatch(Long batchId) {
        if (batchId == null) {
            return null;
        }
        return batchRepo.findById(batchId)
                        .orElseThrow(() -> new InvalidBatchReferenceException(batchId));
    }

    private Storage resolveOptionalStorage(Long storageId) {
        if (storageId == null) {
            return null;
        }
        return storageRepo.findById(storageId)
                          .orElseThrow(() -> new InvalidStorageReferenceException(storageId));
    }

    private FeltRollDto toDto(FeltRoll roll) {
        FeltColorVariant feltColorVariant = roll.getFeltColorVariant();
        FeltVariant feltVariant = feltColorVariant.getFeltVariant();
        Felt felt = feltVariant.getFelt();
        FeltType feltType = felt.getFeltType();
        Supplier supplier = felt.getSupplier();
        Batch batch = roll.getBatch();
        Storage storage = roll.getStorage();

        return new FeltRollDto(roll.getId(), roll.getLength(), roll.getWidth(), feltColorVariant.getId(),
                feltColorVariant.getColor(), feltColorVariant.getSupplierColor(), feltVariant.getId(),
                feltVariant.getThickness(), feltVariant.getDensity(), feltVariant.getPrice(), felt.getId(),
                felt.getArticleNumber(), feltType.getName(), supplier.getName(), batch != null ? batch.getId() : null,
                batch != null ? batch.getName() : null, storage != null ? storage.getId() : null,
                storage != null ? storage.getName() : null);
    }
}
