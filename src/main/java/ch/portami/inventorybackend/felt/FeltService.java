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
import java.util.List;
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
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found")
            );
    }

    @Transactional
    public FeltDto create(CreateFeltDto dto) {
        Supplier supplier = supplierRepo
            .findById(dto.supplierId())
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found")
            );

        FeltType feltType = feltTypeRepo
            .findByName(dto.feltTypeName())
            .orElseGet(
                () -> feltTypeRepo.save(new FeltType(dto.feltTypeName()))
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

    private FeltType updateType(UpdateFeltDto dto, Felt felt) {
        FeltType type = felt.getFeltType();
        boolean hasChanges = !type.getName().equals(dto.feltTypeName());

        if (hasChanges) {
            FeltType targetFeltType = feltTypeRepo
                .findByName(dto.feltTypeName())
                .orElseGet(() -> feltTypeRepo.save(
                    new FeltType(dto.feltTypeName())
                ));
            // Set FK directly — do not touch in-memory collections to avoid orphan-removal trap
            felt.setFeltType(targetFeltType);
            return targetFeltType;
        }
        return type;
    }

    private Felt updateFelt(UpdateFeltDto dto, FeltVariant feltVariant) {
        Felt felt = feltVariant.getFelt();
        FeltType type = updateType(dto, felt);
        boolean hasChanges = !felt.getArticleNumber().equals(dto.articleNumber());

        if (hasChanges) {
            Felt targetFelt = feltRepo
                .findByArticleNumber(dto.articleNumber())
                .orElseGet(() -> feltRepo.save(
                    new Felt(type, felt.getSupplier(), dto.articleNumber())
                ));
            // Set FK directly — do not touch in-memory collections to avoid orphan-removal trap
            feltVariant.setFelt(targetFelt);
            return targetFelt;
        }
        return felt;
    }

    private FeltVariant updateVariant(UpdateFeltDto dto, FeltColorVariant colorVariant) {
        FeltVariant variant = colorVariant.getFeltVariant();
        Felt felt = updateFelt(dto, variant);

        boolean hasChanges = !variant.getThickness().equals(dto.thickness())
            || !variant.getDensity().equals(dto.density())
            || variant.getPrice().compareTo(dto.price()) != 0;

        if (hasChanges) {
            int count = feltColorVariantRepo.countByFeltVariantId(variant.getId());
            if (count > 1) {
                FeltVariant targetVariant = feltVariantRepo
                    .findByFeltAndThicknessAndDensityAndPrice(felt, dto.thickness(), dto.density(), dto.price())
                    .orElseGet(() -> feltVariantRepo.save(
                        new FeltVariant(felt, dto.thickness(), dto.density(), dto.price())
                    ));
                // Set FK directly — do not touch in-memory collections to avoid orphan-removal trap
                colorVariant.setFeltVariant(targetVariant);
                return  targetVariant;
            } else {
                variant.setFelt(felt);
                variant.setThickness(dto.thickness());
                variant.setDensity(dto.density());
                variant.setPrice(dto.price());
            }
        }
        return variant;
    }

    @Transactional
    public FeltDto update(Long id, UpdateFeltDto dto) {
        FeltColorVariant feltColorVariant = feltColorVariantRepo
            .findById(id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found")
            );

        feltColorVariant.setColor(dto.color());
        feltColorVariant.setSupplierColor(dto.supplierColor());

        updateVariant(dto, feltColorVariant);

        return toDto(feltColorVariant);
    }

    @Transactional
    public void delete(Long id) {
        if (!feltColorVariantRepo.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found");
        }
        feltColorVariantRepo.deleteById(id);
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
