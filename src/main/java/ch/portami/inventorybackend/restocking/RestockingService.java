package ch.portami.inventorybackend.restocking;

import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.FeltVariant;
import ch.portami.inventorybackend.restocking.dto.SupplyDto;
import ch.portami.inventorybackend.restocking.dto.UpdateSupplyDto;
import ch.portami.inventorybackend.restocking.entity.Supply;
import ch.portami.inventorybackend.restocking.repository.SupplyRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class RestockingService {

    private final SupplyRepository supplyRepository;

    public RestockingService(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }

    @Transactional
    public void createForRoll(FeltRoll roll) {
        supplyRepository.save(new Supply(false, false, roll));
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
        FeltRoll roll = supply.getFeltRoll();
        FeltColorVariant colorVariant = roll.getFeltColorVariant();
        FeltVariant variant = colorVariant.getFeltVariant();
        Felt felt = variant.getFelt();

        return new SupplyDto(
                roll.getId(),
                supply.isLowOnSupply(),
                supply.isHasBeenReordered(),
                colorVariant.getId(),
                colorVariant.getColor(),
                felt.getArticleNumber(),
                felt.getSupplier().getName(),
                felt.getFeltType().getName()
        );
    }
}
