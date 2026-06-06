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

/**
 * Service for managing scrap pieces (offcuts), including assigning them to batches and storage
 * locations. Publishes a {@link ScrapPieceCreatedEvent} whenever a scrap piece is created (so a
 * barcode is generated for it).
 */
@Service
@Transactional(readOnly = true)
public class ScrapPieceService {

    private final StorageService storageService;
    private final FeltRepository feltRepo;
    private final ScrapPieceRepository scrapPieceRepo;
    private final BatchRepository batchRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final ScrapPieceMapper scrapPieceMapper;

    public ScrapPieceService(StorageService storageService, FeltRepository feltRepo,
            ScrapPieceRepository scrapPieceRepo, BatchRepository batchRepo,
            ApplicationEventPublisher eventPublisher,
            ScrapPieceMapper scrapPieceMapper) {
        this.storageService = storageService;
        this.feltRepo = feltRepo;
        this.scrapPieceRepo = scrapPieceRepo;
        this.batchRepo = batchRepo;
        this.eventPublisher = eventPublisher;
        this.scrapPieceMapper = scrapPieceMapper;
    }

    /**
     * Retrieves all scrap pieces.
     *
     * @return a list of DTOs for all scrap pieces
     */
    public List<ScrapPieceDto> findAll() {
        return scrapPieceRepo.findAll()
                             .stream()
                             .map(scrapPieceMapper::toDto)
                             .toList();
    }

    /**
     * Retrieves all scrap pieces of a given felt.
     *
     * @param feltId the ID of the felt whose scrap pieces to retrieve
     * @return a list of DTOs for the felt's scrap pieces
     * @throws FeltNotFoundException if no felt with the given ID exists
     */
    public List<ScrapPieceDto> findAllByFelt(Long feltId) {
        if (!feltRepo.existsById(feltId)) {
            throw new FeltNotFoundException(feltId);
        }
        return scrapPieceRepo.findByFeltId(feltId)
                             .stream()
                             .map(scrapPieceMapper::toDto)
                             .toList();
    }

    /**
     * Retrieves a scrap piece by its ID.
     *
     * @param id the ID of the scrap piece to retrieve
     * @return the DTO of the retrieved scrap piece
     * @throws ScrapPieceNotFoundException if no scrap piece with the given ID exists
     */
    public ScrapPieceDto findById(Long id) {
        return scrapPieceRepo.findById(id)
                             .map(scrapPieceMapper::toDto)
                             .orElseThrow(() -> new ScrapPieceNotFoundException(id));
    }

    /**
     * Creates a new scrap piece, optionally assigned to a batch and storage location. A
     * {@link ScrapPieceCreatedEvent} is published once the scrap piece is persisted.
     *
     * @param dto the data for the new scrap piece
     * @return the DTO of the created scrap piece
     * @throws FeltNotFoundException                                                  if the referenced felt does not exist
     * @throws InvalidBatchReferenceException                                         if a batch is referenced but does not exist
     * @throws ch.portami.inventorybackend.storage.exception.InvalidStorageReferenceException if a storage is referenced but does not exist
     */
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

    /**
     * Applies a partial update to a scrap piece. Only non-null fields of the DTO are applied;
     * referenced batch and storage are re-resolved when supplied.
     *
     * @param id  the ID of the scrap piece to update
     * @param dto the requested updates; null fields are left unchanged
     * @return the DTO of the updated scrap piece
     * @throws ScrapPieceNotFoundException                                            if no scrap piece with the given ID exists
     * @throws InvalidBatchReferenceException                                         if a referenced batch does not exist
     * @throws ch.portami.inventorybackend.storage.exception.InvalidStorageReferenceException if a referenced storage does not exist
     */
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

    /**
     * Deletes a scrap piece by its ID. Deleting a non-existent scrap piece is a no-op.
     *
     * @param id the ID of the scrap piece to delete
     */
    @Transactional
    public void delete(Long id) {
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
