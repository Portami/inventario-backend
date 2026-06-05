package ch.portami.inventorybackend.cutting;

import ch.portami.inventorybackend.cutassistant.CuttingStockLoader;
import ch.portami.inventorybackend.cutassistant.config.CuttingProperties;
import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;
import ch.portami.inventorybackend.cutassistant.domain.CuttableStock;
import ch.portami.inventorybackend.cutassistant.domain.CuttingAssignment;
import ch.portami.inventorybackend.cutassistant.domain.RequiredPiece;
import ch.portami.inventorybackend.cutassistant.domain.StockType;
import ch.portami.inventorybackend.cutassistant.impl.GuillotineCuttingOptimizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class GuillotineCuttingOptimizerTest {

    @Mock
    private CuttingStockLoader cuttingStockLoaderMock;

    private GuillotineCuttingOptimizer testee;

    @BeforeEach
    void setUp() {
        testee = new GuillotineCuttingOptimizer(cuttingStockLoaderMock, new CuttingProperties(1.5));
    }

    @Test
    void givenStockAndPiece_whenOptimize_thenAssignsWhenStockMatchesAndOutputsPaddedDimensions() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 100.0, 50.0, 1);
        CuttableStock s = new CuttableStock(StockType.ROLL, 10L, "blue", 200.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(List.of(s));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments()
                              .size());

        CuttingAssignment assignment = result.assignments()
                                             .getFirst();
        assertEquals(1, assignment.pieces()
                                  .size());

        // Verifies the final result gives the customer the piece WITH the 1.5 cm margins (example: Sitzbank allowance)
        assertEquals(103.0, assignment.pieces()
                                      .getFirst()
                                      .length());
        assertEquals(53.0, assignment.pieces()
                                     .getFirst()
                                     .width());

        // Waste is based on total roll area (200x100 = 20,000) minus the padded piece area (103x53 = 5,459)
        // 20,000 - 5,459 = 14,541
        assertEquals(14541.0, assignment.waste());
    }

    @Test
    void givenRollAndScrap_whenOptimize_thenPrioritizesScrapsOverRolls() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 1); // Customer gets: 53 x 53
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, "blue", 200.0, 100.0);
        CuttableStock scrap = new CuttableStock(StockType.SCRAP, 10L, "blue", 60.0, 60.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(new ArrayList<>(Arrays.asList(roll, scrap)));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments()
                              .size());
        assertEquals(StockType.SCRAP, result.assignments()
                                            .getFirst()
                                            .stockItem()
                                            .stockType());
    }

    @Test
    void givenMultiplePieces_whenOptimize_thenPlacesPiecesSideBySideIn2d() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 40.0, 40.0, 4);

        // Stock is 100x100.
        // They form a 2x2 grid taking 86x86 space, which easily fits inside 100x100.
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible(), "Should be feasible due to 2D side-by-side packing");
        assertEquals(1, result.assignments()
                              .size());
        assertEquals(4, result.assignments()
                              .getFirst()
                              .pieces()
                              .size());

        // Verify waste: 100x100 stock = 10,000. Minus four 43x43 pieces (4 * 1849 = 7396). Waste = 2604.
        assertEquals(2604.0, result.assignments()
                                   .getFirst()
                                   .waste());
    }

    @Test
    void givenPiecesWithMargins_whenOptimize_thenRespectsMarginsForMultiplePiecesPreventingFit() {
        // 2 pieces of 48x48. With 1.5 margins, customer needs 51x51 space each.
        RequiredPiece req = new RequiredPiece(10L, "blue", 48.0, 48.0, 2);

        // Stock is 100x100.
        // With margins, 51+51 = 102. They cannot fit side-by-side (102 > 100) or end-to-end.
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible(), "Should be infeasible because combined margins push dimensions over 100");
    }

    @Test
    void givenMultiplePiecesAndMultipleScraps_whenOptimize_thenAssignsMultiplePiecesAcrossMultipleStocks() {
        // Requires three 50x50 pieces (customer gets 53x53).
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 3);

        // Scraps are 60x60, so each can only hold ONE piece.
        CuttableStock scrap1 = new CuttableStock(StockType.SCRAP, 10L, "blue", 60.0, 60.0);
        CuttableStock scrap2 = new CuttableStock(StockType.SCRAP, 10L, "blue", 61.0, 60.0);
        CuttableStock scrap3 = new CuttableStock(StockType.SCRAP, 10L, "blue", 62.0, 60.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(new ArrayList<>(Arrays.asList(scrap1, scrap2, scrap3)));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(3, result.assignments()
                              .size());
        result.assignments()
              .forEach(assignment -> assertEquals(1, assignment.pieces()
                                                               .size()));
    }

    @Test
    void givenLargeAndSmallScrap_whenOptimize_thenMinimizesWasteBySelectingSmallerScrap() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 1); // Customer gets 53 x 53
        CuttableStock largeScrap = new CuttableStock(StockType.SCRAP, 10L, "blue", 150.0, 150.0);
        CuttableStock smallScrap = new CuttableStock(StockType.SCRAP, 10L, "blue", 60.0, 60.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(new ArrayList<>(Arrays.asList(largeScrap, smallScrap)));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments()
                              .size());
        // Since we are checking which one was picked, we verify the dimensions to distinguish them.
        assertEquals(60.0, result.assignments()
                                 .getFirst()
                                 .stockItem()
                                 .length());
        assertEquals(60.0, result.assignments()
                                 .getFirst()
                                 .stockItem()
                                 .width());
    }

    @Test
    void givenNoStock_whenOptimize_thenReportsInfeasible() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 100.0, 50.0, 1);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(List.of());

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertNotNull(result.reason());
        assertTrue(result.reason()
                         .contains("unavailable felt type"));
    }

    @Test
    void givenStockTooSmall_whenOptimize_thenReportsInfeasible() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 150.0, 50.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertTrue(result.reason()
                         .contains("exceeding available space"));
    }

    @Test
    void givenPieceFittingWithoutMarginButNotWith_whenOptimize_thenReportsInfeasibleDueToAddedMargin() {
        // Without margin, this 100x100 piece would fit perfectly into the 100x100 stock.
        // With the 1.5 cm margin on all sides, it becomes 103x103 and should be rejected.
        RequiredPiece req = new RequiredPiece(10L, "blue", 100.0, 100.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertTrue(result.reason()
                         .contains("exceeding available space"));
        assertTrue(result.reason()
                         .contains("with margins"));
    }

    @Test
    void givenWrongColorStock_whenOptimize_thenReportsInfeasible() {
        RequiredPiece req = new RequiredPiece(10L, "red", 50.0, 50.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll())
               .thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertTrue(result.reason()
                         .contains("unavailable felt type"));
    }
}
