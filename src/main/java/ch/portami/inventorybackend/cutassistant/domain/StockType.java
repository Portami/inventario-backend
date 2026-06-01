package ch.portami.inventorybackend.cutassistant.domain;

/**
 * The kind of stock a {@link CuttableStock} item originates from.
 */
public enum StockType {
    /** Stock taken from a full felt roll. */
    ROLL,
    /** Stock taken from a leftover scrap piece. */
    SCRAP
}
