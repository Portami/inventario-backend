package ch.portami.inventorybackend.barcode.listener;

import ch.portami.inventorybackend.barcode.BarcodeService;
import ch.portami.inventorybackend.felt.event.FeltRollCreatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

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
