package ch.portami.inventorybackend.products.dto;

import ch.portami.inventorybackend.products.model.Color;
import ch.portami.inventorybackend.products.model.ProductType;

/**
 * Payload for partially updating a product — all fields are optional.
 */
public record ProductPatchRequest(String name, String articleNumber, ProductType type, Color color, Integer thickness,
                                  Integer density) {
}
