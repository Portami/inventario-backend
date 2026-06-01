package ch.portami.inventorybackend.barcode;

import ch.portami.inventorybackend.barcode.dto.BarcodeLookupDto;
import ch.portami.inventorybackend.barcode.entity.Barcode;
import ch.portami.inventorybackend.barcode.exception.BarcodeNotFoundException;
import ch.portami.inventorybackend.barcode.repository.BarcodeRepository;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for creating barcodes for inventory items and resolving scanned barcodes back to the
 * entity they identify.
 */
@Service
@Transactional(readOnly = true)
public class BarcodeService {

    private final BarcodeRepository barcodeRepo;

    public BarcodeService(BarcodeRepository barcodeRepo) {
        this.barcodeRepo = barcodeRepo;
    }

    /**
     * Resolves a scanned barcode to the entity it identifies.
     *
     * @param id the numeric barcode value
     * @return a lookup DTO indicating the kind of entity (roll or scrap piece) and its ID
     * @throws BarcodeNotFoundException if no barcode with the given value exists
     */
    public BarcodeLookupDto findByCode(long id) {
        Barcode barcode = barcodeRepo.findById(id)
                                     .orElseThrow(() -> new BarcodeNotFoundException(id));
        return toDto(barcode);
    }

    /**
     * Creates and persists a barcode for the given felt roll.
     *
     * @param roll the felt roll to create a barcode for
     * @return the persisted barcode
     */
    @Transactional
    public Barcode createForRoll(FeltRoll roll) {
        return barcodeRepo.save(Barcode.forRoll(roll));
    }

    /**
     * Creates and persists a barcode for the given scrap piece.
     *
     * @param scrap the scrap piece to create a barcode for
     * @return the persisted barcode
     */
    @Transactional
    public Barcode createForScrap(ScrapPiece scrap) {
        return barcodeRepo.save(Barcode.forScrap(scrap));
    }

    private static BarcodeLookupDto toDto(Barcode barcode) {
        return switch (barcode.getType()) {
            case ROLL -> new BarcodeLookupDto("roll", barcode.getFeltRoll()
                                                             .getId());
            case SCRAP -> new BarcodeLookupDto("scrap", barcode.getScrapPiece()
                                                               .getId());
        };
    }
}
