package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.core.storage.entity.Storage;
import ch.portami.inventorybackend.core.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.core.storage.repository.StorageRepository;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryDto;
import ch.portami.inventorybackend.product.dto.productinventory.UpdateProductInventoryDto;
import ch.portami.inventorybackend.product.entity.ProductInventory;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.exception.InvalidProductVariantReferenceException;
import ch.portami.inventorybackend.product.exception.NotEnoughInventoryException;
import ch.portami.inventorybackend.product.mapper.ProductInventoryMapper;
import ch.portami.inventorybackend.product.repository.ProductInventoryRepository;
import ch.portami.inventorybackend.product.repository.ProductVariantRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing product inventory across different storage locations.
 */
@Service
public class ProductInventoryService {

    private final ProductInventoryRepository productInventoryRepository;
    private final ProductVariantRepository productVariantRepository;
    private final StorageRepository storageRepository;
    private final ProductInventoryMapper productInventoryMapper;

    /**
     * Creates a new ProductInventoryService with the given dependencies.
     *
     * @param productInventoryRepository the repository for accessing product inventory data
     * @param productVariantRepository   the repository for accessing product variant data, needed for resolving product
     *                                   variant references
     * @param storageRepository          the repository for accessing storage data, needed for resolving storage
     *                                   references
     * @param productInventoryMapper     the mapper for converting between ProductInventory entities and DTOs
     */
    public ProductInventoryService(ProductInventoryRepository productInventoryRepository,
            ProductVariantRepository productVariantRepository, StorageRepository storageRepository,
            ProductInventoryMapper productInventoryMapper) {
        this.productInventoryRepository = productInventoryRepository;
        this.productVariantRepository = productVariantRepository;
        this.storageRepository = storageRepository;
        this.productInventoryMapper = productInventoryMapper;
    }

    /**
     * Applies a list of inventory changes atomically, which can include both additions and removals of inventory for
     * specific product variants and storage locations.
     *
     * @param inventoryChanges a list of inventory changes to apply
     * @return a list of DTOs representing the updated inventory entries after applying the changes. They are returned
     * in the same order as in the input list and multiple changes for the same product variant and storage location are
     * returned as separate DTOs.
     * @throws InvalidProductVariantReferenceException if any of the changes reference a product variant that does not
     *                                                 exist
     * @throws InvalidStorageReferenceException        if any of the changes reference a storage location that does not
     *                                                 exist
     * @throws NotEnoughInventoryException             if any of the changes would result in negative inventory
     */
    @Transactional
    public List<ProductInventoryDto> changeInventory(List<UpdateProductInventoryDto> inventoryChanges) {

        List<ProductInventoryDto> results = new ArrayList<>(inventoryChanges.size());

        for (UpdateProductInventoryDto change : inventoryChanges) {

            Optional<ProductInventory> inventoryEntry = productInventoryRepository.findByProductVariantIdAndStorageId(
                    change.productVariantId(), change.storageId());

            ProductInventoryDto updatedInventoryDto;

            if (inventoryEntry.isPresent()) {
                updatedInventoryDto = updateOrRemoveExistingEntry(change, inventoryEntry.get());
            } else {
                updatedInventoryDto = createNewEntry(change);
            }

            results.add(updatedInventoryDto);

        }

        return results;

    }

    private ProductInventoryDto updateOrRemoveExistingEntry(UpdateProductInventoryDto change,
            ProductInventory inventoryEntry) {

        ProductInventoryDto updatedInventoryDto;
        int newCount = inventoryEntry.getCount() + change.quantityChange();

        if (newCount > 0) {
            inventoryEntry.setCount(newCount);
            inventoryEntry = productInventoryRepository.save(inventoryEntry);
            updatedInventoryDto = productInventoryMapper.toProductInventoryDto(inventoryEntry);
        } else if (newCount == 0) {
            updatedInventoryDto = new ProductInventoryDto(change.storageId(), inventoryEntry.getStorage()
                                                                                            .getName(), 0);
            productInventoryRepository.delete(inventoryEntry);
        } else {
            throw new NotEnoughInventoryException(change.productVariantId(), change.storageId(),
                    -change.quantityChange(), inventoryEntry.getCount());
        }

        return updatedInventoryDto;

    }

    private ProductInventoryDto createNewEntry(UpdateProductInventoryDto change) {

        ProductInventoryDto updatedInventoryDto;
        ProductInventory inventoryEntry;

        Storage storage = storageRepository.findById(change.storageId())
                                           .orElseThrow(() -> new InvalidStorageReferenceException(change.storageId()));

        ProductVariant variant = productVariantRepository.findById(change.productVariantId())
                                                         .orElseThrow(() -> new InvalidProductVariantReferenceException(
                                                                 change.productVariantId()));

        if (change.quantityChange() > 0) {
            inventoryEntry = new ProductInventory(variant, storage, change.quantityChange());
            inventoryEntry = productInventoryRepository.save(inventoryEntry);
            updatedInventoryDto = productInventoryMapper.toProductInventoryDto(inventoryEntry);
        } else if (change.quantityChange() == 0) {
            updatedInventoryDto = new ProductInventoryDto(change.storageId(), storage.getName(), 0);
        } else {
            throw new NotEnoughInventoryException(change.productVariantId(), change.storageId(),
                    -change.quantityChange());
        }

        return updatedInventoryDto;

    }

}
