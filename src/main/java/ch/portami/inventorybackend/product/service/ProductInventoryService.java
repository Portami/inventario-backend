package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.core.entity.Storage;
import ch.portami.inventorybackend.core.exceptions.StorageNotFoundException;
import ch.portami.inventorybackend.core.repository.StorageRepository;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryChangeDto;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryDto;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryMapper;
import ch.portami.inventorybackend.product.entity.ProductInventory;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.exception.ProductVariantNotFoundException;
import ch.portami.inventorybackend.product.repository.ProductInventoryRepository;
import ch.portami.inventorybackend.product.repository.ProductVariantRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductInventoryService {

    private final ProductInventoryRepository productInventoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final StorageRepository storageRepository;
    private final ProductInventoryMapper productInventoryMapper;

    public ProductInventoryService(ProductInventoryRepository productInventoryRepository,
            ProductVariantRepository productVariantRepository, StorageRepository storageRepository,
            ProductInventoryMapper productInventoryMapper) {
        this.productInventoryRepository = productInventoryRepository;
        this.productVariantRepository = productVariantRepository;
        this.storageRepository = storageRepository;
        this.productInventoryMapper = productInventoryMapper;
    }

    @Transactional
    public List<ProductInventoryDto> changeInventory(List<ProductInventoryChangeDto> inventoryChanges) {

        List<ProductInventoryDto> result = new ArrayList<>(inventoryChanges.size());

        for (ProductInventoryChangeDto change : inventoryChanges) {

            Optional<ProductInventory> inventoryEntryOpt = productInventoryRepository.findByProductVariantIdAndStorageId(
                    change.productVariantId(), change.storageId());

            ProductInventory inventoryEntry;

            if (inventoryEntryOpt.isPresent()) {
                inventoryEntry = inventoryEntryOpt.get();
                inventoryEntry.setCount(inventoryEntry.getCount() + change.quantityChange());
            } else {
                Storage storage = storageRepository.findById(change.storageId())
                                                   .orElseThrow(() -> new StorageNotFoundException(change.storageId()));
                ProductVariant variant = productVariantRepository.findById(change.productVariantId())
                                                                 .orElseThrow(() -> new ProductVariantNotFoundException(
                                                                         change.productVariantId()));

                inventoryEntry = new ProductInventory(variant, storage, change.quantityChange());
            }

            inventoryEntry = productInventoryRepository.save(inventoryEntry);
            result.add(productInventoryMapper.toProductInventoryDto(inventoryEntry));

        }

        return result;

    }

}
