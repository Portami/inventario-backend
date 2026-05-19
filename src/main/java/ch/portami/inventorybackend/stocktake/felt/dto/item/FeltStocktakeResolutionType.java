package ch.portami.inventorybackend.stocktake.felt.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description =
        "The type of resolution to apply to a stocktake item problem. Only certain resolutions are valid for certain types of problems. The possible resolution types and when they may be applied is as follows:<br/><br/> "
                + " - `ADJUST_STORAGE`: Adjust the storage location of the item to match the storage location where it was found during the stocktake. Only applicable for items with status `WRONG_STORAGE`.<br/><br/> "
                + " - `MOVE_PHYSICALLY`: Physically move the item to the correct storage location. The location of the item in the system is correct and will not change. Only applicable for items with status `WRONG_STORAGE`.<br/><br/> "
                + " - `IGNORE_MISSING`: Ignore the fact that the item is missing and do not apply any inventory mutation for this item. Only applicable for items with status `MISSING`.<br/><br/> "
                + " - `REMOVE_MISSING`: Remove the item from the system once the stocktake is completed, as it is considered to be no longer in inventory. Only applicable for items with status `MISSING`.<br/><br/> "
                + " - `ACKNOWLEDGE`: Acknowledge the problem. No decision can be made within the stocktake process. Only applicable for items with status `NOT_IN_STOCKTAKE` or `UNKNOWN`.<br/>")
public enum FeltStocktakeResolutionType {
    ADJUST_STORAGE,
    MOVE_PHYSICALLY,
    IGNORE_MISSING,
    REMOVE_MISSING,
    ACKNOWLEDGE,
}

