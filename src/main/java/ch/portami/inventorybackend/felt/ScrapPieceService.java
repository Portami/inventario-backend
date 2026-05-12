package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.core.storage.entity.Storage;
import ch.portami.inventorybackend.core.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.core.storage.repository.StorageRepository;
import ch.portami.inventorybackend.felt.dto.CreateScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.ScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.UpdateScrapPieceDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.event.ScrapPieceCreatedEvent;
import ch.portami.inventorybackend.felt.exception.InvalidBatchReferenceException;
import ch.portami.inventorybackend.felt.exception.ScrapPieceNotFoundException;
import ch.portami.inventorybackend.felt.mapper.ScrapPieceMapper;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ScrapPieceService {

    private final FeltColorVariantRepository feltColorVariantRepo;
    private final ScrapPieceRepository scrapPieceRepo;
    private final BatchRepository batchRepo;
    private final StorageRepository storageRepo;
    private final ApplicationEventPublisher eventPublisher;
    private final ScrapPieceMapper scrapPieceMapper;

    public ScrapPieceService(
            FeltColorVariantRepository feltColorVariantRepo,
            ScrapPieceRepository scrapPieceRepo,
            BatchRepository batchRepo,
            StorageRepository storageRepo,
            ApplicationEventPublisher eventPublisher,
            ScrapPieceMapper scrapPieceMapper
    ) {
        this.feltColorVariantRepo = feltColorVariantRepo;
        this.scrapPieceRepo = scrapPieceRepo;
        this.batchRepo = batchRepo;
        this.storageRepo = storageRepo;
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
        if (!feltColorVariantRepo.existsById(feltId)) {
            throw new ScrapPieceNotFoundException(feltId);
        }
        return scrapPieceRepo.findByFeltColorVariantId(feltId)
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
        FeltColorVariant colorVariant = feltColorVariantRepo.findById(dto.feltId())
                                                            .orElseThrow(
                                                                    () -> new ScrapPieceNotFoundException(dto.feltId()));

        Batch batch = resolveOptionalBatch(dto.batchId());
        Storage storage = resolveOptionalStorage(dto.storageId());

        ScrapPiece scrapPiece = new ScrapPiece(colorVariant, batch, storage, dto.length(), dto.width());
        scrapPiece = scrapPieceRepo.save(scrapPiece);

        eventPublisher.publishEvent(new ScrapPieceCreatedEvent(scrapPiece));

        return scrapPieceMapper.toDto(scrapPiece);
    }

    @Transactional
    public ScrapPieceDto update(Long id, UpdateScrapPieceDto dto) {
        ScrapPiece scrapPiece = scrapPieceRepo.findById(id)
                                              .orElseThrow(() -> new ScrapPieceNotFoundException(id));

        if (dto.length() != null) {
            scrapPiece.setLength(dto.length());
        }
        if (dto.width() != null) {
            scrapPiece.setWidth(dto.width());
        }
        if (dto.batchId() != null) {
            scrapPiece.setBatch(resolveOptionalBatch(dto.batchId()));
        }
        if (dto.storageId() != null) {
            scrapPiece.setStorage(resolveOptionalStorage(dto.storageId()));
        }

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
        return storageRepo.findById(storageId)
                          .orElseThrow(() -> new InvalidStorageReferenceException(storageId));
    }
}
