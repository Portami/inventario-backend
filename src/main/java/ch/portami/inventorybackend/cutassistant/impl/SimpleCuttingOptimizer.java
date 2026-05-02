package ch.portami.inventorybackend.cutassistant.impl;

import ch.portami.inventorybackend.cutassistant.CuttingOptimizer;
import ch.portami.inventorybackend.cutassistant.CuttingStockLoader;
import ch.portami.inventorybackend.cutassistant.domain.AssignedPiece;
import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;
import ch.portami.inventorybackend.cutassistant.domain.CuttableStock;
import ch.portami.inventorybackend.cutassistant.domain.CuttingAssignment;
import ch.portami.inventorybackend.cutassistant.domain.RequiredPiece;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * A cutting optimizer that assigns required pieces to available stock to minimize waste.
 *
 * <p>The algorithm works as follows:
 *
 * <ol>
 *   <li><b>Data Preparation:</b>
 *       <ul>
 *         <li>Stock items are loaded and sorted. Scraps are always prioritized over full rolls.
 *             Within each category, smaller stock items come first to ensure a "best-fit" approach,
 *             minimizing leftover waste.
 *         <li>Required pieces are sorted by area in descending order. This heuristic (placing
 *             larger items first) generally leads to more efficient use of stock.
 *       </ul>
 *   <li><b>Assignment Process:</b>
 *       <ul>
 *         <li>The algorithm iterates through each piece and finds the most suitable stock. Due to
 *             the sorting, the first stock that can fit the piece is guaranteed to be the best
 *             choice (the smallest available scrap or, if none, the smallest available roll).
 *         <li>It assumes a simple 1D packing model where pieces are cut along the length of a
 *             stock item. The remaining length of each stock is tracked to allow multiple pieces
 *             to be cut from a single source.
 *       </ul>
 *   <li><b>Result Generation:</b>
 *       <ul>
 *         <li>If all pieces are successfully assigned, the result includes the list of assignments
 *             and the total calculated waste.
 *         <li>If any piece cannot be placed, the optimization is deemed infeasible.
 *       </ul>
 * </ol>
 */
@Service
public class SimpleCuttingOptimizer implements CuttingOptimizer {

    private final CuttingStockLoader stockLoader;

    public SimpleCuttingOptimizer(CuttingStockLoader stockLoader) {
        this.stockLoader = stockLoader;
    }

    /**
     * Executes the cutting optimization process to assign requested pieces to available inventory stock.
     *
     * @param input the requested pieces to be cut
     * @return a {@link CutResult} containing the assignments, total waste, and feasibility status.
     *         If the optimization is not feasible, it will include an infeasible reason.
     */
    @Override
    public CutResult optimize(CutInput input) {
        if (input.requiredPieces().isEmpty()) {
            return new CutResult(Collections.emptyList(), 0.0, true);
        }

        List<CuttableStock> availableStocks = prepareStocks();
        List<RequiredPiece> piecesToCut = preparePieces(input.requiredPieces());

        Map<CuttableStock, List<AssignedPiece>> assignments = new HashMap<>();
        Map<CuttableStock, Double> remainingLengths = initializeRemainingLengths(availableStocks);

        for (RequiredPiece piece : piecesToCut) {
            Optional<CuttableStock> bestStockOptional = findBestStockForPiece(piece, availableStocks, remainingLengths);

            if (bestStockOptional.isEmpty()) {
                String reason = determineInfeasibleReason(piece, availableStocks);
                return new CutResult(Collections.emptyList(), 0.0, false, reason);
            }

            CuttableStock bestStock = bestStockOptional.get();

            assignments.computeIfAbsent(bestStock, k -> new ArrayList<>()).add(
                    new AssignedPiece(piece.feltVariantId(), piece.color(), piece.length(), piece.width())
            );

            remainingLengths.put(bestStock, remainingLengths.get(bestStock) - piece.length());
        }

        return createCutResult(assignments);
    }

    /**
     * Loads available stock items and sorts them according to the best-fit strategy.
     * Scraps are prioritized over full rolls, and smaller items are prioritized within each type.
     *
     * @return a sorted list of available stock items
     */
    private List<CuttableStock> prepareStocks() {
        List<CuttableStock> stocks = new ArrayList<>(stockLoader.loadAll());
        stocks.sort(Comparator.comparing(CuttableStock::stockType).reversed()
                .thenComparingDouble(s -> s.length() * s.width()));
        return stocks;
    }

    /**
     * Flattens requests with quantities into individual pieces and sorts them by area in descending order
     * to facilitate a more efficient packing heuristic.
     *
     * @param requiredPieces the list of requested pieces with quantities
     * @return a flattened and sorted list of individual pieces
     */
    private List<RequiredPiece> preparePieces(List<RequiredPiece> requiredPieces) {
        List<RequiredPiece> flatList = new ArrayList<>();
        for (RequiredPiece piece : requiredPieces) {
            for (int i = 0; i < piece.quantity(); i++) {
                flatList.add(new RequiredPiece(piece.feltVariantId(), piece.color(), piece.length(), piece.width(), 1));
            }
        }
        flatList.sort(Comparator.comparingDouble((RequiredPiece p) -> p.length() * p.width()).reversed());
        return flatList;
    }

    /**
     * Initializes a tracking map to manage the remaining length of each stock item as pieces are assigned.
     *
     * @param availableStocks the list of available stock items
     * @return a map associating each stock item with its current remaining length
     */
    private Map<CuttableStock, Double> initializeRemainingLengths(List<CuttableStock> availableStocks) {
        Map<CuttableStock, Double> remainingLengths = new HashMap<>();
        for (CuttableStock stock : availableStocks) {
            remainingLengths.put(stock, stock.length());
        }
        return remainingLengths;
    }

    /**
     * Finds the most suitable stock item for a given piece based on matching criteria and available dimensions.
     *
     * @param piece the required piece to be assigned
     * @param stocks the sorted list of available stock items
     * @param remainingLengths the map tracking the remaining length of each stock
     * @return an {@link Optional} containing the best matching stock, or empty if no stock can accommodate the piece
     */
    private Optional<CuttableStock> findBestStockForPiece(RequiredPiece piece, List<CuttableStock> stocks, Map<CuttableStock, Double> remainingLengths) {
        return stocks.stream()
                .filter(stock -> matchesVariantAndColor(stock, piece))
                .filter(stock -> canAccommodatePiece(piece, stock, remainingLengths.get(stock)))
                .findFirst();
    }

    /**
     * Analyzes why a specific piece could not be accommodated by any available stock and generates a descriptive message.
     *
     * @param piece the required piece that could not be assigned
     * @param allStocks the list of all available stock items
     * @return a descriptive message explaining the reason for infeasibility
     */
    private String determineInfeasibleReason(RequiredPiece piece, List<CuttableStock> allStocks) {
        boolean hasMatchingVariantAndColor = allStocks.stream()
                .anyMatch(stock -> matchesVariantAndColor(stock, piece));

        if (!hasMatchingVariantAndColor) {
            return String.format("Request for unavailable felt type (ID: %d) or color (%s)",
                    piece.feltVariantId(), piece.color());
        }

        return String.format("Request exceeding all stock dimensions for piece %.1f x %.1f",
                piece.length(), piece.width());
    }

    /**
     * Checks if a stock item matches the variant ID and color required by a piece.
     *
     * @param stock the available stock item
     * @param piece the required piece
     * @return true if the variant ID and color match, false otherwise
     */
    private boolean matchesVariantAndColor(CuttableStock stock, RequiredPiece piece) {
        if (!stock.feltVariantId().equals(piece.feltVariantId())) {
            return false;
        }
        if (piece.color() == null) {
            return true;
        }
        return piece.color().equalsIgnoreCase(stock.color());
    }

    /**
     * Determines if a stock item has sufficient remaining length and adequate width to accommodate a piece,
     * based on a 1D packing model.
     *
     * @param piece the required piece
     * @param stock the available stock item
     * @param remainingLength the current available length of the stock item
     * @return true if the piece fits within the remaining dimensions, false otherwise
     */
    private boolean canAccommodatePiece(RequiredPiece piece, CuttableStock stock, double remainingLength) {
        return remainingLength >= piece.length() && stock.width() >= piece.width();
    }

    /**
     * Compiles the final optimization result, aggregating assignments and calculating total waste.
     *
     * @param assignments a map of stock items to their assigned pieces
     * @return a successful {@link CutResult} containing all assignments and total calculated waste
     */
    private CutResult createCutResult(Map<CuttableStock, List<AssignedPiece>> assignments) {
        List<CuttingAssignment> cuttingAssignments = new ArrayList<>();
        double totalWaste = 0.0;

        for (Map.Entry<CuttableStock, List<AssignedPiece>> entry : assignments.entrySet()) {
            CuttableStock stock = entry.getKey();
            List<AssignedPiece> pieces = entry.getValue();

            double assignmentWaste = calculateWaste(stock, pieces);
            
            cuttingAssignments.add(new CuttingAssignment(stock, pieces, assignmentWaste));
            totalWaste += assignmentWaste;
        }

        return new CutResult(cuttingAssignments, totalWaste, true);
    }

    /**
     * Calculates the waste generated by cutting specific pieces from a stock item.
     * Waste is defined as the unused area within the length slice required for the pieces.
     *
     * @param stock the stock item being cut
     * @param pieces the list of pieces assigned to this stock
     * @return the calculated waste in square units
     */
    private double calculateWaste(CuttableStock stock, List<AssignedPiece> pieces) {
        double piecesTotalArea = pieces.stream().mapToDouble(p -> p.length() * p.width()).sum();

        double lengthUsed = pieces.stream().mapToDouble(AssignedPiece::length).sum();
        double areaUsed = lengthUsed * stock.width();
        
        return areaUsed - piecesTotalArea;
    }
}
