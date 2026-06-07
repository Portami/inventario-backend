package ch.portami.inventorybackend.barcode.listener;

import ch.portami.inventorybackend.barcode.BarcodeService;
import ch.portami.inventorybackend.felt.event.FeltRollCreatedEvent;
import ch.portami.inventorybackend.felt.event.ScrapPieceCreatedEvent;
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

    /**
     * Generates and persists a barcode for a newly created felt roll. Runs before commit so the
     * barcode is written in the same transaction as the roll; if barcode creation fails the roll
     * creation is rolled back too.
     *
     * @param event the published roll-created event carrying the new roll
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onRollCreated(FeltRollCreatedEvent event) {
        barcodeService.createForRoll(event.roll());
    }

    /**
     * Generates and persists a barcode for a newly created scrap piece. Runs before commit so the
     * barcode is written in the same transaction as the scrap piece; if barcode creation fails the
     * scrap piece creation is rolled back too.
     *
     * @param event the published scrap-created event carrying the new scrap piece
     */
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onScrapCreated(ScrapPieceCreatedEvent event) {
        barcodeService.createForScrap(event.scrapPiece());
    }
}
