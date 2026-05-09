package ch.portami.inventorybackend.barcode;

import ch.portami.inventorybackend.barcode.dto.BarcodeLookupDto;
import ch.portami.inventorybackend.barcode.entity.Barcode;
import ch.portami.inventorybackend.barcode.exception.BarcodeNotFoundException;
import ch.portami.inventorybackend.barcode.repository.BarcodeRepository;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class BarcodeService {

    private final BarcodeRepository barcodeRepo;

    public BarcodeService(BarcodeRepository barcodeRepo) {
        this.barcodeRepo = barcodeRepo;
    }

    public BarcodeLookupDto findByCode(long id) {
        Barcode barcode = barcodeRepo.findById(id)
                                     .orElseThrow(() -> new BarcodeNotFoundException(id));
        return toDto(barcode);
    }

    @Transactional
    public Barcode createForRoll(FeltRoll roll) {
        return barcodeRepo.save(Barcode.forRoll(roll));
    }

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
