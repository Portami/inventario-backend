package ch.portami.inventorybackend.cutassistant;

import ch.portami.inventorybackend.cutassistant.api.dto.CutProposalDto;
import ch.portami.inventorybackend.cutassistant.api.dto.RequestCutProposalsDto;
import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;
import ch.portami.inventorybackend.cutassistant.domain.RequiredPiece;
import ch.portami.inventorybackend.cutassistant.impl.GuillotineCuttingOptimizer;
import ch.portami.inventorybackend.offer.OfferService;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CutAssistantService {

    private final GuillotineCuttingOptimizer optimizer;
    private final OfferService offerService;

    private final Map<String, CutProposalDto> proposalCache = new ConcurrentHashMap<>();
    private static final Long DUMMY_FELT_ID = 1L;
    // TODO
    // Placeholder for a generic "custom cut" felt variant ID from the database.
    private static final Long CUSTOM_CUT_FELT_VARIANT_ID = 999L;
    // TODO
    // Placeholder for price per square meter for the felt type.
    private static final BigDecimal PRICE_PER_SQ_METER = new BigDecimal("50.00");

    public CutAssistantService(GuillotineCuttingOptimizer optimizer, OfferService offerService) {
        this.optimizer = optimizer;
        this.offerService = offerService;
    }

    public Optional<CutProposalDto> requestBestProposal(RequestCutProposalsDto dto) {
        List<RequiredPiece> requiredPieces = dto.requestedPieces().stream()
                .map(p -> new RequiredPiece(
                        DUMMY_FELT_ID,
                        dto.feltType(),
                        p.width(),
                        p.height(),
                        p.quantity()))
                .collect(Collectors.toList());

        CutInput cutInput = new CutInput(requiredPieces);
        CutResult cutResult = optimizer.optimize(cutInput);

        if (cutResult.feasible()) {
            List<CutProposalDto.ProposedCutDto> proposedCuts = cutResult.assignments().stream()
                    .flatMap(assignment -> assignment.pieces().stream()
                            .map(piece -> new CutProposalDto.ProposedCutDto(
                                    piece.length(),
                                    piece.width(),
                                    UUID.randomUUID().toString() // TODO: Replace with real stock ID
                            )))
                    .collect(Collectors.toList());

            String proposalId = UUID.randomUUID().toString();
            CutProposalDto proposal = new CutProposalDto(
                    proposalId,
                    proposedCuts,
                    cutResult.totalWaste()
            );
            proposalCache.put(proposalId, proposal);
            return Optional.of(proposal);
        }
        return Optional.empty();
    }

    public OfferDto acceptCutProposal(String offerId, String proposalId) {
        CutProposalDto proposal = proposalCache.get(proposalId);
        if (proposal == null) {
            throw new IllegalArgumentException("Proposal not found or expired.");
        }

        // 1. Convert each ProposedCutDto into a CreateOfferItemDto.
        List<CreateOfferItemDto> newItems = proposal.proposedCuts().stream()
                .map(cut -> {
                    BigDecimal area = BigDecimal.valueOf(cut.width() * cut.height() / 10000.0);
                    BigDecimal price = area.multiply(PRICE_PER_SQ_METER);
                    String description = String.format("Zuschnitt %.1f x %.1f cm", cut.width(), cut.height());

                    // Using feltVariantId as requested.
                    return new CreateOfferItemDto(
                            CUSTOM_CUT_FELT_VARIANT_ID,
                            description,
                            1,
                            price
                    );
                })
                .collect(Collectors.toList());

        // 2. Call OfferService to add these items to the offer.
        offerService.addItemsToOffer(Long.parseLong(offerId), newItems);

        // 3. TODO: Trigger reservation/tagging of the source stock.

        proposalCache.remove(proposalId);

        // 4. Return the updated offer.
        return offerService.getOfferById(Long.parseLong(offerId));
    }
}
