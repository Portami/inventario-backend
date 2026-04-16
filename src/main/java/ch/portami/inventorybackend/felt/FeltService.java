package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.felt.dto.CreateFeltColorVariantDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltTypeDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltVariantDto;
import ch.portami.inventorybackend.felt.dto.FeltColorVariantDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.FeltTypeDto;
import ch.portami.inventorybackend.felt.dto.FeltVariantDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltColorVariantDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltTypeDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltVariantDto;
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
public class FeltService {

    private final FeltRepository feltRepository;
    private final FeltTypeRepository feltTypeRepository;
    private final FeltVariantRepository feltVariantRepository;
    private final FeltColorVariantRepository feltColorVariantRepository;
    private final SupplierRepository supplierRepository;

    public FeltService(
        FeltRepository feltRepository,
        FeltTypeRepository feltTypeRepository,
        FeltVariantRepository feltVariantRepository,
        FeltColorVariantRepository feltColorVariantRepository,
        SupplierRepository supplierRepository
    ) {
        this.feltRepository = feltRepository;
        this.feltTypeRepository = feltTypeRepository;
        this.feltVariantRepository = feltVariantRepository;
        this.feltColorVariantRepository = feltColorVariantRepository;
        this.supplierRepository = supplierRepository;
    }

    // region FeltTypes
    public List<FeltTypeDto> getAllFeltTypes() {
        return feltTypeRepository
            .findAll()
            .stream()
            .map(this::toFeltTypeResponse)
            .toList();
    }

    public FeltTypeDto getFeltTypeById(Long id) {
        return toFeltTypeResponse(findFeltTypeOrThrow(id));
    }

    @Transactional
    public FeltTypeDto createFeltType(CreateFeltTypeDto request) {
        FeltType feltType = new FeltType(request.name());
        return toFeltTypeResponse(feltTypeRepository.save(feltType));
    }

    @Transactional
    public FeltTypeDto updateFeltType(Long id, UpdateFeltTypeDto request) {
        FeltType feltType = findFeltTypeOrThrow(id);
        if (request.name() != null) {
            feltType.setName(request.name());
        }
        return toFeltTypeResponse(feltTypeRepository.save(feltType));
    }

    @Transactional
    public void deleteFeltType(Long id) {
        if (!feltTypeRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltType not found: " + id);
        }
        feltTypeRepository.deleteById(id);
    }
    // endregion

    // region Felts
    @Transactional(readOnly = true)
    public List<FeltDto> getAllFelts() {
        return feltRepository
            .findAll()
            .stream()
            .map(this::toFeltResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public FeltDto getFeltById(Long id) {
        return toFeltResponse(findFeltOrThrow(id));
    }

    @Transactional
    public FeltDto createFelt(CreateFeltDto request) {
        FeltType feltType = findFeltTypeOrThrow(request.feltTypeId());
        Supplier supplier = findSupplierOrThrow(request.supplierId());
        Felt felt = new Felt(feltType, supplier, request.articleNumber());
        return toFeltResponse(feltRepository.save(felt));
    }

    @Transactional
    public FeltDto updateFelt(Long id, UpdateFeltDto request) {
        Felt felt = findFeltOrThrow(id);
        if (request.feltTypeId() != null) {
            felt.setFeltType(findFeltTypeOrThrow(request.feltTypeId()));
        }
        if (request.supplierId() != null) {
            felt.setSupplier(findSupplierOrThrow(request.supplierId()));
        }
        if (request.articleNumber() != null) {
            felt.setArticleNumber(request.articleNumber());
        }
        return toFeltResponse(feltRepository.save(felt));
    }

    @Transactional
    public void deleteFelt(Long id) {
        if (!feltRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found: " + id);
        }
        feltRepository.deleteById(id);
    }
    // endregion

    // region FeltVariants
    @Transactional(readOnly = true)
    public List<FeltVariantDto> getAllFeltVariants() {
        return feltVariantRepository
            .findAll()
            .stream()
            .map(this::toFeltVariantResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public FeltVariantDto getFeltVariantById(Long id) {
        return toFeltVariantResponse(findFeltVariantOrThrow(id));
    }

    @Transactional
    public FeltVariantDto createFeltVariant(CreateFeltVariantDto request) {
        Felt felt = findFeltOrThrow(request.feltId());
        FeltVariant variant = new FeltVariant(felt, request.thickness(), request.density(), request.price());
        return toFeltVariantResponse(feltVariantRepository.save(variant));
    }

    @Transactional
    public FeltVariantDto updateFeltVariant(Long id, UpdateFeltVariantDto request) {
        FeltVariant variant = findFeltVariantOrThrow(id);
        if (request.thickness() != null) {
            variant.setThickness(request.thickness());
        }
        if (request.density() != null) {
            variant.setDensity(request.density());
        }
        if (request.price() != null) {
            variant.setPrice(request.price());
        }
        return toFeltVariantResponse(feltVariantRepository.save(variant));
    }

    @Transactional
    public void deleteFeltVariant(Long id) {
        if (!feltVariantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltVariant not found: " + id);
        }
        feltVariantRepository.deleteById(id);
    }
    // endregion

    // region FeltColorVariants
    @Transactional(readOnly = true)
    public List<FeltColorVariantDto> getAllFeltColorVariants() {
        return feltColorVariantRepository
            .findAll()
            .stream()
            .map(this::toFeltColorVariantResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public FeltColorVariantDto getFeltColorVariantById(Long id) {
        return toFeltColorVariantResponse(findFeltColorVariantOrThrow(id));
    }

    @Transactional
    public FeltColorVariantDto createFeltColorVariant(CreateFeltColorVariantDto request) {
        FeltVariant variant = findFeltVariantOrThrow(request.feltVariantId());
        FeltColorVariant colorVariant = new FeltColorVariant(variant, request.color());
        colorVariant.setSupplierColor(request.supplierColor());
        return toFeltColorVariantResponse(feltColorVariantRepository.save(colorVariant));
    }

    @Transactional
    public FeltColorVariantDto updateFeltColorVariant(Long id, UpdateFeltColorVariantDto request) {
        FeltColorVariant colorVariant = findFeltColorVariantOrThrow(id);
        if (request.color() != null) {
            colorVariant.setColor(request.color());
        }
        if (request.supplierColor() != null) {
            colorVariant.setSupplierColor(request.supplierColor());
        }
        return toFeltColorVariantResponse(feltColorVariantRepository.save(colorVariant));
    }

    @Transactional
    public void deleteFeltColorVariant(Long id) {
        if (!feltColorVariantRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltColorVariant not found: " + id);
        }
        feltColorVariantRepository.deleteById(id);
    }
    // endregion

    private FeltType findFeltTypeOrThrow(Long id) {
        return feltTypeRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltType not found: " + id)
            );
    }

    private Felt findFeltOrThrow(Long id) {
        return feltRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Felt not found: " + id)
            );
    }

    private FeltVariant findFeltVariantOrThrow(Long id) {
        return feltVariantRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltVariant not found: " + id)
            );
    }

    private FeltColorVariant findFeltColorVariantOrThrow(Long id) {
        return feltColorVariantRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "FeltColorVariant not found: " + id)
            );
    }

    private Supplier findSupplierOrThrow(Long id) {
        return supplierRepository
            .findById(id)
            .orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found: " + id)
            );
    }

    private FeltTypeDto toFeltTypeResponse(FeltType feltType) {
        return new FeltTypeDto(
            feltType.getId(),
            feltType.getName()
        );
    }

    private FeltDto toFeltResponse(Felt felt) {
        return new FeltDto(
            felt.getId(),
            felt.getArticleNumber(),
            felt.getFeltType().getId(),
            felt.getFeltType().getName(),
            felt.getSupplier().getId(),
            felt.getSupplier().getName()
        );
    }

    private FeltVariantDto toFeltVariantResponse(FeltVariant variant) {
        Felt felt = variant.getFelt();
        return new FeltVariantDto(
            variant.getId(),
            felt.getId(),
            felt.getArticleNumber(),
            felt.getFeltType().getName(),
            felt.getSupplier().getName(),
            variant.getThickness(),
            variant.getDensity(),
            variant.getPrice()
        );
    }

    private FeltColorVariantDto toFeltColorVariantResponse(FeltColorVariant colorVariant) {
        FeltVariant variant = colorVariant.getFeltVariant();
        Felt felt = variant.getFelt();
        return new FeltColorVariantDto(
            colorVariant.getId(),
            colorVariant.getColor(),
            colorVariant.getSupplierColor(),
            variant.getId(),
            felt.getId(),
            felt.getArticleNumber(),
            felt.getFeltType().getName(),
            felt.getSupplier().getName()
        );
    }
}
