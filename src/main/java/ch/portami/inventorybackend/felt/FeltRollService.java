package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.core.exceptions.BusinessRuleViolationException;
import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.felt.dto.BatchDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.SplitFeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.event.FeltRollCreatedEvent;
import ch.portami.inventorybackend.felt.exception.FeltNotFoundException;
import ch.portami.inventorybackend.felt.exception.FeltRollNotFoundException;
import ch.portami.inventorybackend.felt.exception.InvalidBatchReferenceException;
import ch.portami.inventorybackend.felt.mapper.BatchMapper;
import ch.portami.inventorybackend.felt.mapper.FeltRollMapper;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.util.BatchIdentifierGenerator;
import ch.portami.inventorybackend.storage.StorageService;
import ch.portami.inventorybackend.storage.entity.Storage;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ch.portami.inventorybackend.core.util.NullSafeMapper.applyIfPresent;

@Service
@Transactional(readOnly = true)
public class FeltRollService {

    private final StorageService storageService;
    private final FeltRepository feltRepo;
    private final FeltRollRepository feltRollRepo;
    private final BatchRepository batchRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final FeltRollMapper feltRollMapper;
    private final BatchMapper batchMapper;

    public FeltRollService(StorageService storageService, FeltRepository feltRepo, FeltRollRepository feltRollRepo, BatchRepository batchRepo,
            ApplicationEventPublisher eventPublisher, FeltRollMapper feltRollMapper,
            BatchMapper batchMapper) {
        this.storageService = storageService;
        this.feltRepo = feltRepo;
        this.feltRollRepo = feltRollRepo;
        this.batchRepo = batchRepo;
        this.eventPublisher = eventPublisher;
        this.feltRollMapper = feltRollMapper;
        this.batchMapper = batchMapper;
    }

    public List<FeltRollDto> findAll() {
        return feltRollRepo.findAll()
                           .stream()
                           .map(feltRollMapper::toDto)
                           .toList();
    }

    public List<FeltRollDto> findAllByFelt(Long feltId) {
        if (!feltRepo.existsById(feltId)) {
            throw new FeltNotFoundException(feltId);
        }
        return feltRollRepo.findByFeltId(feltId)
                           .stream()
                           .map(feltRollMapper::toDto)
                           .toList();
    }

    public List<BatchDto> findAllBatchesByFelt(Long feltId) {
        return feltRollRepo.findByFeltId(feltId)
                           .stream()
                           .map(FeltRoll::getBatch)
                           .distinct()
                           .map(batchMapper::toDto)
                           .toList();
    }

    public FeltRollDto findById(Long id) {
        return feltRollRepo.findById(id)
                           .map(feltRollMapper::toDto)
                           .orElseThrow(() -> new FeltRollNotFoundException(id));
    }

    @Transactional
    public FeltRollDto create(CreateFeltRollDto dto) {
        Felt felt = feltRepo.findById(dto.feltId())
                            .orElseThrow(() -> new FeltNotFoundException(dto.feltId()));

        Batch batch = resolveOptionalBatch(dto.batchId());
        Storage storage = storageService.getExistingById(dto.storageId());

        FeltRoll roll = new FeltRoll(felt, batch, storage, dto.length(), dto.width());
        roll = feltRollRepo.save(roll);

        if (batch == null) {
            String batchName = BatchIdentifierGenerator.createIdentifier(roll.getId());
            Batch newBatch = new Batch(batchName);
            newBatch = batchRepo.save(newBatch);
            roll.setBatch(newBatch);
        }

        eventPublisher.publishEvent(new FeltRollCreatedEvent(roll));

        return feltRollMapper.toDto(roll);
    }

    @Transactional
    public FeltRollDto update(Long id, UpdateFeltRollDto dto) {
        FeltRoll roll = feltRollRepo.findById(id)
                                    .orElseThrow(() -> new FeltRollNotFoundException(id));

        applyIfPresent(dto::length, roll::setLength);
        applyIfPresent(dto::width, roll::setWidth);

        applyIfPresent(dto::storageId, storageService::getExistingById, roll::setStorage);

        applyIfPresent(dto::batchId, batchId -> batchRepo.findById(batchId)
                                                         .orElseThrow(
                                                                 () -> new InvalidBatchReferenceException(batchId)),
                roll::setBatch);

        if (dto.batchId() != null) {
            roll.setBatch(resolveOptionalBatch(dto.batchId()));
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

        FeltRoll newRoll = new FeltRoll(source.getFelt(), source.getBatch(), source.getStorage(), source.getWidth(),
                cutWidth);
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
}
