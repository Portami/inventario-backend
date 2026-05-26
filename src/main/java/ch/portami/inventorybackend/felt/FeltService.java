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

    public List<FeltDto> findAll() {
        return feltRepo.findAll()
                       .stream()
                       .map(feltMapper::toDto)
                       .toList();
    }

    public FeltDto findById(Long id) {
        return feltRepo.findById(id)
                       .map(feltMapper::toDto)
                       .orElseThrow(() -> new FeltNotFoundException(id));
    }

    public List<FeltTypeDto> findAllFeltTypes() {
        return feltTypeRepo.findAll()
                           .stream()
                           .map(feltTypeMapper::toDto)
                           .toList();
    }

    public List<SupplierDto> findAllSuppliers() {
        return supplierRepo.findAll()
                           .stream()
                           .map(supplierMapper::toDto)
                           .toList();
    }

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
                                                                                  id)), felt::setFeltType);

        applyIfPresent(dto::supplierId, supplierId -> supplierRepo.findById(supplierId)
                                                                  .orElseThrow(
                                                                          () -> new InvalidSupplierReferenceException(
                                                                                  id)), felt::setSupplier);

        return feltMapper.toDto(felt);
    }

    @Transactional
    public void delete(Long id) {
        Felt felt = feltRepo.findById(id)
                            .orElseThrow(() -> new FeltNotFoundException(id));
        feltRepo.delete(felt);
    }
}
