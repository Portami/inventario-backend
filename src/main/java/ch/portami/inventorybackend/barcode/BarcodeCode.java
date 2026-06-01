package ch.portami.inventorybackend.barcode;

/**
 * The raw value of a scanned barcode, before it has been resolved to an entity.
 *
 * <p>Wraps the scanned string so it can be validated (see {@code ValidBarcodeCode}) and converted
 * to the numeric id used for lookups.
 *
 * @param value the raw scanned barcode string
 */
public record BarcodeCode(String value) {

    /**
     * Converts the raw barcode value to its numeric id.
     *
     * @return the barcode value parsed as a {@code long}
     * @throws NumberFormatException if the value is not a valid number
     */
    public long toId() {
        return Long.parseLong(value);
    }
}
