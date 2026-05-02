package ch.portami.inventorybackend.cutting;

import ch.portami.inventorybackend.cutassistant.CuttingStockLoader;
import ch.portami.inventorybackend.cutassistant.domain.AssignedPiece;
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
    void optimizer_assigns_when_stock_matches() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 100.0, 50.0, 1);
        CuttableStock s = new CuttableStock(StockType.ROLL, 10L, "blue", 200.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(s));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments().size());

        CuttingAssignment assignment = result.assignments().getFirst();
        assertEquals(1, assignment.pieces().size());
        assertTrue(assignment.waste() > 0);
    }

    @Test
    void optimizer_reports_infeasible_when_no_stock() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 100.0, 50.0, 1);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of());

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertNotNull(result.infeasibleReason());
    }

    @Test
    void optimizer_prioritizes_scraps_over_rolls() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 200.0, 100.0);
        CuttableStock scrap = new CuttableStock(StockType.SCRAP, 10L, 101L, "blue", 60.0, 60.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(new ArrayList<>(Arrays.asList(roll, scrap)));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments().size());
        assertEquals(StockType.SCRAP, result.assignments().getFirst().stockItem().stockType());
    }

    @Test
    void optimizer_assigns_multiple_pieces_to_same_stock() {
        RequiredPiece req1 = new RequiredPiece(10L, "blue", 50.0, 50.0, 1);
        RequiredPiece req2 = new RequiredPiece(10L, "blue", 40.0, 50.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req1, req2)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments().size());
        assertEquals(2, result.assignments().getFirst().pieces().size());
    }

    @Test
    void optimizer_minimizes_waste_by_selecting_smaller_scrap() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 1);
        CuttableStock largeScrap = new CuttableStock(StockType.SCRAP, 10L, 100L, "blue", 150.0, 150.0);
        CuttableStock smallScrap = new CuttableStock(StockType.SCRAP, 10L, 101L, "blue", 60.0, 60.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(new ArrayList<>(Arrays.asList(largeScrap, smallScrap)));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments().size());
        assertEquals(smallScrap.feltColorVariantId(), result.assignments().getFirst().stockItem().feltColorVariantId());
    }

    @Test
    void optimizer_handles_quantities_correctly() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 50.0, 50.0, 3);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 200.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertTrue(result.feasible());
        assertEquals(1, result.assignments().size());
        assertEquals(3, result.assignments().getFirst().pieces().size());
    }

    @Test
    void optimizer_reports_infeasible_when_stock_too_small() {
        RequiredPiece req = new RequiredPiece(10L, "blue", 150.0, 50.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertTrue(result.infeasibleReason().contains("exceeding"));
    }

    @Test
    void optimizer_reports_infeasible_when_wrong_color() {
        RequiredPiece req = new RequiredPiece(10L, "red", 50.0, 50.0, 1);
        CuttableStock roll = new CuttableStock(StockType.ROLL, 10L, 100L, "blue", 100.0, 100.0);

        Mockito.when(cuttingStockLoaderMock.loadAll()).thenReturn(List.of(roll));

        CutResult result = testee.optimize(new CutInput(List.of(req)));

        assertFalse(result.feasible());
        assertTrue(result.infeasibleReason().contains("unavailable felt type"));
    }
}
