package ch.portami.inventorybackend.felt.supply;

import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.supply.dto.SupplyDto;
import ch.portami.inventorybackend.felt.supply.dto.UpdateSupplyDto;
import ch.portami.inventorybackend.felt.supply.entity.Supply;
import ch.portami.inventorybackend.felt.supply.repository.SupplyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class SupplyService {

    private final SupplyRepository supplyRepository;

    public SupplyService(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Transactional
    public void createForFelt(Felt felt) {
        supplyRepository.save(new Supply(false, false, felt));
    }

    public Optional<Supply> findByRollId(Long rollId) {
        return supplyRepository.findById(rollId);
    }

    @Transactional
    public SupplyDto updateSupply(Long rollId, UpdateSupplyDto dto) {
        Supply supply = supplyRepository.findById(rollId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supply not found"));

        if (dto.lowOnSupply() != null) {
            supply.setLowOnSupply(dto.lowOnSupply());
        }
        if (dto.reordered() != null) {
            supply.setHasBeenReordered(dto.reordered());
        }

        return toDto(supply);
    }

    public List<SupplyDto> findOpenReorders() {
        return supplyRepository.findAllByIsLowOnSupplyTrueOrHasBeenReorderedTrue()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private SupplyDto toDto(Supply supply) {
        Felt felt = supply.getFelt();

        return new SupplyDto(
                felt.getId(),
                supply.isLowOnSupply(),
                supply.isHasBeenReordered(),
                felt.getColor(),
                felt.getArticleNumber(),
                felt.getSupplier().getName(),
                felt.getFeltType().getName()
        );
    }
}
