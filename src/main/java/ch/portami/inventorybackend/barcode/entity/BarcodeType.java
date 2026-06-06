package ch.portami.inventorybackend.barcode.entity;

/**
 * The kind of inventory item a barcode is attached to.
 */
public enum BarcodeType {
    /** The barcode identifies a felt roll. */
    ROLL,
    /** The barcode identifies a scrap piece. */
    SCRAP
}
