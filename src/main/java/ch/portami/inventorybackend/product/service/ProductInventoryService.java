package ch.portami.inventorybackend.product.service;

import ch.portami.inventorybackend.core.entity.Storage;
import ch.portami.inventorybackend.core.exceptions.InvalidStorageReferenceException;
import ch.portami.inventorybackend.core.repository.StorageRepository;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryDto;
import ch.portami.inventorybackend.product.dto.productinventory.ProductInventoryMapper;
import ch.portami.inventorybackend.product.dto.productinventory.UpdateProductInventoryDto;
import ch.portami.inventorybackend.product.entity.ProductInventory;
import ch.portami.inventorybackend.product.entity.ProductVariant;
import ch.portami.inventorybackend.product.exception.InvalidProductVariantReferenceException;
import ch.portami.inventorybackend.product.exception.NotEnoughInventoryException;
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
