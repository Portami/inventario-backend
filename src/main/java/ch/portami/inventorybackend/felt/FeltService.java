package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.FeltTypeDto;
import ch.portami.inventorybackend.felt.dto.SupplierDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.exception.FeltNotFoundException;
import ch.portami.inventorybackend.felt.exception.InvalidFeltTypeReferenceException;
import ch.portami.inventorybackend.felt.exception.InvalidSupplierReferenceException;
import ch.portami.inventorybackend.felt.mapper.FeltMapper;
import ch.portami.inventorybackend.felt.mapper.FeltTypeMapper;
import ch.portami.inventorybackend.felt.mapper.SupplierMapper;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static ch.portami.inventorybackend.core.util.NullSafeMapper.applyIfPresent;

/**
 * Service for retrieving and managing felts, along with read access to their felt types and
 * suppliers.
 */
@Service
@Transactional(readOnly = true)
public class FeltService {

    private final FeltTypeRepository feltTypeRepo;
    private final FeltRepository feltRepo;
    private final SupplierRepository supplierRepo;
    private final FeltMapper feltMapper;
    private final FeltTypeMapper feltTypeMapper;
    private final SupplierMapper supplierMapper;

    public FeltService(
            FeltTypeRepository feltTypeRepo,
            FeltRepository feltRepo,
            SupplierRepository supplierRepo,
            FeltMapper feltMapper, FeltTypeMapper feltTypeMapper,
            SupplierMapper supplierMapper
    ) {
        this.feltTypeRepo = feltTypeRepo;
        this.feltRepo = feltRepo;
        this.supplierRepo = supplierRepo;
        this.feltMapper = feltMapper;
        this.feltTypeMapper = feltTypeMapper;
        this.supplierMapper = supplierMapper;
    }

    /**
     * Retrieves all felts.
     *
     * @return a list of DTOs for all felts
     */
    public List<FeltDto> findAll() {
        return feltRepo.findAll()
                       .stream()
                       .map(feltMapper::toDto)
                       .toList();
    }

    /**
     * Retrieves a felt by its ID.
     *
     * @param id the ID of the felt to retrieve
     * @return the DTO of the retrieved felt
     * @throws FeltNotFoundException if no felt with the given ID exists
     */
    public FeltDto findById(Long id) {
        return feltRepo.findById(id)
                       .map(feltMapper::toDto)
                       .orElseThrow(() -> new FeltNotFoundException(id));
    }

    /**
     * Retrieves all felt types.
     *
     * @return a list of DTOs for all felt types
     */
    public List<FeltTypeDto> findAllFeltTypes() {
        return feltTypeRepo.findAll()
                           .stream()
                           .map(feltTypeMapper::toDto)
                           .toList();
    }

    /**
     * Retrieves all suppliers.
     *
     * @return a list of DTOs for all suppliers
     */
    public List<SupplierDto> findAllSuppliers() {
        return supplierRepo.findAll()
                           .stream()
                           .map(supplierMapper::toDto)
                           .toList();
    }

    /**
     * Creates a new felt, resolving its referenced felt type and supplier.
     *
     * @param dto the data for the new felt
     * @return the DTO of the created felt
     * @throws InvalidFeltTypeReferenceException if the referenced felt type does not exist
     * @throws InvalidSupplierReferenceException if the referenced supplier does not exist
     */
    @Transactional
    public FeltDto create(CreateFeltDto dto) {
        FeltType feltType = feltTypeRepo.findById(dto.feltTypeId())
                                        .orElseThrow(() -> new InvalidFeltTypeReferenceException(dto.feltTypeId()));

        Supplier supplier = supplierRepo.findById(dto.supplierId())
                                        .orElseThrow(() -> new InvalidSupplierReferenceException(dto.supplierId()));

        Felt felt = feltRepo.save(new Felt(
                feltType, supplier, dto.articleNumber(),
                dto.thickness(), dto.density(), dto.price(),
                dto.color(), dto.supplierColor()
        ));

        return feltMapper.toDto(felt);
    }

    /**
     * Applies a partial update to a felt. Only non-null fields of the DTO are applied; referenced
     * felt type and supplier are re-resolved when supplied.
     *
     * @param id  the ID of the felt to update
     * @param dto the requested updates; null fields are left unchanged
     * @return the DTO of the updated felt
     * @throws FeltNotFoundException             if no felt with the given ID exists
     * @throws InvalidFeltTypeReferenceException if a referenced felt type does not exist
     * @throws InvalidSupplierReferenceException if a referenced supplier does not exist
     */
    @Transactional
    public FeltDto update(Long id, UpdateFeltDto dto) {
        Felt felt = feltRepo.findById(id)
                            .orElseThrow(() -> new FeltNotFoundException(id));

        applyIfPresent(dto::color, felt::setColor);
        applyIfPresent(dto::supplierColor, felt::setSupplierColor);
        applyIfPresent(dto::thickness, felt::setThickness);
        applyIfPresent(dto::density, felt::setDensity);
        applyIfPresent(dto::price, felt::setPrice);
        applyIfPresent(dto::articleNumber, felt::setArticleNumber);
        applyIfPresent(dto::isLowOnSupply, felt::setLowOnSupply);
        applyIfPresent(dto::hasBeenReordered, felt::setHasBeenReordered);

        applyIfPresent(dto::feltTypeId, feltTypeId -> feltTypeRepo.findById(feltTypeId)
                                                                  .orElseThrow(
                                                                          () -> new InvalidFeltTypeReferenceException(
                                                                                  feltTypeId)), felt::setFeltType);

        applyIfPresent(dto::supplierId, supplierId -> supplierRepo.findById(supplierId)
                                                                  .orElseThrow(
                                                                          () -> new InvalidSupplierReferenceException(
                                                                                  supplierId)), felt::setSupplier);

        return feltMapper.toDto(felt);
    }

    /**
     * Deletes a felt by its ID. Deleting a non-existent felt is a no-op.
     *
     * @param id the ID of the felt to delete
     */
    @Transactional
    public void delete(Long id) {
        feltRepo.deleteById(id);
    }
}
