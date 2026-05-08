package ch.portami.inventorybackend.barcode;

public record BarcodeCode(String value) {

    public long toId() {
        return Long.parseLong(value);
    }
}
