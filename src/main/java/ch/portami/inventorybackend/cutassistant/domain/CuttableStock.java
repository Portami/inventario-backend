package ch.portami.inventorybackend.cutassistant.domain;

public record CuttableStock(StockType stockType, Long feltId,
                            String color, Double length, Double width) {

}
