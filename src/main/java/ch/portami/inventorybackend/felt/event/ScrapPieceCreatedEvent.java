package ch.portami.inventorybackend.felt.event;

import ch.portami.inventorybackend.felt.entity.ScrapPiece;

/**
 * Published when a scrap piece has been created, so that interested components (e.g. barcode
 * generation) can react within the same transaction.
 *
 * @param scrapPiece the newly created scrap piece
 */
public record ScrapPieceCreatedEvent(ScrapPiece scrapPiece) {

}
