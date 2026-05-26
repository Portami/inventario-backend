package ch.portami.inventorybackend.stocktake.felt.entity;

import java.io.Serializable;

public record FeltStocktakeStorageId(Long stocktake, Long storage) implements Serializable {

}

