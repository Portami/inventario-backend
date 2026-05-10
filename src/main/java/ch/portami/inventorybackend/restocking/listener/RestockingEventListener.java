package ch.portami.inventorybackend.restocking.listener;

import ch.portami.inventorybackend.felt.event.FeltColorVariantCreatedEvent;
import ch.portami.inventorybackend.restocking.RestockingService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class RestockingEventListener {

    private final RestockingService restockingService;

    public RestockingEventListener(RestockingService restockingService) {
        this.restockingService = restockingService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onFeltColorVariantCreated(FeltColorVariantCreatedEvent event) {
        restockingService.createForFelt(event.feltColorVariant());
    }
}
