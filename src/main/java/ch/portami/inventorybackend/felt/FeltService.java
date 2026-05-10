package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.core.storage.entity.Storage;
import ch.portami.inventorybackend.core.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.core.storage.repository.StorageRepository;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltRollDto;
import ch.portami.inventorybackend.felt.entity.Batch;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.FeltVariant;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.event.FeltColorVariantCreatedEvent;
import ch.portami.inventorybackend.felt.event.FeltRollCreatedEvent;
import ch.portami.inventorybackend.felt.exception.FeltNotFoundException;
import ch.portami.inventorybackend.felt.exception.FeltRollNotFoundException;
import ch.portami.inventorybackend.felt.exception.InvalidBatchReferenceException;
import ch.portami.inventorybackend.felt.exception.InvalidFeltTypeReferenceException;
import ch.portami.inventorybackend.felt.exception.InvalidSupplierReferenceException;
import ch.portami.inventorybackend.felt.mapper.FeltMapper;
import ch.portami.inventorybackend.felt.mapper.FeltRollMapper;
import ch.portami.inventorybackend.felt.repository.BatchRepository;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.FeltVariantRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import ch.portami.inventorybackend.restocking.repository.SupplyRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeltService {

    private final FeltTypeRepository feltTypeRepo;
    private final FeltRepository feltRepo;
    private final FeltVariantRepository feltVariantRepo;
    private final FeltColorVariantRepository feltColorVariantRepo;
    private final SupplierRepository supplierRepo;
    private final FeltRollRepository feltRollRepo;
    private final BatchRepository batchRepo;
    private final StorageRepository storageRepo;
    private final SupplyRepository supplyRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final FeltMapper feltMapper;
    private final FeltRollMapper feltRollMapper;

    public FeltService(
            FeltTypeRepository feltTypeRepo,
            FeltRepository feltRepo,
            FeltVariantRepository feltVariantRepo,
            FeltColorVariantRepository feltColorVariantRepo,
            SupplierRepository supplierRepo,
            FeltRollRepository feltRollRepo,
            BatchRepository batchRepo,
            StorageRepository storageRepo,
            SupplyRepository supplyRepository,
            ApplicationEventPublisher eventPublisher,
            FeltMapper feltMapper,
            FeltRollMapper feltRollMapper
    ) {
        this.feltTypeRepo = feltTypeRepo;
        this.feltRepo = feltRepo;
        this.feltVariantRepo = feltVariantRepo;
        this.feltColorVariantRepo = feltColorVariantRepo;
        this.supplierRepo = supplierRepo;
        this.feltRollRepo = feltRollRepo;
        this.batchRepo = batchRepo;
        this.storageRepo = storageRepo;
        this.supplyRepository = supplyRepository;
        this.eventPublisher = eventPublisher;
        this.feltMapper = feltMapper;
        this.feltRollMapper = feltRollMapper;
    }

    // ─── Felt Color Variant CRUD ─────────────────────────────────────────────────

    public List<FeltDto> findAll() {
        return feltColorVariantRepo
                .findAll()
                .stream()
                .map(this::toFeltDto)
                .toList();
    }

    public FeltDto findById(Long id) {
        return feltColorVariantRepo
                .findById(id)
                .map(this::toFeltDto)
                .orElseThrow(() -> new FeltNotFoundException(id));
    }

    @Transactional
    public FeltDto create(CreateFeltDto dto) {
        FeltType feltType = feltTypeRepo
                .findById(dto.feltTypeId())
                .orElseThrow(() -> new InvalidFeltTypeReferenceException(dto.feltTypeId()));

        Supplier supplier = supplierRepo
                .findById(dto.supplierId())
                .orElseThrow(() -> new InvalidSupplierReferenceException(dto.supplierId()));

        Felt felt = feltRepo
                .findByFeltTypeAndSupplierAndArticleNumber(feltType, supplier, dto.articleNumber())
                .orElseGet(
                        () -> feltRepo.save(new Felt(feltType, supplier, dto.articleNumber()))
                );

        FeltVariant feltVariant = feltVariantRepo
                .findByFeltAndThicknessAndDensityAndPrice(felt, dto.thickness(), dto.density(), dto.price())
                .orElseGet(
                        () -> feltVariantRepo.save(new FeltVariant(felt, dto.thickness(), dto.density(), dto.price()))
                );

        FeltColorVariant feltColorVariant = new FeltColorVariant(feltVariant, dto.color());
        feltColorVariant.setSupplierColor(dto.supplierColor());
        feltColorVariant = feltColorVariantRepo.save(feltColorVariant);

        eventPublisher.publishEvent(new FeltColorVariantCreatedEvent(feltColorVariant));

        return toFeltDto(feltColorVariant);
    }

    @Transactional
    public FeltDto update(Long id, UpdateFeltDto dto) {
        FeltColorVariant feltColorVariant = feltColorVariantRepo
                .findById(id)
                .orElseThrow(() -> new FeltNotFoundException(id));

        if (dto.color() != null) {
            feltColorVariant.setColor(dto.color());
        }
        if (dto.supplierColor() != null) {
            feltColorVariant.setSupplierColor(dto.supplierColor());
        }

        updateVariant(dto, feltColorVariant);

        return toFeltDto(feltColorVariant);
    }

    @Transactional
    public void delete(Long id) {
        FeltColorVariant colorVariant = feltColorVariantRepo
                .findById(id)
                .orElseThrow(() -> new FeltNotFoundException(id));

        FeltVariant variant = colorVariant.getFeltVariant();
        Felt felt = variant.getFelt();

        feltColorVariantRepo.delete(colorVariant);
        feltColorVariantRepo.flush();

        if (feltColorVariantRepo.countByFeltVariantId(variant.getId()) == 0) {
            feltVariantRepo.delete(variant);
            feltVariantRepo.flush();

            if (feltVariantRepo.countByFelt(felt) == 0) {
                feltRepo.delete(felt);
            }
        }
    }

    // ─── Felt Roll CRUD ──────────────────────────────────────────────────────────

    public List<FeltRollDto> findAllByFelt(Long feltId) {
        if (!feltColorVariantRepo.existsById(feltId)) {
            throw new FeltRollNotFoundException(feltId);
        }
        return feltRollRepo.findByFeltColorVariantId(feltId)
                           .stream()
                           .map(feltRollMapper::toDto)
                           .toList();
    }

    public FeltRollDto findRollById(Long id) {
        return feltRollRepo.findById(id)
                           .map(feltRollMapper::toDto)
                           .orElseThrow(() -> new FeltRollNotFoundException(id));
    }

    @Transactional
    public FeltRollDto createRoll(CreateFeltRollDto dto) {
        FeltColorVariant colorVariant = feltColorVariantRepo.findById(dto.feltId())
                                                            .orElseThrow(() -> new FeltRollNotFoundException(dto.feltId()));

        Batch batch = resolveOptionalBatch(dto.batchId());
        Storage storage = resolveOptionalStorage(dto.storageId());

        FeltRoll roll = new FeltRoll(colorVariant, batch, storage, dto.length(), dto.width());
        roll = feltRollRepo.save(roll);

        eventPublisher.publishEvent(new FeltRollCreatedEvent(roll));

        return feltRollMapper.toDto(roll);
    }

    @Transactional
    public FeltRollDto updateRoll(Long id, UpdateFeltRollDto dto) {
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
    public void deleteRoll(Long id) {
        if (!feltRollRepo.existsById(id)) {
            throw new FeltRollNotFoundException(id);
        }
        feltRollRepo.deleteById(id);
    }

    // ─── Private helpers ─────────────────────────────────────────────────────────

    private FeltDto toFeltDto(FeltColorVariant feltColorVariant) {
        FeltDto dto = feltMapper.toDto(feltColorVariant);
        return enrichWithSupply(dto);
    }

    private FeltDto enrichWithSupply(FeltDto dto) {
        return supplyRepository.findById(dto.id())
                .map(supply -> new FeltDto(
                        dto.id(), dto.color(), dto.supplierColor(),
                        dto.thickness(), dto.density(), dto.price(),
                        dto.feltVariantId(), dto.articleNumber(),
                        dto.supplierId(), dto.supplierName(),
                        dto.feltId(), dto.feltTypeId(), dto.feltTypeName(),
                        supply.isLowOnSupply(), supply.isHasBeenReordered()))
                .orElse(dto);
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

    private void updateVariant(UpdateFeltDto dto, FeltColorVariant colorVariant) {
        FeltVariant variant = colorVariant.getFeltVariant();
        Felt currentFelt = variant.getFelt();

        FeltType targetType = currentFelt.getFeltType();
        if (dto.feltTypeId() != null) {
            targetType = feltTypeRepo
                    .findById(dto.feltTypeId())
                    .orElseThrow(() -> new InvalidFeltTypeReferenceException(dto.feltTypeId()));
        }

        Supplier targetSupplier = currentFelt.getSupplier();
        if (dto.supplierId() != null) {
            targetSupplier = supplierRepo
                    .findById(dto.supplierId())
                    .orElseThrow(() -> new InvalidSupplierReferenceException(dto.supplierId()));
        }

        updateFeltAndVariant(dto, colorVariant, currentFelt, variant, targetType, targetSupplier);
    }

    private void updateFeltAndVariant(
            UpdateFeltDto dto,
            FeltColorVariant colorVariant,
            Felt currentFelt,
            FeltVariant variant,
            FeltType targetType,
            Supplier targetSupplier
    ) {
        String targetArticle = dto.articleNumber() != null ? dto.articleNumber() : currentFelt.getArticleNumber();
        Double targetThick = dto.thickness() != null ? dto.thickness() : variant.getThickness();
        Double targetDensity = dto.density() != null ? dto.density() : variant.getDensity();
        BigDecimal targetPrice = dto.price() != null ? dto.price() : variant.getPrice();

        boolean feltChanged = hasFeltChanged(currentFelt, targetType.getId(), targetSupplier.getId(), targetArticle);
        boolean variantChanged = hasVariantChanged(variant, targetThick, targetDensity, targetPrice);

        if (!feltChanged && !variantChanged) {
            return;
        }

        boolean variantIsShared = feltColorVariantRepo.countByFeltVariantId(variant.getId()) > 1;

        if (variantIsShared) {
            Felt targetFelt = feltChanged
                    ? resolveFelt(targetType, targetSupplier, targetArticle)
                    : currentFelt;
            updateSharedFeltVariant(colorVariant, targetFelt, targetThick, targetDensity, targetPrice);
            return;
        }

        if (feltChanged) {
            boolean feltIsShared = feltVariantRepo.countByFelt(currentFelt) > 1;
            if (feltIsShared) {
                variant.setFelt(resolveFelt(targetType, targetSupplier, targetArticle));
            } else {
                updateFelt(currentFelt, variant, targetType, targetSupplier, targetArticle);
            }
        }
        if (variantChanged) {
            variant.setThickness(targetThick);
            variant.setDensity(targetDensity);
            variant.setPrice(targetPrice);
        }
    }

    private void updateFelt(
            Felt currentFelt,
            FeltVariant variant,
            FeltType targetType,
            Supplier targetSupplier,
            String targetArticle
    ) {
        Optional<Felt> existingFelt = feltRepo
                .findByFeltTypeAndSupplierAndArticleNumber(targetType, targetSupplier, targetArticle);

        if (existingFelt.isPresent() && !existingFelt.get()
                                                     .getId()
                                                     .equals(currentFelt.getId())) {
            variant.setFelt(existingFelt.get());
            feltRepo.delete(currentFelt);
        } else if (existingFelt.isEmpty()) {
            currentFelt.setFeltType(targetType);
            currentFelt.setSupplier(targetSupplier);
            currentFelt.setArticleNumber(targetArticle);
        }
    }

    private void updateSharedFeltVariant(
            FeltColorVariant colorVariant,
            Felt targetFelt,
            Double thickness,
            Double density,
            BigDecimal price
    ) {
        FeltVariant targetVariant = feltVariantRepo
                .findByFeltAndThicknessAndDensityAndPrice(targetFelt, thickness, density, price)
                .orElseGet(() -> feltVariantRepo.save(
                        new FeltVariant(targetFelt, thickness, density, price))
                );
        colorVariant.setFeltVariant(targetVariant);
    }

    private boolean hasFeltChanged(Felt felt, Long targetTypeId, Long targetSupplierId, String targetArticleNumber) {
        return !felt.getFeltType()
                    .getId()
                    .equals(targetTypeId)
                || !felt.getSupplier()
                        .getId()
                        .equals(targetSupplierId)
                || !felt.getArticleNumber()
                        .equals(targetArticleNumber);
    }

    private boolean hasVariantChanged(
            FeltVariant feltVariant,
            Double targetThickness,
            Double targetDensity,
            BigDecimal targetPrice
    ) {
        return !feltVariant.getThickness()
                           .equals(targetThickness)
                || !feltVariant.getDensity()
                               .equals(targetDensity)
                || feltVariant.getPrice()
                              .compareTo(targetPrice) != 0;
    }

    private Felt resolveFelt(FeltType type, Supplier supplier, String articleNumber) {
        return feltRepo
                .findByFeltTypeAndSupplierAndArticleNumber(type, supplier, articleNumber)
                .orElseGet(() -> feltRepo.save(new Felt(type, supplier, articleNumber)));
    }
}
