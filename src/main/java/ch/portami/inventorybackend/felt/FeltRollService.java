package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.core.entity.Storage;
import ch.portami.inventorybackend.core.repository.StorageRepository;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.FeltVariant;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FeltRollService {

    private final FeltRollRepository feltRollRepository;
    private final FeltColorVariantRepository feltColorVariantRepository;
    private final BatchRepository batchRepository;
    private final StorageRepository storageRepository;

    public FeltRollService(
        FeltRollRepository feltRollRepository,
        FeltColorVariantRepository feltColorVariantRepository,
        BatchRepository batchRepository,
        StorageRepository storageRepository
    ) {
        this.feltRollRepository = feltRollRepository;
        this.feltColorVariantRepository = feltColorVariantRepository;
        this.batchRepository = batchRepository;
        this.storageRepository = storageRepository;
    }

    @Transactional(readOnly = true)
    public List<FeltRollDto> getFeltRollsByColorVariant(Long feltId, Long variantId, Long colorVariantId) {
        FeltColorVariant colorVariant = findColorVariantOrThrow(colorVariantId);
        validateColorVariantChain(colorVariant, feltId, variantId);
        return feltRollRepository
            .findByFeltColorVariantId(colorVariantId)
            .stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public FeltRollDto getFeltRollById(Long id, Long feltId, Long variantId, Long colorVariantId) {
        FeltRoll roll = findRollOrThrow(id);
        validateRollBelongsToColorVariant(roll, feltId, variantId, colorVariantId);
        return toResponse(roll);
    }

    @Transactional
    public FeltRollDto createFeltRoll(Long feltId, Long variantId, Long colorVariantId, CreateFeltRollDto request) {
        FeltColorVariant colorVariant = findColorVariantOrThrow(colorVariantId);
        validateColorVariantChain(colorVariant, feltId, variantId);

        Batch batch = null;
        if (request.batchId() != null) {
            batch = batchRepository
                .findById(request.batchId())
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found: " + request.batchId())
                );
        }

        Storage storage = null;
        if (request.storageId() != null) {
            storage = storageRepository
                .findById(request.storageId())
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Storage not found: " + request.storageId())
                );
        }

        FeltRoll roll = new FeltRoll(colorVariant, batch, storage, request.length(), request.width());
        return toResponse(feltRollRepository.save(roll));
    }

    @Transactional
    public FeltRollDto updateFeltRoll(Long id, Long feltId, Long variantId, Long colorVariantId, UpdateFeltRollDto request) {
        FeltRoll roll = findRollOrThrow(id);
        validateRollBelongsToColorVariant(roll, feltId, variantId, colorVariantId);

        if (request.length() != null) {
            roll.setLength(request.length());
        }
        if (request.width() != null) {
            roll.setWidth(request.width());
        }

        if (request.batchId() != null) {
            Batch batch = batchRepository
                .findById(request.batchId())
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Batch not found: " + request.batchId())
                );
            roll.setBatch(batch);
        } else {
            roll.setBatch(null);
        }

        if (request.storageId() != null) {
            Storage storage = storageRepository
                .findById(request.storageId())
                .orElseThrow(() ->
                    new ResponseStatusException(HttpStatus.NOT_FOUND, "Storage not found: " + request.storageId())
                );
            roll.setStorage(storage);
        } else {
            roll.setStorage(null);
        }

        return toResponse(feltRollRepository.save(roll));
    }

    @Transactional
    public void deleteFeltRoll(Long id, Long feltId, Long variantId, Long colorVariantId) {
        FeltRoll roll = findRollOrThrow(id);
        validateRollBelongsToColorVariant(roll, feltId, variantId, colorVariantId);
        feltRollRepository.deleteById(id);
    }

    private void validateColorVariantChain(FeltColorVariant colorVariant, Long feltId, Long variantId) {
        FeltVariant variant = colorVariant.getFeltVariant();
        if (!variant.getId().equals(variantId) || !variant.getFelt().getId().equals(feltId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                "FeltColorVariant not found: " + colorVariant.getId());
        }
    }

    private void validateRollBelongsToColorVariant(FeltRoll roll, Long feltId, Long variantId, Long colorVariantId) {
        FeltColorVariant colorVariant = roll.getFeltColorVariant();
        if (!colorVariant.getId().equals(colorVariantId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltRoll not found: " + roll.getId());
        }
        validateColorVariantChain(colorVariant, feltId, variantId);
    }

    private FeltColorVariant findColorVariantOrThrow(Long id) {
        return feltColorVariantRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltColorVariant not found: " + id)
            );
    }

    private FeltRoll findRollOrThrow(Long id) {
        return feltRollRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltRoll not found: " + id)
            );
    }

    private FeltRollDto toResponse(FeltRoll roll) {
        FeltColorVariant colorVariant = roll.getFeltColorVariant();
        FeltVariant feltVariant = colorVariant.getFeltVariant();
        var felt = feltVariant.getFelt();
        Batch batch = roll.getBatch();
        Storage storage = roll.getStorage();

        return new FeltRollDto(
            roll.getId(),
            roll.getLength(),
            roll.getWidth(),
            colorVariant.getId(),
            colorVariant.getColor(),
            colorVariant.getSupplierColor(),
            feltVariant.getId(),
            feltVariant.getThickness(),
            feltVariant.getDensity(),
            feltVariant.getPrice(),
            felt.getId(),
            felt.getArticleNumber(),
            felt.getFeltType().getName(),
            felt.getSupplier().getName(),
            batch != null ? batch.getId() : null,
            batch != null ? batch.getName() : null,
            storage != null ? storage.getId() : null,
            storage != null ? storage.getName() : null
        );
    }
}
