package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.FeltVariant;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.FeltVariantRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class FeltService {

    private final FeltTypeRepository feltTypeRepo;
    private final FeltRepository feltRepo;
    private final FeltVariantRepository feltVariantRepo;
    private final FeltColorVariantRepository feltColorVariantRepo;
    private final SupplierRepository supplierRepo;

    public FeltService(
        FeltTypeRepository feltTypeRepo,
        FeltRepository feltRepo,
        FeltVariantRepository feltVariantRepo,
        FeltColorVariantRepository feltColorVariantRepo,
        SupplierRepository supplierRepo
    ) {
        this.feltTypeRepo = feltTypeRepo;
        this.feltRepo = feltRepo;
        this.feltVariantRepo = feltVariantRepo;
        this.feltColorVariantRepo = feltColorVariantRepo;
        this.supplierRepo = supplierRepo;
    }

    public List<FeltDto> findAll() {
        return feltColorVariantRepo
            .findAll()
            .stream()
            .map(this::toDto)
            .toList();
    }

    public FeltDto findById(Long id) {
        return feltColorVariantRepo
            .findById(id)
            .map(this::toDto)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found"))
            ;
    }

    @Transactional
    public FeltDto create(CreateFeltDto dto) {
        FeltType feltType = feltTypeRepo
            .findById(dto.feltTypeId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltType not found")
            );

        Supplier supplier = supplierRepo
            .findById(dto.supplierId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found")
            );

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

        return toDto(feltColorVariant);
    }

    @Transactional
    public FeltDto update(Long id, UpdateFeltDto dto) {
        FeltColorVariant feltColorVariant = feltColorVariantRepo
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found")
            );

        if (dto.color() != null) {
            feltColorVariant.setColor(dto.color());
        }
        if (dto.supplierColor() != null) {
            feltColorVariant.setSupplierColor(dto.supplierColor());
        }

        updateVariant(dto, feltColorVariant);

        return toDto(feltColorVariant);
    }

    @Transactional
    public void delete(Long id) {
        FeltColorVariant colorVariant = feltColorVariantRepo
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found")
            );

        FeltVariant variant = colorVariant.getFeltVariant();
        Felt felt = variant.getFelt();

        feltColorVariantRepo.delete(colorVariant);
        feltColorVariantRepo.flush();

        if (feltColorVariantRepo.countByFeltVariantId(variant.getId()) == 0) {
            feltVariantRepo.delete(variant);
            feltVariantRepo.flush();

            // if no more variants use this felt, delete the felt
            if (feltVariantRepo.countByFelt(felt) == 0) {
                feltRepo.delete(felt);
            }
        }
    }

    /**
     * Applies all variant-level and felt-level changes from the DTO to the given color variant.
     *
     * <p>Uses "effective target values": if a DTO field is null (omitted in a PATCH request) the
     * current entity value is used, so nothing changes at that level.
     *
     * <p>Two-level sharing check:
     * <ul>
     *   <li>FeltVariant shared → always find-or-create at both levels, re-point colorVariant.
     *   <li>FeltVariant exclusive, Felt shared → mutate variant in-place; find-or-create Felt.
     *   <li>Both exclusive → mutate variant in-place; mutate Felt in-place when possible,
     *       falling back to re-pointing only when a matching Felt already exists elsewhere.
     * </ul>
     */
    private void updateVariant(UpdateFeltDto dto, FeltColorVariant colorVariant) {
        FeltVariant variant = colorVariant.getFeltVariant();
        Felt currentFelt = variant.getFelt();

        FeltType targetType = currentFelt.getFeltType();
        if (dto.feltTypeId() != null) { // only update if not null
            targetType = feltTypeRepo
                .findById(dto.feltTypeId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltType not found"));
        }

        Supplier targetSupplier = currentFelt.getSupplier();
        if (dto.supplierId() != null) { // only update if not null
            targetSupplier = supplierRepo
                .findById(dto.supplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found"));
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
        // we only update if the property is not null in the dto
        String targetArticle = dto.articleNumber() != null ? dto.articleNumber() : currentFelt.getArticleNumber();
        Double targetThick = dto.thickness() != null ? dto.thickness() : variant.getThickness();
        Double targetDensity = dto.density() != null ? dto.density() : variant.getDensity();
        BigDecimal targetPrice = dto.price() != null ? dto.price() : variant.getPrice();

        boolean feltChanged = hasFeltChanged(currentFelt, targetType.getId(), targetSupplier.getId(), targetArticle);
        boolean variantChanged = hasVariantChanged(variant, targetThick, targetDensity, targetPrice);

        if (!feltChanged && !variantChanged) {
            return; // early return if nothing else changed
        }

        boolean variantIsShared = feltColorVariantRepo.countByFeltVariantId(variant.getId()) > 1;

        if (variantIsShared) {
            // Cannot mutate anything in-place — find-or-create at both levels.
            Felt targetFelt = feltChanged
                ? resolveFelt(targetType, targetSupplier, targetArticle)
                : currentFelt;
            updateSharedFeltVariant(colorVariant, targetFelt, targetThick, targetDensity, targetPrice);
            return;
        }
        // Variant is safe to mutate in-place. Handle the Felt separately.
        if (feltChanged) {
            boolean feltIsShared = feltVariantRepo.countByFelt(currentFelt) > 1;
            if (feltIsShared) {
                // Other FeltVariants depend on this Felt — find-or-create, never mutate it.
                // Set FK directly — do not touch in-memory collections to avoid orphan-removal trap
                variant.setFelt(resolveFelt(targetType, targetSupplier, targetArticle));
                return;

            }
            updateFelt(currentFelt, variant, targetType, targetSupplier, targetArticle);
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
        // Felt is also exclusive — prefer in-place mutation to avoid orphaned records.
        Optional<Felt> existingFelt = feltRepo
            .findByFeltTypeAndSupplierAndArticleNumber(targetType, targetSupplier, targetArticle);

        if (existingFelt.isPresent() && !existingFelt.get()
                                                     .getId()
                                                     .equals(currentFelt.getId())) {
            // A different Felt with the target identity already exists — re-point the
            // variant FK and clean up the now-orphaned exclusive Felt.
            // Set FK directly — do not touch in-memory collections to avoid orphan-removal trap
            variant.setFelt(existingFelt.get());
            feltRepo.delete(currentFelt);

        } else if (existingFelt.isEmpty()) {
            // No Felt with the target identity exists yet — mutate the exclusive Felt
            // in-place so no new entity is created and no orphan accumulates.
            currentFelt.setFeltType(targetType);
            currentFelt.setSupplier(targetSupplier);
            currentFelt.setArticleNumber(targetArticle);
        }
        // else existingTarget == currentFelt → already matches, nothing to do
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
        // Set FK directly — do not touch in-memory collections to avoid orphan-removal trap
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

    /**
     * Finds an existing Felt by its natural key or creates a new one.
     */
    private Felt resolveFelt(FeltType type, Supplier supplier, String articleNumber) {
        return feltRepo
            .findByFeltTypeAndSupplierAndArticleNumber(type, supplier, articleNumber)
            .orElseGet(() -> feltRepo.save(new Felt(type, supplier, articleNumber)));
    }

    private FeltDto toDto(FeltColorVariant feltColorVariant) {
        FeltVariant feltVariant = feltColorVariant.getFeltVariant();
        Felt felt = feltVariant.getFelt();
        FeltType feltType = felt.getFeltType();
        Supplier supplier = felt.getSupplier();

        return new FeltDto(
            feltColorVariant.getId(),
            feltColorVariant.getColor(),
            feltColorVariant.getSupplierColor(),
            feltVariant.getThickness(),
            feltVariant.getDensity(),
            feltVariant.getPrice(),
            feltVariant.getId(),
            felt.getArticleNumber(),
            supplier.getId(),
            supplier.getName(),
            felt.getId(),
            feltType.getId(),
            feltType.getName()
        );
    }
}
