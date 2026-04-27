package ch.portami.inventorybackend.cutting.domain;

/**
 * A single required piece in a cutting request. The required piece refers to a felt variant (felt type) and a color
 * name. Internally matching will be done against the FeltColorVariant / FeltVariant of available stock.
 */
public record RequiredPiece(Long feltVariantId, String color, Double length, Double width, Integer quantity) {

}

