package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.felt.dto.BarcodeLookupDto;
import ch.portami.inventorybackend.felt.entity.Barcode;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.repository.BarcodeRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class BarcodeService {

    private final BarcodeRepository barcodeRepo;

    public BarcodeService(BarcodeRepository barcodeRepo) {
        this.barcodeRepo = barcodeRepo;
    }

    public BarcodeLookupDto findByCode(String code) {
        long id = parseCode(code);
        Barcode barcode = barcodeRepo.findById(id)
                                     .orElseThrow(() -> new ResponseStatusException(
                                             HttpStatus.NOT_FOUND, "Barcode not found"));
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

    private static long parseCode(String code) {
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode must be a positive integer");
        }
        long id;
        try {
            id = Long.parseLong(code);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode must be numeric");
        }
        if (id <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Barcode must be a positive integer");
        }
        return id;
    }

    private static BarcodeLookupDto toDto(Barcode barcode) {
        return switch (barcode.getType()) {
            case ROLL -> new BarcodeLookupDto("roll", barcode.getFeltRoll().getId());
            case SCRAP -> new BarcodeLookupDto("scrap", barcode.getScrapPiece().getId());
        };
    }
}
