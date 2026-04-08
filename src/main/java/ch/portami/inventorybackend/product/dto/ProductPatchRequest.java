package ch.portami.inventorybackend.product.dto;

import ch.portami.inventorybackend.product.model.Color;
import ch.portami.inventorybackend.product.model.ProductType;

/**
 * Payload for partially updating a product — all fields are optional.
 */
public record ProductPatchRequest(String name, String articleNumber, ProductType type, Color color, Integer thickness,
                                  Integer density) {
}
