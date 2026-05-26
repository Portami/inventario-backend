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

@Service
@Transactional(readOnly = true)
public class StorageService {

    private final StorageRepository storageRepository;
    private final StorageMapper storageMapper;

    public StorageService(StorageRepository storageRepository, StorageMapper storageMapper) {
        this.storageRepository = storageRepository;
        this.storageMapper = storageMapper;
    }

    public List<StorageDto> findAll() {
        return storageRepository.findAll()
                                .stream()
                                .map(storageMapper::toStorageDto)
                                .toList();
    }

    public Optional<Storage> findById(Long id) {
        return storageRepository.findById(id);
    }

    public Storage getExistingById(Long id) {
        return storageRepository.findById(id)
                                .orElseThrow(() -> new InvalidStorageReferenceException(id));
    }
}
