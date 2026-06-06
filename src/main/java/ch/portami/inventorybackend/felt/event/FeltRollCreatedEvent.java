package ch.portami.inventorybackend.felt.event;

import ch.portami.inventorybackend.felt.entity.FeltRoll;

/**
 * Published when a felt roll has been created, so that interested components (e.g. barcode
 * generation) can react within the same transaction.
 *
 * @param roll the newly created felt roll
 */
public record FeltRollCreatedEvent(FeltRoll roll) {

}
