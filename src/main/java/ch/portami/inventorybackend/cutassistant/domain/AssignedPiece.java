package ch.portami.inventorybackend.cutassistant.domain;

/**
 * A single required piece that has been placed onto a stock item.
 *
 * @param requiredFeltId the id of the felt the piece was cut from
 * @param color          the felt color
 * @param length         the piece length
 * @param width          the piece width
 */
public record AssignedPiece(Long requiredFeltId, String color, Double length, Double width) {

}
