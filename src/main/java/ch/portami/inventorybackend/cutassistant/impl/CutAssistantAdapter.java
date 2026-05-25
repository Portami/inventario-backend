package ch.portami.inventorybackend.cutassistant.impl;

import ch.portami.inventorybackend.cutassistant.domain.CutProposal;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Adapter to communicate with the external Cut Assistant backend service.
 * This class is responsible for making the actual HTTP calls.
 */
@Component
public class CutAssistantAdapter {

    /**
     * Fetches cut proposals from the external Cut Assistant service.
     *
     * @param feltType The type of felt to be cut.
     * @param requestedPieces A list of pieces to be cut.
     * @return A list of optimized cut proposals.
     */
    public List<CutProposal> fetchCutProposals(String feltType, List<Object> requestedPieces) {
        // TODO: Implement the actual call to the external service.
        // This will likely involve using RestTemplate or WebClient.
        System.out.println("Calling external Cut Assistant for felt type: " + feltType);

        // For now, returning a dummy list.
        return Collections.emptyList();
    }
}
