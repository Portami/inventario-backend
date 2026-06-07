package ch.portami.inventorybackend.core.bootstrap;

import ch.portami.inventorybackend.felt.FeltRollService;
import ch.portami.inventorybackend.felt.ScrapPieceService;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.CreateScrapPieceDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Seeds demo inventory (felt rolls and scrap pieces) in the {@code dev} profile by calling the real
 * services, so batch identifiers and barcodes are generated through the production code path rather
 * than hand-forged in SQL. Felt colours, products and offer history are seeded separately via the
 * dev-only Flyway migrations.
 *
 * <p>Idempotent: it does nothing if any felt roll already exists, so restarts don't duplicate data.
 */
@Component
@Profile("dev")
public class DevDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DevDataSeeder.class);

    private static final long STORAGE_ATELIER = 1L;
    private static final long STORAGE_KELLER = 2L;

    /** Felt rolls grouped per felt; each felt's rolls share a single generated batch. */
    private static final List<FeltSeed> FELT_SEEDS = List.of(
            new FeltSeed(1, STORAGE_KELLER, new double[][]{{1000, 180}, {180, 100}}),
            new FeltSeed(2, STORAGE_KELLER, new double[][]{{850, 180}, {180, 98}}),
            new FeltSeed(3, STORAGE_KELLER, new double[][]{{1200, 180}, {180, 102}, {180, 97}}),
            new FeltSeed(4, STORAGE_KELLER, new double[][]{{380, 180}, {180, 103}}),
            new FeltSeed(5, STORAGE_KELLER, new double[][]{{900, 180}, {180, 99}}),
            new FeltSeed(6, STORAGE_KELLER, new double[][]{{1350, 180}, {180, 101}, {180, 95}}),
            new FeltSeed(7, STORAGE_KELLER, new double[][]{{600, 180}, {180, 105}}),
            new FeltSeed(8, STORAGE_KELLER, new double[][]{{1480, 180}, {180, 96}}),
            new FeltSeed(9, STORAGE_KELLER, new double[][]{{530, 180}, {180, 104}, {180, 100}}),
            new FeltSeed(10, STORAGE_KELLER, new double[][]{{800, 180}, {180, 98}}),
            new FeltSeed(11, STORAGE_KELLER, new double[][]{{1250, 180}, {180, 103}}),
            new FeltSeed(12, STORAGE_KELLER, new double[][]{{420, 180}, {180, 97}, {180, 102}}),
            new FeltSeed(13, STORAGE_KELLER, new double[][]{{950, 180}, {180, 99}}),
            new FeltSeed(14, STORAGE_KELLER, new double[][]{{1150, 180}, {180, 101}}),
            new FeltSeed(15, STORAGE_KELLER, new double[][]{{670, 180}, {180, 96}, {180, 105}}),
            new FeltSeed(16, STORAGE_KELLER, new double[][]{{1400, 180}, {180, 95}}),
            new FeltSeed(17, STORAGE_KELLER, new double[][]{{350, 180}, {180, 100}}),
            new FeltSeed(18, STORAGE_KELLER, new double[][]{{780, 180}, {180, 98}, {180, 104}}),
            new FeltSeed(19, STORAGE_KELLER, new double[][]{{1050, 180}, {180, 103}}),
            new FeltSeed(20, STORAGE_KELLER, new double[][]{{500, 180}, {180, 97}}),
            new FeltSeed(21, STORAGE_KELLER, new double[][]{{1300, 180}, {180, 102}, {180, 99}}),
            new FeltSeed(22, STORAGE_KELLER, new double[][]{{650, 180}, {180, 101}}),
            new FeltSeed(23, STORAGE_KELLER, new double[][]{{880, 180}, {180, 96}}),
            new FeltSeed(24, STORAGE_KELLER, new double[][]{{1420, 180}, {180, 105}, {180, 95}}),
            new FeltSeed(25, STORAGE_KELLER, new double[][]{{550, 180}, {180, 100}}),
            new FeltSeed(26, STORAGE_KELLER, new double[][]{{740, 180}, {180, 98}}),
            new FeltSeed(27, STORAGE_KELLER, new double[][]{{1180, 180}, {180, 103}, {180, 97}}),
            new FeltSeed(28, STORAGE_KELLER, new double[][]{{400, 180}, {180, 102}}),
            new FeltSeed(29, STORAGE_KELLER, new double[][]{{980, 180}, {180, 99}}),
            new FeltSeed(30, STORAGE_KELLER, new double[][]{{1280, 180}, {180, 101}, {180, 96}}),
            new FeltSeed(31, STORAGE_KELLER, new double[][]{{620, 180}, {180, 105}}),
            new FeltSeed(32, STORAGE_KELLER, new double[][]{{850, 180}, {180, 95}}),
            new FeltSeed(33, STORAGE_KELLER, new double[][]{{700, 180}, {180, 100}, {180, 98}}),
            new FeltSeed(34, STORAGE_KELLER, new double[][]{{1100, 180}, {180, 104}}),
            new FeltSeed(35, STORAGE_ATELIER, new double[][]{{1300, 180}, {180, 100}}));

    private final FeltRollService feltRollService;
    private final ScrapPieceService scrapPieceService;
    private final FeltRollRepository feltRollRepository;

    public DevDataSeeder(FeltRollService feltRollService, ScrapPieceService scrapPieceService,
            FeltRollRepository feltRollRepository) {
        this.feltRollService = feltRollService;
        this.scrapPieceService = scrapPieceService;
        this.feltRollRepository = feltRollRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (feltRollRepository.count() > 0) {
            log.info("Felt rolls already present — skipping demo inventory seeding.");
            return;
        }

        log.info("Seeding demo inventory (felt rolls and scrap pieces) via the services...");
        FELT_SEEDS.forEach(this::seedFeltRolls);
        seedScraps();
        log.info("Demo inventory seeding complete: {} felt rolls created.", feltRollRepository.count());
    }

    /** Creates a felt's rolls, letting the first roll generate a batch the remaining rolls reuse. */
    private void seedFeltRolls(FeltSeed seed) {
        Long batchId = null;
        for (double[] dims : seed.rolls()) {
            FeltRollDto created = feltRollService.create(
                    new CreateFeltRollDto(seed.feltId(), dims[0], dims[1], batchId, seed.storageId()));
            batchId = created.batchId();
        }
    }

    private void seedScraps() {
        scrapPieceService.create(new CreateScrapPieceDto(1L, 57.3, 58.7, null, STORAGE_ATELIER));
        scrapPieceService.create(new CreateScrapPieceDto(35L, 60.3, 46.0, null, STORAGE_ATELIER));
    }

    private record FeltSeed(long feltId, long storageId, double[][] rolls) {
    }
}
