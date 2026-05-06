package ch.portami.inventorybackend.restocking;

import ch.portami.inventorybackend.restocking.repository.SupplyRepository;
import org.springframework.stereotype.Service;

@Service
public class RestockingService {

    private final SupplyRepository supplyRepository;

    public RestockingService(SupplyRepository supplyRepository) {
        this.supplyRepository = supplyRepository;
    }


}
