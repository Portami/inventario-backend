package ch.portami.inventorybackend.cutassistant.api;

import ch.portami.inventorybackend.cutassistant.CutAssistantService;
import ch.portami.inventorybackend.cutassistant.api.dto.AcceptCutProposalDto;
import ch.portami.inventorybackend.cutassistant.api.dto.CutProposalDto;
import ch.portami.inventorybackend.cutassistant.api.dto.RequestCutProposalsDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/cut-assistant")
@Tag(name = "Cut Assistant", description = "Endpoints for waste-optimized cutting proposals.")
public class CutAssistantController {

    private final CutAssistantService cutAssistantService;

    public CutAssistantController(CutAssistantService cutAssistantService) {
        this.cutAssistantService = cutAssistantService;
    }

    @PostMapping("/proposals")
    @Operation(summary = "Request the best cut proposal", description = "Generates the single best-fit cutting proposal for a given set of requested pieces.")
    public ResponseEntity<CutProposalDto> requestBestProposal(
            @Valid @RequestBody RequestCutProposalsDto requestDto) {
        
        Optional<CutProposalDto> proposal = cutAssistantService.requestBestProposal(requestDto);
        
        return proposal.map(ResponseEntity::ok)
                       .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @PostMapping("/offers/{offerId}/proposals/accept")
    @Operation(summary = "Accept a cut proposal", description = "Accepts a specific cut proposal and adds the resulting line items to the given offer.")
    public ResponseEntity<OfferDto> acceptProposal(
            @PathVariable String offerId,
            @Valid @RequestBody AcceptCutProposalDto requestDto) {
        
        OfferDto updatedOffer = cutAssistantService.acceptCutProposal(offerId, requestDto.proposalId());
        return ResponseEntity.ok(updatedOffer);
    }
}
