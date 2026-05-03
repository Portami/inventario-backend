package ch.portami.inventorybackend.cutting;

import ch.portami.inventorybackend.cutassistant.CuttingStockLoader;
import ch.portami.inventorybackend.cutassistant.domain.CutInput;
import ch.portami.inventorybackend.cutassistant.domain.CutResult;
import ch.portami.inventorybackend.cutassistant.domain.CuttableStock;
import ch.portami.inventorybackend.cutassistant.domain.CuttingAssignment;
import ch.portami.inventorybackend.cutassistant.domain.RequiredPiece;
import ch.portami.inventorybackend.cutassistant.domain.StockType;
import ch.portami.inventorybackend.cutassistant.impl.SimpleCuttingOptimizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SimpleCuttingOptimizerTest {

    @Mock
    private CuttingStockLoader cuttingStockLoaderMock;

    @InjectMocks
    private SimpleCuttingOptimizer testee;

    @Test
    void optimizer_assigns_when_stock_matches_and_outputs_padded_dimensions() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 100.0, 50.0, 1);
        CuttableStock s = new CuttableStock(StockType.ROLL, 10L, "blue", 200.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(s));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments().size());

        CuttingAssignment assignment = result.assignments().getFirst();
        assertEquals(1, assignment.pieces().size());

        // Verifies the final result gives the customer the piece WITH the 1.5 cm margins (example: Sitzbank allowance)
        assertEquals(103.0, assignment.pieces().getFirst().length());
        assertEquals(53.0, assignment.pieces().getFirst().width());

        // Waste is based on total roll area (200x100 = 20,000) minus the padded piece area (103x53 = 5,459)
        // 20,000 - 5,459 = 14,541
        assertEquals(14541.0, assignment.waste());
    }

    @Test
    void optimizer_prioritizes_scraps_over_rolls() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 1); // Customer gets: 53 x 53
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 200.0, 100.0);
        CuttableStock scrap = new CuttableStock(StockType.SCRAP, 10L, 101L, "blue", 60.0, 60.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(new ArrayList<>(Arrays.asList(roll, scrap)));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments().size());
        assertEquals(StockType.SCRAP, result.assignments().getFirst().stockItem().stockType());
    }

    @Test
    void optimizer_places_pieces_side_by_side_in_2d() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 40.0, 40.0, 4);

        // Stock is 100x100.
        // They form a 2x2 grid taking 86x86 space, which easily fits inside 100x100.
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible(), "Should be feasible due to 2D side-by-side packing");
        assertEquals(1, result.assignments().size());
        assertEquals(4, result.assignments().getFirst().pieces().size());

        // Verify waste: 100x100 stock = 10,000. Minus four 43x43 pieces (4 * 1849 = 7396). Waste = 2604.
        assertEquals(2604.0, result.assignments().getFirst().waste());
    }

    @Test
    void optimizer_respects_margins_for_multiple_pieces_preventing_fit() {
        // 2 pieces of 48x48. With 1.5 margins, customer needs 51x51 space each.
        RequiredPiece req = new RequiredPiece(10L, "blue", 48.0, 48.0, 2);

        // Stock is 100x100.
        // With margins, 51+51 = 102. They cannot fit side-by-side (102 > 100) or end-to-end.
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible(), "Should be infeasible because combined margins push dimensions over 100");
    }

    @Test
    void optimizer_assigns_multiple_pieces_across_multiple_stocks() {
        // Requires three 50x50 pieces (customer gets 53x53).
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 3);

        // Scraps are 60x60, so each can only hold ONE piece.
        CuttableStock scrap1 = new CuttableStock(StockType.SCRAP, 10L, 100L, "blue", 60.0, 60.0);
        CuttableStock scrap2 = new CuttableStock(StockType.SCRAP, 10L, 101L, "blue", 60.0, 60.0);
        CuttableStock scrap3 = new CuttableStock(StockType.SCRAP, 10L, 102L, "blue", 60.0, 60.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(new ArrayList<>(Arrays.asList(scrap1, scrap2, scrap3)));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(3, result.assignments().size());
        result.assignments().forEach(assignment -> assertEquals(1, assignment.pieces().size()));
    }

    @Test
    void optimizer_minimizes_waste_by_selecting_smaller_scrap() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 1); // Customer gets 53 x 53
        CuttableStock largeScrap = new CuttableStock(StockType.SCRAP, 10L, 100L, "blue", 150.0, 150.0);
        CuttableStock smallScrap = new CuttableStock(StockType.SCRAP, 10L, 101L, "blue", 60.0, 60.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(new ArrayList<>(Arrays.asList(largeScrap, smallScrap)));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments().size());
        assertEquals(smallScrap.feltColorVariantId(), result.assignments().getFirst().stockItem().feltColorVariantId());
    }

    @Test
    void optimizer_reports_infeasible_when_no_stock() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 100.0, 50.0, 1);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of());

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertNotNull(result.reason());
        assertTrue(result.reason().contains("unavailable felt type"));
    }

    @Test
    void optimizer_reports_infeasible_when_stock_too_small() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 150.0, 50.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertTrue(result.reason().contains("exceeding available space"));
    }

    @Test
    void optimizer_reports_infeasible_due_to_added_margin() {
        // Without margin, this 100x100 piece would fit perfectly into the 100x100 stock.
        // With the 1.5 cm margin on all sides, it becomes 103x103 and should be rejected.
        RequiredPiece req = new RequiredPiece(10L, "blue", 100.0, 100.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertTrue(result.reason().contains("exceeding available space"));
        assertTrue(result.reason().contains("with margins"));
    }

    @Test
    void optimizer_reports_infeasible_when_wrong_color() {
        RequiredPiece req = new RequiredPiece(10L, "red", 50.0, 50.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertTrue(result.reason().contains("unavailable felt type"));
    }
}