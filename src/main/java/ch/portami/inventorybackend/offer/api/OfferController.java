package ch.portami.inventorybackend.offer.api;

import ch.portami.inventorybackend.offer.OfferService;
import ch.portami.inventorybackend.offer.domain.OfferState;
import ch.portami.inventorybackend.offer.dto.CreateOfferDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Offers", description = "Manage offers")
@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private final OfferService offerService;

    public OfferController(OfferService offerService) {
        this.offerService = offerService;
    }

    @Operation(summary = "Create an offer")
    @ApiResponse(responseCode = "201", description = "Offer successfully created")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @PostMapping
    public ResponseEntity<OfferDto> createOffer(@RequestBody @Valid CreateOfferDto createOfferDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body(offerService.createOffer(createOfferDto));
    }

    @Operation(summary = "Get an offer by ID")
    @ApiResponse(responseCode = "200", description = "Offer found and returned in the response body")
    @ApiResponse(responseCode = "404", description = "No offer exists with the given ID")
    @GetMapping("/{id}")
    public ResponseEntity<OfferDto> getOffer(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @Operation(summary = "List offers")
    @ApiResponse(responseCode = "200", description = "List of all offers by state (may be empty)")
    @GetMapping
    public ResponseEntity<List<OfferDto>> listOffersByState(@RequestParam OfferState state) {
        return ResponseEntity.ok(offerService.listOffers(state));
    }

    @Operation(summary = "Update an offer")
    @ApiResponse(responseCode = "200", description = "Offer successfully updated and returned in the response body")
    @ApiResponse(responseCode = "400", description = "Validation error in the request body")
    @ApiResponse(responseCode = "404", description = "No offer exists with the given ID")
    @PatchMapping("/{id}")
    public ResponseEntity<OfferDto> updateOffer(@PathVariable Long id,
            @RequestBody @Valid UpdateOfferDto updateOfferDto) {
        return ResponseEntity.ok(offerService.updateOffer(id, updateOfferDto));
    }

    @Operation(summary = "Delete an offer")
    @ApiResponse(responseCode = "204", description = "Offer successfully deleted or is not existing (anymore)")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent()
                             .build();
    }

}

