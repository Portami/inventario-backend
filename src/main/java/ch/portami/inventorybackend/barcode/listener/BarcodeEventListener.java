package ch.portami.inventorybackend.barcode.listener;

import ch.portami.inventorybackend.barcode.BarcodeService;
import ch.portami.inventorybackend.felt.event.FeltRollCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listens for inventory domain events and creates the corresponding barcodes.
 *
 * <p>Handlers run {@link TransactionPhase#BEFORE_COMMIT} so the barcode is persisted within the same
 * transaction that created the originating entity.
 */
@Component
public class BarcodeEventListener {

    private final BarcodeService barcodeService;

    public BarcodeEventListener(BarcodeService barcodeService) {
        this.barcodeService = barcodeService;
    }

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onRollCreated(FeltRollCreatedEvent event) {
        barcodeService.createForRoll(event.roll());
    }
}
