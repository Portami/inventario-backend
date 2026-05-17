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
    }
}
