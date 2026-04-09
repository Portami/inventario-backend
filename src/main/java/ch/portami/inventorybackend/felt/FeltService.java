package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import org.springframework.stereotype.Service;

@Service
public class FeltService {
    private final SupplierRepository supplierRepository;
    private final FeltTypeRepository feltTypeRepository;

    public FeltService(SupplierRepository supplierRepository, FeltTypeRepository feltTypeRepository) {
        this.supplierRepository = supplierRepository;
        this.feltTypeRepository = feltTypeRepository;
    }
}
