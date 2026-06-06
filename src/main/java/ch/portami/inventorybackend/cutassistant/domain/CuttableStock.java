package ch.portami.inventorybackend.cutassistant.domain;

/**
 * A piece of available stock the optimizer can cut required pieces from.
 *
 * @param stockType whether the stock comes from a roll or a scrap piece
 * @param feltId    the id of the felt this stock is made of (used to match required pieces)
 * @param color     the felt color
 * @param length    the available length
 * @param width     the available width
 */
public record CuttableStock(StockType stockType, Long feltId,
                            String color, Double length, Double width) {

}
