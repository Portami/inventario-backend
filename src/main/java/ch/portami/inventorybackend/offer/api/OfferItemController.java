package ch.portami.inventorybackend.offer.api;

import ch.portami.inventorybackend.offer.OfferService;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemOptionalDto;
import ch.portami.inventorybackend.offer.dto.OfferItemDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Offer Items", description = "Manage items inside offers")
@RestController
@RequestMapping("/api/offers/{id}/items")
public class OfferItemController {

    private final OfferService offerService;

    public OfferItemController(OfferService offerService) {
        this.offerService = offerService;
    }

    @Operation(summary = "Add an item to an existing offer")
    @ApiResponse(responseCode = "201", description = "Offer item created")
    @PostMapping()
    public ResponseEntity<OfferItemDto> addOfferItem(@PathVariable Long id,
            @RequestBody CreateOfferItemOptionalDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(offerService.addOfferItem(id, dto));
    }

    @Operation(summary = "Delete an item from an existing offer")
    @ApiResponse(responseCode = "204", description = "Offer item deleted or not existing")
    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> deleteOfferItem(@PathVariable Long id, @PathVariable Long itemId) {
        offerService.deleteOfferItem(id, itemId);
        return ResponseEntity.noContent()
                             .build();
    }

}
