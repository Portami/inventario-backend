package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.core.entity.Storage;
import ch.portami.inventorybackend.core.repository.StorageRepository;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollRequest;
import ch.portami.inventorybackend.felt.dto.FeltRollResponse;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollRequest;
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
public class FeltService {

    private final FeltRollRepository feltRollRepository;
    private final FeltColorVariantRepository feltColorVariantRepository;
    private final BatchRepository batchRepository;
    private final StorageRepository storageRepository;

    public FeltService(
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

    public List<FeltRollResponse> getAllFeltRolls() {
        return feltRollRepository
            .findAll()
            .stream()
            .map(this::toResponse)
            .toList();
    }

    public FeltRollResponse getFeltRollById(Long id) {
        return toResponse(findRollOrThrow(id));
    }

    @Transactional
    public FeltRollResponse createFeltRoll(CreateFeltRollRequest request) {
        FeltColorVariant colorVariant = feltColorVariantRepository
            .findById(request.feltColorVariantId())
            .orElseThrow(() -> 
                new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltColorVariant not found: " + request.feltColorVariantId())
            );

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
    public FeltRollResponse updateFeltRoll(Long id, UpdateFeltRollRequest request) {
        FeltRoll roll = findRollOrThrow(id);

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
    public void deleteFeltRoll(Long id) {
        if (!feltRollRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltRoll not found: " + id);
        }
        feltRollRepository.deleteById(id);
    }

    private FeltRoll findRollOrThrow(Long id) {
        return feltRollRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltRoll not found: " + id)
            );
    }

    private FeltRollResponse toResponse(FeltRoll roll) {
        FeltColorVariant colorVariant = roll.getFeltColorVariant();
        FeltVariant feltVariant = colorVariant.getFeltVariant();
        var felt = feltVariant.getFelt();
        Batch batch = roll.getBatch();
        Storage storage = roll.getStorage();

        return new FeltRollResponse(
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
