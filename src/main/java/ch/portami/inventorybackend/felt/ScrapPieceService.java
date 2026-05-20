package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.felt.dto.CreateScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.ScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.UpdateScrapPieceDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.event.ScrapPieceCreatedEvent;
import ch.portami.inventorybackend.felt.exception.FeltNotFoundException;
import ch.portami.inventorybackend.felt.exception.InvalidBatchReferenceException;
import ch.portami.inventorybackend.felt.exception.ScrapPieceNotFoundException;
import ch.portami.inventorybackend.felt.mapper.ScrapPieceMapper;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import ch.portami.inventorybackend.storage.StorageService;
import ch.portami.inventorybackend.storage.entity.Storage;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ch.portami.inventorybackend.core.util.NullSafeMapper.applyIfPresent;

@Service
@Transactional(readOnly = true)
public class ScrapPieceService {

    private final StorageService storageService;
    private final FeltRepository feltRepo;
    private final ScrapPieceRepository scrapPieceRepo;
    private final BatchRepository batchRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final ScrapPieceMapper scrapPieceMapper;

    public ScrapPieceService(StorageService storageService, FeltRepository feltRepo, ScrapPieceRepository scrapPieceRepo, BatchRepository batchRepo,
            ApplicationEventPublisher eventPublisher,
            ScrapPieceMapper scrapPieceMapper) {
        this.storageService = storageService;
        this.feltRepo = feltRepo;
        this.scrapPieceRepo = scrapPieceRepo;
        this.batchRepo = batchRepo;
        this.eventPublisher = eventPublisher;
        this.scrapPieceMapper = scrapPieceMapper;
    }

    public List<ScrapPieceDto> findAll() {
        return scrapPieceRepo.findAll()
                             .stream()
                             .map(scrapPieceMapper::toDto)
                             .toList();
    }

    public List<ScrapPieceDto> findAllByFelt(Long feltId) {
        if (!feltRepo.existsById(feltId)) {
            throw new FeltNotFoundException(feltId);
        }
        return scrapPieceRepo.findByFeltId(feltId)
                             .stream()
                             .map(scrapPieceMapper::toDto)
                             .toList();
    }

    public ScrapPieceDto findById(Long id) {
        return scrapPieceRepo.findById(id)
                             .map(scrapPieceMapper::toDto)
                             .orElseThrow(() -> new ScrapPieceNotFoundException(id));
    }

    @Transactional
    public ScrapPieceDto create(CreateScrapPieceDto dto) {
        Felt felt = feltRepo.findById(dto.feltId())
                            .orElseThrow(() -> new FeltNotFoundException(dto.feltId()));

        Batch batch = resolveOptionalBatch(dto.batchId());
        Storage storage = resolveOptionalStorage(dto.storageId());

        ScrapPiece scrapPiece = new ScrapPiece(felt, batch, storage, dto.length(), dto.width());
        scrapPiece = scrapPieceRepo.save(scrapPiece);

        eventPublisher.publishEvent(new ScrapPieceCreatedEvent(scrapPiece));

        return scrapPieceMapper.toDto(scrapPiece);
    }

    @Transactional
    public ScrapPieceDto update(Long id, UpdateScrapPieceDto dto) {
        ScrapPiece scrapPiece = scrapPieceRepo.findById(id)
                                              .orElseThrow(() -> new ScrapPieceNotFoundException(id));

        applyIfPresent(dto::length, scrapPiece::setLength);
        applyIfPresent(dto::width, scrapPiece::setWidth);
        applyIfPresent(dto::batchId, batchId -> batchRepo.findById(batchId)
                                                         .orElseThrow(
                                                                 () -> new InvalidBatchReferenceException(batchId)),
                scrapPiece::setBatch);
        applyIfPresent(dto::storageId, storageService::getExistingById, scrapPiece::setStorage);

        return scrapPieceMapper.toDto(scrapPiece);
    }

    @Transactional
    public void delete(Long id) {
        if (!scrapPieceRepo.existsById(id)) {
            throw new ScrapPieceNotFoundException(id);
        }
        scrapPieceRepo.deleteById(id);
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

        return storageService.getExistingById(storageId);
    }
}
