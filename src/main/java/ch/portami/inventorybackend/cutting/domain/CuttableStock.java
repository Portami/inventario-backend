package ch.portami.inventorybackend.cutting.domain;

public record CuttableStock(StockType stockType, Long feltVariantId, Long feltColorVariantId,
                            String color, Double length, Double width) {

}


