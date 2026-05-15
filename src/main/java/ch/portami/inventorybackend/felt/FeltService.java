package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.exception.FeltNotFoundException;
import ch.portami.inventorybackend.felt.exception.InvalidFeltTypeReferenceException;
import ch.portami.inventorybackend.felt.exception.InvalidSupplierReferenceException;
import ch.portami.inventorybackend.felt.mapper.FeltMapper;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import ch.portami.inventorybackend.felt.supply.SupplyService;
import ch.portami.inventorybackend.felt.supply.entity.Supply;
import ch.portami.inventorybackend.felt.supply.exception.SupplyNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class FeltService {

    private final SupplyService supplyService;
    private final FeltTypeRepository feltTypeRepo;
    private final FeltRepository feltRepo;
    private final SupplierRepository supplierRepo;
    private final FeltMapper feltMapper;

    public FeltService(
            SupplyService supplyService,
            FeltTypeRepository feltTypeRepo,
            FeltRepository feltRepo,
            SupplierRepository supplierRepo,
            FeltMapper feltMapper
    ) {
        this.supplyService = supplyService;
        this.feltTypeRepo = feltTypeRepo;
        this.feltRepo = feltRepo;
        this.supplierRepo = supplierRepo;
        this.feltMapper = feltMapper;
    }

    public List<FeltDto> findAll() {
        return feltRepo.findAll()
                .stream()
                .map(this::toFeltDto)
                .toList();
    }

    public FeltDto findById(Long id) {
        return feltRepo.findById(id)
                .map(this::toFeltDto)
                .orElseThrow(() -> new FeltNotFoundException(id));
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

        supplyService.createForFelt(felt);

        return toFeltDto(felt);
    }

    @Transactional
    public FeltDto update(Long id, UpdateFeltDto dto) {
        Felt felt = feltRepo.findById(id)
                .orElseThrow(() -> new FeltNotFoundException(id));

        if (dto.color() != null) {
            felt.setColor(dto.color());
        }
        if (dto.supplierColor() != null) {
            felt.setSupplierColor(dto.supplierColor());
        }
        if (dto.thickness() != null) {
            felt.setThickness(dto.thickness());
        }
        if (dto.density() != null) {
            felt.setDensity(dto.density());
        }
        if (dto.price() != null) {
            felt.setPrice(dto.price());
        }
        if (dto.articleNumber() != null) {
            felt.setArticleNumber(dto.articleNumber());
        }
        if (dto.feltTypeId() != null) {
            FeltType feltType = feltTypeRepo.findById(dto.feltTypeId())
                    .orElseThrow(() -> new InvalidFeltTypeReferenceException(dto.feltTypeId()));
            felt.setFeltType(feltType);
        }
        if (dto.supplierId() != null) {
            Supplier supplier = supplierRepo.findById(dto.supplierId())
                    .orElseThrow(() -> new InvalidSupplierReferenceException(dto.supplierId()));
            felt.setSupplier(supplier);
        }

        return toFeltDto(felt);
    }

    @Transactional
    public void delete(Long id) {
        Felt felt = feltRepo.findById(id)
                .orElseThrow(() -> new FeltNotFoundException(id));
        feltRepo.delete(felt);
    }

    private FeltDto toFeltDto(Felt felt) {
        Supply supply = supplyService.findByRollId(felt.getId())
                .orElseThrow(() -> new SupplyNotFoundException(felt.getId()));
        return feltMapper.toDto(felt, supply);
    }
}
