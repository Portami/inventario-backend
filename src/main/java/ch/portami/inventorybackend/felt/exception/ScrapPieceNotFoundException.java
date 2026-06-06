package ch.portami.inventorybackend.felt.exception;

import ch.portami.inventorybackend.core.exceptions.ResourceIdentifier;
import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import java.text.MessageFormat;

/**
 * Thrown when a scrap piece is looked up by an id that does not correspond to an existing scrap
 * piece.
 */
public class ScrapPieceNotFoundException extends ResourceNotFoundException {

    public ScrapPieceNotFoundException(long scrapPieceId) {
        super(MessageFormat.format("Scrap piece with id {0} not found", scrapPieceId),
                new ResourceIdentifier("scrapPieceId", scrapPieceId));
    }
}
