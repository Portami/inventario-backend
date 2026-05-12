package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

public class ScrapPieceNotFoundException extends ResourceNotFoundException {

    public ScrapPieceNotFoundException(long scrapPieceId) {
        super(MessageFormat.format("Scrap piece with id {0} not found", scrapPieceId), new ResourceIdentifier("scrapPieceId", scrapPieceId));
    }
}
