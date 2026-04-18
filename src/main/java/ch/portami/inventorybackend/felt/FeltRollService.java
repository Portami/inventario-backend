package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.core.entity.Storage;
import ch.portami.inventorybackend.core.repository.StorageRepository;
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
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class FeltRollService {

    private final FeltColorVariantRepository feltColorVariantRepo;
    private final FeltRollRepository feltRollRepo;
    private final BatchRepository batchRepo;
    private final StorageRepository storageRepo;

    public FeltRollService(
        FeltColorVariantRepository feltColorVariantRepo,
        FeltRollRepository feltRollRepo,
        BatchRepository batchRepo,
        StorageRepository storageRepo
    ) {
        this.feltColorVariantRepo = feltColorVariantRepo;
        this.feltRollRepo = feltRollRepo;
        this.batchRepo = batchRepo;
        this.storageRepo = storageRepo;
    }

    public List<FeltRollDto> findAllByFelt(Long feltId) {
        if (!feltColorVariantRepo.existsById(feltId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found");
        }
        return feltRollRepo.findByFeltColorVariantId(feltId)
            .stream()
            .map(this::toDto)
            .toList();
    }

    public FeltRollDto findById(Long id) {
        return feltRollRepo.findById(id)
            .map(this::toDto)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roll not found"));
    }

    @Transactional
    public FeltRollDto create(CreateFeltRollDto dto) {
        FeltColorVariant colorVariant = feltColorVariantRepo.findById(dto.feltId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found"));

        Batch batch = resolveOptionalBatch(dto.batchId());
        Storage storage = resolveOptionalStorage(dto.storageId());

        FeltRoll roll = new FeltRoll(colorVariant, batch, storage, dto.length(), dto.width());
        roll = feltRollRepo.save(roll);

        return toDto(roll);
    }

    @Transactional
    public FeltRollDto update(Long id, UpdateFeltRollDto dto) {
        FeltRoll roll = feltRollRepo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Roll not found"));

        if (dto.length()    != null) roll.setLength(dto.length());
        if (dto.width()     != null) roll.setWidth(dto.width());
        if (dto.batchId()   != null) roll.setBatch(resolveOptionalBatch(dto.batchId()));
        if (dto.storageId() != null) roll.setStorage(resolveOptionalStorage(dto.storageId()));

        return toDto(roll);
    }

    @Transactional
    public void delete(Long id) {
        if (!feltRollRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Roll not found");
        }
        feltRollRepo.deleteById(id);
    }

    private Batch resolveOptionalBatch(Long batchId) {
        if (batchId == null) return null;
        return batchRepo.findById(batchId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found"));
    }

    private Storage resolveOptionalStorage(Long storageId) {
        if (storageId == null) return null;
        return storageRepo.findById(storageId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Storage not found"));
    }

    private FeltRollDto toDto(FeltRoll roll) {
        FeltColorVariant cv = roll.getFeltColorVariant();
        FeltVariant fv = cv.getFeltVariant();
        Felt felt = fv.getFelt();
        FeltType feltType = felt.getFeltType();
        Supplier supplier = felt.getSupplier();
        Batch batch = roll.getBatch();
        Storage storage = roll.getStorage();

        return new FeltRollDto(
            roll.getId(),
            roll.getLength(),
            roll.getWidth(),
            cv.getId(),
            cv.getColor(),
            cv.getSupplierColor(),
            fv.getId(),
            fv.getThickness(),
            fv.getDensity(),
            fv.getPrice(),
            felt.getId(),
            felt.getArticleNumber(),
            feltType.getName(),
            supplier.getName(),
            batch != null ? batch.getId() : null,
            batch != null ? batch.getName() : null,
            storage != null ? storage.getId() : null,
            storage != null ? storage.getName() : null
        );
    }
}
