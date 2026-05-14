package ch.portami.inventorybackend.cutassistant.impl;

import ch.portami.inventorybackend.cutassistant.CuttingOptimizer;
import ch.portami.inventorybackend.cutassistant.CuttingStockLoader;
import ch.portami.inventorybackend.cutassistant.domain.AssignedPiece;
import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;
import ch.portami.inventorybackend.cutassistant.domain.CuttableStock;
import ch.portami.inventorybackend.cutassistant.domain.CuttingAssignment;
import ch.portami.inventorybackend.cutassistant.domain.RequiredPiece;
import ch.portami.inventorybackend.cutassistant.domain.StockType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * A 2D Guillotine Bin Packing cutting optimizer that assigns required pieces to available stock.
 *
 * <p>The algorithm works as follows:
 *
 * <ol>
 *   <li><b>Data Preparation:</b>
 *       <ul>
 *         <li>Stock items are loaded and explicitly sorted: Scraps first, then by smallest area.
 *         <li>Requests are flattened into individual pieces.
 *         <li>Pieces are sorted by area descending. Placing larger pieces first is mathematically
 *             proven to yield significantly denser 2D packing results (First-Fit Decreasing heuristic).
 *       </ul>
 *   <li><b>2D Assignment Process (Guillotine Split):</b>
 *       <ul>
 *         <li>Each stock item manages a list of "Free Spaces" (initially just one space the size of the stock).
 *         <li>For each piece, a 1.5 cm margin is added to all 4 edges to calculate the "padded" footprint.
 *         <li>The algorithm searches all Free Spaces across all compatible stock items
 *             to find the one that provides the "Best Area Fit" (leaves the smallest remaining area).
 *         <li>When a piece is placed, the chosen Free Space is removed, and the remaining L-shaped
 *             area is cleanly split into two new rectangular Free Spaces using a Guillotine cut.
 *             The cut is made along the shorter axis to maximize the usefulness of the remaining space.
 *       </ul>
 *   <li><b>Result Generation:</b>
 *       <ul>
 *         <li>Returns the assigned pieces with their original (un-padded) dimensions.
 *         <li>Calculates the true waste (Total bounding box area used minus the true area of the pieces).
 *       </ul>
 * </ol>
 */
@Service
public class GuillotineCuttingOptimizer implements CuttingOptimizer {

    private static final Double MARGIN = 1.5;

    private final CuttingStockLoader stockLoader;

    public GuillotineCuttingOptimizer(CuttingStockLoader stockLoader) {
        this.stockLoader = stockLoader;
    }

    /**
     * Executes the 2D cutting optimization process to assign requested pieces to available inventory stock.
     *
     * @param input the requested pieces to be cut
     * @return a {@link CutResult} containing the assignments, calculated waste, and feasibility status.
     *         If infeasible, the result includes a detailed reason.
     */
    @Override
    public CutResult optimize(CutInput input) {
        if (input.requiredPieces().isEmpty()) {
            return new CutResult(Collections.emptyList(), 0.0, true, null);
        }

        List<CuttableStock> availableStocks = prepareStocks();
        List<RequiredPiece> piecesToCut = preparePieces(input.requiredPieces());

        Map<CuttableStock, List<FreeSpace>> stockSpaces = initializeStockSpaces(availableStocks);
        Map<CuttableStock, List<RequiredPiece>> assignments = new HashMap<>();

        for (RequiredPiece piece : piecesToCut) {
            Double paddedLength = piece.length() + (MARGIN * 2);
            Double paddedWidth = piece.width() + (MARGIN * 2);

            Placement bestPlacement = findBestPlacement(piece, paddedLength, paddedWidth, availableStocks, stockSpaces);

            if (bestPlacement == null) {
                String reason = determineInfeasibleReason(piece, paddedLength, paddedWidth, availableStocks);
                return new CutResult(Collections.emptyList(), 0.0, false, reason);
            }

            assignments.computeIfAbsent(bestPlacement.stock, k -> new ArrayList<>()).add(piece);

            splitFreeSpace(bestPlacement.stock, bestPlacement.space, paddedLength, paddedWidth, stockSpaces);
        }

        return createCutResult(assignments);
    }

    /**
     * Loads and sorts available stock items based on business priority and fit heuristics.
     * <p>
     * Scraps are strictly prioritized over rolls to clear out existing inventory. Within a category,
     * items are sorted by area ascending so that the algorithm attempts to use the smallest viable stock first.
     *
     * @return a sorted list of available {@link CuttableStock}
     */
    private List<CuttableStock> prepareStocks() {
        List<CuttableStock> stocks = new ArrayList<>(stockLoader.loadAll());
        stocks.sort(Comparator.comparingInt((CuttableStock s) -> s.stockType() == StockType.SCRAP ? 0 : 1)
                              .thenComparingDouble(s -> s.length() * s.width()));
        return stocks;
    }

    /**
     * Flattens piece requests that have quantities > 1 into distinct individual pieces and sorts them.
     * <p>
     * The list is sorted by area descending. This supports the First-Fit Decreasing (FFD) 2D packing
     * heuristic, ensuring that large, difficult-to-place pieces are accommodated before smaller pieces fill up the gaps.
     *
     * @param requiredPieces the raw input list containing piece types and requested quantities
     * @return a flattened, sorted list of individual {@link RequiredPiece}s
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
     * Initializes the 2D spatial tracking for each stock item.
     * <p>
     * Every stock item begins its lifecycle as a single, contiguous rectangular {@link FreeSpace}
     * matching its exact total length and width.
     *
     * @param availableStocks the list of available stocks to track
     * @return a map linking each stock item to its list of currently available free spaces
     */
    private Map<CuttableStock, List<FreeSpace>> initializeStockSpaces(List<CuttableStock> availableStocks) {
        Map<CuttableStock, List<FreeSpace>> stockSpaces = new HashMap<>();
        for (CuttableStock stock : availableStocks) {
            List<FreeSpace> initialSpace = new ArrayList<>();
            initialSpace.add(new FreeSpace(stock.length(), stock.width()));
            stockSpaces.put(stock, initialSpace);
        }
        return stockSpaces;
    }

    /**
     * Finds the optimal {@link FreeSpace} across all compatible stock items for a given piece.
     * <p>
     * Uses the "Best Area Fit" heuristic: it evaluates all spaces where the piece physically fits,
     * calculates the area of the void that would remain if the piece were placed there, and selects
     * the space that leaves the smallest remaining void. This minimizes loose, unusable gaps.
     *
     * @param piece the required piece to place
     * @param paddedLength the piece's required length, including cutting margins
     * @param paddedWidth the piece's required width, including cutting margins
     * @param stocks the prioritized list of available stocks
     * @param stockSpaces the current map of available free spaces
     * @return a {@link Placement} object containing the chosen stock and space, or {@code null} if no fit exists
     */
    private Placement findBestPlacement(RequiredPiece piece, Double paddedLength, Double paddedWidth,
            List<CuttableStock> stocks, Map<CuttableStock, List<FreeSpace>> stockSpaces) {

        Placement bestPlacement = null;
        Double bestAreaFit = Double.MAX_VALUE;

        for (CuttableStock stock : stocks) {
            if (!matchesVariantAndColor(stock, piece)) continue;

            List<FreeSpace> spaces = stockSpaces.get(stock);
            for (FreeSpace space : spaces) {
                if (space.length >= paddedLength && space.width >= paddedWidth) {
                    Double remainingArea = (space.length * space.width) - (paddedLength * paddedWidth);

                    if (remainingArea < bestAreaFit) {
                        bestAreaFit = remainingArea;
                        bestPlacement = new Placement(stock, space);
                    }
                }
            }
        }

        return bestPlacement;
    }

    /**
     * Executes a Guillotine cut to update the available free spaces after a piece is placed.
     * <p>
     * When a piece is cut from the corner of a rectangular space, it leaves an L-shaped remainder.
     * This method splits that L-shape into two perfectly rectangular new {@link FreeSpace}s.
     * It uses a "Shorter Axis Split" heuristic, extending the cut line along the shorter remaining
     * dimension to preserve the largest, most contiguous rectangular block for future pieces.
     *
     * @param stock the stock item being modified
     * @param usedSpace the original space that the piece was placed into
     * @param pieceLen the padded length of the piece being removed
     * @param pieceWid the padded width of the piece being removed
     * @param stockSpaces the tracking map to be updated with the newly generated free spaces
     */
    private void splitFreeSpace(CuttableStock stock, FreeSpace usedSpace, Double pieceLen, Double pieceWid,
            Map<CuttableStock, List<FreeSpace>> stockSpaces) {

        List<FreeSpace> spaces = stockSpaces.get(stock);
        spaces.remove(usedSpace);

        Double rightLength = usedSpace.length - pieceLen;
        Double topWidth = usedSpace.width - pieceWid;

        if (rightLength > topWidth) {
            if (rightLength > 0) spaces.add(new FreeSpace(rightLength, usedSpace.width));
            if (topWidth > 0) spaces.add(new FreeSpace(pieceLen, topWidth));
        } else {
            if (topWidth > 0) spaces.add(new FreeSpace(usedSpace.length, topWidth));
            if (rightLength > 0) spaces.add(new FreeSpace(rightLength, pieceWid));
        }
    }

    /**
     * Compiles the internal tracking state into domain response objects for the consuming system.
     *
     * @param assignments a map of stocks to their successfully assigned pieces
     * @return a formatted {@link CutResult} containing all assignments and aggregate waste
     */
    private CutResult createCutResult(Map<CuttableStock, List<RequiredPiece>> assignments) {
        List<CuttingAssignment> results = new ArrayList<>();
        Double totalWaste = 0.0;

        for (Map.Entry<CuttableStock, List<RequiredPiece>> entry : assignments.entrySet()) {
            CuttableStock stock = entry.getKey();
            List<RequiredPiece> pieces = entry.getValue();

            // The customer receives the piece WITH the margin included for edge-wrapping/fixing
            List<AssignedPiece> assignedPieces = pieces.stream()
                                                       .map(p -> new AssignedPiece(
                                                               p.feltVariantId(),
                                                               p.color(),
                                                               p.length() + (MARGIN * 2),
                                                               p.width() + (MARGIN * 2)))
                                                       .toList();

            Double stockWaste = calculateTotalWasteForStock(stock, pieces);

            results.add(new CuttingAssignment(stock, assignedPieces, stockWaste));
            totalWaste += stockWaste;
        }

        return new CutResult(results, totalWaste, true, null);
    }

    /**
     * Calculates the raw waste generated on a specific stock item.
     * <p>
     * Waste is strictly defined as the total area of the original stock item minus the
     * total area of the pieces cut from it (including their requested margins).
     * The upstream consuming system is responsible for deciding if this remaining area
     * is large enough to be re-categorized as a usable SCRAP, or if it is true trash.
     *
     * @param stock the original inventory stock item
     * @param pieces the list of pieces assigned to this stock item
     * @return the calculated remaining area (waste) in square units
     */
    private Double calculateTotalWasteForStock(CuttableStock stock, List<RequiredPiece> pieces) {
        Double totalStockArea = stock.length() * stock.width();
        Double usedPaddedArea = pieces.stream()
                                      .mapToDouble(p -> (p.length() + (MARGIN * 2)) * (p.width() + (MARGIN * 2)))
                                      .sum();

        return totalStockArea - usedPaddedArea;
    }

    /**
     * Verifies that a stock item meets the exact felt variant and color requirements of a piece.
     *
     * @param stock the inventory stock item
     * @param piece the requested piece
     * @return true if the variant and color match perfectly, false otherwise
     */
    private boolean matchesVariantAndColor(CuttableStock stock, RequiredPiece piece) {
        if (!stock.feltVariantId().equals(piece.feltVariantId())) return false;
        if (piece.color() == null) return true;
        return piece.color().equalsIgnoreCase(stock.color());
    }

    /**
     * Determines the specific reason a piece could not be placed, prioritizing stock mismatch
     * over dimension constraints for clearer user feedback.
     *
     * @param piece the piece that failed to place
     * @param paddedLength the piece's required length with margins
     * @param paddedWidth the piece's required width with margins
     * @param stocks the full list of available stock items
     * @return a human-readable string explaining the failure reason
     */
    private String determineInfeasibleReason(RequiredPiece piece, Double paddedLength, Double paddedWidth, List<CuttableStock> stocks) {
        boolean hasMatchingVariantAndColor = stocks.stream()
                                                   .anyMatch(stock -> matchesVariantAndColor(stock, piece));

        if (!hasMatchingVariantAndColor) {
            return String.format("Request for unavailable felt type (ID: %d) or color (%s)",
                    piece.feltVariantId(), piece.color());
        }

        return String.format("Request exceeding available space for piece %.1f x %.1f (requires %.1f x %.1f with margins)",
                piece.length(), piece.width(), paddedLength, paddedWidth);
    }

    // --- Helper Domain Classes for 2D Geometry ---

    /**
     * Represents a continuous, unassigned rectangular area on a specific stock item.
     * Used by the 2D Guillotine algorithm to track available space.
     */
    private static class FreeSpace {
        final Double length;
        final Double width;

        /**
         * Creates a new FreeSpace instance.
         *
         * @param length the length of the available rectangle
         * @param width the width of the available rectangle
         */
        FreeSpace(Double length, Double width) {
            this.length = length;
            this.width = width;
        }
    }

    /**
     * Represents a successful match between a required piece and an available location.
     */
    private static class Placement {
        final CuttableStock stock;
        final FreeSpace space;

        /**
         * Creates a new Placement instance.
         *
         * @param stock the stock item the piece will be cut from
         * @param space the specific free space on that stock item the piece will occupy
         */
        Placement(CuttableStock stock, FreeSpace space) {
            this.stock = stock;
            this.space = space;
        }
    }
}