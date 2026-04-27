package ch.portami.inventorybackend.cutassistant.domain;

public record CuttableStock(StockType stockType, Long feltVariantId, Long feltColorVariantId,
                            String color, Double length, Double width) {

}


