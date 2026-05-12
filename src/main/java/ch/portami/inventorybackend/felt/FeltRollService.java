package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.core.storage.entity.Storage;
import ch.portami.inventorybackend.core.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.core.storage.repository.StorageRepository;
import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.SplitFeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.event.FeltRollCreatedEvent;
import ch.portami.inventorybackend.felt.exception.FeltRollNotFoundException;
import ch.portami.inventorybackend.felt.exception.InvalidBatchReferenceException;
import ch.portami.inventorybackend.felt.mapper.FeltRollMapper;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeltRollService {

    private final FeltColorVariantRepository feltColorVariantRepo;
    private final FeltRollRepository feltRollRepo;
    private final BatchRepository batchRepo;
    private final StorageRepository storageRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final FeltRollMapper feltRollMapper;

    public FeltRollService(
            FeltColorVariantRepository feltColorVariantRepo,
            FeltRollRepository feltRollRepo,
            BatchRepository batchRepo,
            StorageRepository storageRepo,
            ApplicationEventPublisher eventPublisher,
            FeltRollMapper feltRollMapper
    ) {
        this.feltColorVariantRepo = feltColorVariantRepo;
        this.feltRollRepo = feltRollRepo;
        this.batchRepo = batchRepo;
        this.storageRepo = storageRepo;
        this.eventPublisher = eventPublisher;
        this.feltRollMapper = feltRollMapper;
    }

    public List<FeltRollDto> findAll() {
        return feltRollRepo.findAll()
                           .stream()
                           .map(feltRollMapper::toDto)
                           .toList();
    }

    public List<FeltRollDto> findAllByFelt(Long feltId) {
        if (!feltColorVariantRepo.existsById(feltId)) {
            throw new FeltRollNotFoundException(feltId);
        }
        return feltRollRepo.findByFeltColorVariantId(feltId)
                           .stream()
                           .map(feltRollMapper::toDto)
                           .toList();
    }

    public FeltRollDto findById(Long id) {
        return feltRollRepo.findById(id)
                           .map(feltRollMapper::toDto)
                           .orElseThrow(() -> new FeltRollNotFoundException(id));
    }

    @Transactional
    public FeltRollDto create(CreateFeltRollDto dto) {
        FeltColorVariant colorVariant = feltColorVariantRepo.findById(dto.feltId())
                                                            .orElseThrow(
                                                                    () -> new FeltRollNotFoundException(dto.feltId()));

        Batch batch = resolveOptionalBatch(dto.batchId());
        Storage storage = resolveOptionalStorage(dto.storageId());

        FeltRoll roll = new FeltRoll(colorVariant, batch, storage, dto.length(), dto.width());
        roll = feltRollRepo.save(roll);

        eventPublisher.publishEvent(new FeltRollCreatedEvent(roll));

        return feltRollMapper.toDto(roll);
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

        return feltRollMapper.toDto(roll);
    }

    @Transactional
    public FeltRollDto split(Long sourceRollId, SplitFeltRollDto dto) {
        FeltRoll source = feltRollRepo.findById(sourceRollId)
                                      .orElseThrow(() -> new FeltRollNotFoundException(sourceRollId));

        double cutWidth = dto.width();

        if (cutWidth >= source.getLength()) {
            throw new BusinessRuleViolationException(
                    "Split width (" + cutWidth + ") must be less than source roll length (" + source.getLength() + ")",
                    new ResourceIdentifier("rollId", sourceRollId));
        }

        FeltRoll newRoll = new FeltRoll(
                source.getFeltColorVariant(),
                source.getBatch(),
                source.getStorage(),
                source.getWidth(),
                cutWidth
        );
        newRoll = feltRollRepo.save(newRoll);

        source.setLength(source.getLength() - cutWidth);

        eventPublisher.publishEvent(new FeltRollCreatedEvent(newRoll));

        return feltRollMapper.toDto(newRoll);
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
}
