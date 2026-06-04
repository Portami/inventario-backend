package ch.portami.inventorybackend.stocktake.felt.domain;

import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktake;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeItem;
import ch.portami.inventorybackend.stocktake.felt.entity.FeltStocktakeRollOrScrap;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeItemRepository;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRollOrScrapRepository;
import ch.portami.inventorybackend.storage.entity.Storage;
import jakarta.annotation.Nullable;
import org.springframework.stereotype.Component;

@Component
public class FeltStocktakeRollOrScrapHelper {

    private final FeltStocktakeItemRepository itemRepo;
    private final FeltStocktakeRollOrScrapRepository rollOrScrapRepo;

    public FeltStocktakeRollOrScrapHelper(FeltStocktakeItemRepository itemRepo,
            FeltStocktakeRollOrScrapRepository rollOrScrapRepo) {
        this.itemRepo = itemRepo;
        this.rollOrScrapRepo = rollOrScrapRepo;
    }

    public FeltStocktakeItem createAndSaveItemForRoll(FeltStocktake stocktake, FeltRoll roll,
            @Nullable Storage expectedStorage) {

        FeltStocktakeItem item = new FeltStocktakeItem(stocktake);
        item = itemRepo.save(item);

        Felt felt = roll.getFelt();

        FeltStocktakeRollOrScrap rollOrScrap = new FeltStocktakeRollOrScrap(
                item,
                expectedStorage,
                roll.getLength(),
                roll.getWidth(),
                felt.getColor(),
                felt.getThickness(),
                felt.getDensity(),
                felt.getPrice(),
                felt.getArticleNumber(),
                felt.getFeltType()
                    .getName(),
                felt.getSupplier()
                    .getName(),
                roll
        );

        rollOrScrap = rollOrScrapRepo.save(rollOrScrap);
        item.setRollOrScrap(rollOrScrap);

        return item;

    }

    public FeltStocktakeItem createAndSaveItemForScrap(FeltStocktake stocktake, ScrapPiece scrap,
            @Nullable Storage expectedStorage) {

        FeltStocktakeItem item = new FeltStocktakeItem(stocktake);
        item = itemRepo.save(item);

        Felt felt = scrap.getFelt();

        FeltStocktakeRollOrScrap rollOrScrap = new FeltStocktakeRollOrScrap(
                item,
                expectedStorage,
                scrap.getLength(),
                scrap.getWidth(),
                felt.getColor(),
                felt.getThickness(),
                felt.getDensity(),
                felt.getPrice(),
                felt.getArticleNumber(),
                felt.getFeltType()
                    .getName(),
                felt.getSupplier()
                    .getName(),
                scrap
        );

        rollOrScrap = rollOrScrapRepo.save(rollOrScrap);
        item.setRollOrScrap(rollOrScrap);

        return item;

    }

}
