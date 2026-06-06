package ch.portami.inventorybackend.storage;

import ch.portami.inventorybackend.storage.dto.StorageDto;
import ch.portami.inventorybackend.storage.entity.Storage;
import ch.portami.inventorybackend.storage.exception.InvalidStorageReferenceException;
import ch.portami.inventorybackend.storage.mapper.StorageMapper;
import ch.portami.inventorybackend.storage.repository.StorageRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for retrieving storage locations and resolving storage references.
 */
@Service
@Transactional(readOnly = true)
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    public StorageService(StorageRepository storageRepository, StorageMapper storageMapper) {
        this.storageRepository = storageRepository;
        this.storageMapper = storageMapper;
    }

    /**
     * Retrieves all storage locations.
     *
     * @return a list of DTOs for all storage locations
     */
    public List<StorageDto> findAll() {
        return storageRepository.findAll()
                                .stream()
                                .map(storageMapper::toStorageDto)
                                .toList();
    }

    /**
     * Looks up a storage location by its ID without failing if it is absent.
     *
     * @param id the ID of the storage to look up
     * @return an {@link Optional} containing the storage entity, or empty if none exists
     */
    public Optional<Storage> findById(Long id) {
        return storageRepository.findById(id);
    }

    /**
     * Resolves a storage location that is expected to exist, for use when other operations reference
     * a storage by id.
     *
     * @param id the ID of the storage to resolve
     * @return the storage entity
     * @throws InvalidStorageReferenceException if no storage with the given ID exists
     */
    public Storage getExistingById(Long id) {
        return storageRepository.findById(id)
                                .orElseThrow(() -> new InvalidStorageReferenceException(id));
    }
}
