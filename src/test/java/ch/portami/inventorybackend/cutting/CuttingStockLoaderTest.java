package ch.portami.inventorybackend.cutting;

import ch.portami.inventorybackend.cutassistant.CuttingStockLoader;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltRoll;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.lang.reflect.Field;
import java.util.List;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CuttingStockLoaderTest {

    @Mock
    private FeltRollRepository feltRollRepositoryMock;

    @Mock
    private ScrapPieceRepository scrapPieceRepositoryMock;

    @InjectMocks
    private CuttingStockLoader testee;

    @Test
    void loadAll_mapsRollsAndScraps() throws Exception {
        Felt felt = new Felt();
        setId(felt, 7L);
        felt.setColor("red");

        FeltRoll roll = new FeltRoll();
        roll.setFelt(felt);
        roll.setLength(200.0);
        roll.setWidth(100.0);
        setId(roll, 11L);

        ScrapPiece scrap = new ScrapPiece();
        scrap.setFelt(felt);
        scrap.setLength(44.0);
        scrap.setWidth(44.0);
        setId(scrap, 22L);

        Mockito.when(feltRollRepositoryMock.findAll()).thenReturn(List.of(roll));
        Mockito.when(scrapPieceRepositoryMock.findAll()).thenReturn(List.of(scrap));

        var stocks = testee.loadAll();

        assertEquals(2, stocks.size());
        var r = stocks.stream().filter(s -> s.stockType().name().equals("ROLL")).findFirst().orElseThrow();
        assertEquals(7L, r.feltId());
        assertEquals("red", r.color());
        assertEquals(200.0, r.length());
    }

    private static void setId(Object target, Long id) throws Exception {
        Field f = null;
        Class<?> c = target.getClass();
        while (c != null) {
            try {
                f = c.getDeclaredField("id");
                break;
            } catch (NoSuchFieldException _) {
                c = c.getSuperclass();
            }
        }
        if (f == null) throw new IllegalStateException("id field not found on " + target.getClass());
        f.setAccessible(true);
        f.set(target, id);
    }
}
