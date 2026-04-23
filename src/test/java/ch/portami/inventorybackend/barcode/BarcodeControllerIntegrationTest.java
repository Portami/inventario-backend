package ch.portami.inventorybackend.barcode;

import ch.portami.inventorybackend.barcode.dto.BarcodeLookupDto;
import ch.portami.inventorybackend.barcode.entity.Barcode;
import ch.portami.inventorybackend.barcode.repository.BarcodeRepository;
import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.CreateFeltRollDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.FeltRollDto;
import ch.portami.inventorybackend.felt.entity.FeltColorVariant;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.ScrapPiece;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRollRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.ScrapPieceRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mariadb.MariaDBContainer;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
@ActiveProfiles("test")
class BarcodeControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired private RestTestClient restTestClient;
    @Autowired private BarcodeRepository barcodeRepository;
    @Autowired private BarcodeService barcodeService;
    @Autowired private FeltRollRepository feltRollRepository;
    @Autowired private ScrapPieceRepository scrapPieceRepository;
    @Autowired private FeltColorVariantRepository feltColorVariantRepository;
    @Autowired private FeltTypeRepository feltTypeRepository;
    @Autowired private SupplierRepository supplierRepository;

    private Long feltId;

    @BeforeAll
    void setup() {
        Supplier supplier = supplierRepository.save(new Supplier("Test Supplier"));
        FeltType feltType = feltTypeRepository.save(new FeltType("Wool"));

        FeltDto felt = restTestClient.post().uri("/api/felts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateFeltDto(
                        "Red", "Supplier Red",
                        2.0, 300.0, new BigDecimal("12.50"),
                        "ART-BC-001", supplier.getId(), feltType.getId()
                ))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeltDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(felt).isNotNull();
        feltId = felt.id();
    }

    @BeforeEach
    void reset() {
        barcodeRepository.deleteAll();
        feltRollRepository.deleteAll();
        scrapPieceRepository.deleteAll();
    }

    // --- Fixture helpers ---------------------------------------------------

    private SeededBarcode seedRollBarcode() {
        FeltRollDto roll = restTestClient.post().uri("/api/rolls")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CreateFeltRollDto(feltId, 10.0, 1.5, null, null))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeltRollDto.class)
                .returnResult()
                .getResponseBody();

        assertThat(roll).isNotNull();
        // The roll's creation auto-created exactly one Barcode via FeltRollService.
        Barcode barcode = barcodeRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new AssertionError("Expected a barcode to be created for the roll"));
        return new SeededBarcode(barcode.getId(), roll.id());
    }

    private SeededBarcode seedScrapBarcode() {
        FeltColorVariant variant = feltColorVariantRepository.findById(feltId)
                .orElseThrow(() -> new AssertionError("FeltColorVariant fixture missing"));
        ScrapPiece scrap = scrapPieceRepository.save(
                new ScrapPiece(variant, null, null, 50.0, 50.0));
        Barcode barcode = barcodeService.createForScrap(scrap);
        return new SeededBarcode(barcode.getId(), scrap.getId());
    }

    private record SeededBarcode(Long code, Long linkedEntityId) {}

    // ── GET /api/barcodes/{code} — ROLL ─────────────────────────────────────

    @Nested
    @DisplayName("GET /api/barcodes/{code} — resolves roll barcodes")
    class ResolveRollBarcode {

        @Test
        @DisplayName("returns 200 with type=roll and the FeltRoll id")
        void returnsRollLookup() {
            SeededBarcode seeded = seedRollBarcode();

            restTestClient.get().uri("/api/barcodes/{code}", seeded.code())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(BarcodeLookupDto.class)
                    .value(dto -> {
                        assertThat(dto.type()).isEqualTo("roll");
                        assertThat(dto.id()).isEqualTo(seeded.linkedEntityId());
                    });
        }

        @Test
        @DisplayName("resolves zero-padded code identically to its numeric form")
        void resolvesZeroPaddedCode() {
            SeededBarcode seeded = seedRollBarcode();
            String padded = String.format("%08d", seeded.code());

            restTestClient.get().uri("/api/barcodes/{code}", padded)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(BarcodeLookupDto.class)
                    .value(dto -> {
                        assertThat(dto.type()).isEqualTo("roll");
                        assertThat(dto.id()).isEqualTo(seeded.linkedEntityId());
                    });
        }
    }

    // ── GET /api/barcodes/{code} — SCRAP ────────────────────────────────────

    @Nested
    @DisplayName("GET /api/barcodes/{code} — resolves scrap barcodes")
    class ResolveScrapBarcode {

        @Test
        @DisplayName("returns 200 with type=scrap and the ScrapPiece id")
        void returnsScrapLookup() {
            SeededBarcode seeded = seedScrapBarcode();

            restTestClient.get().uri("/api/barcodes/{code}", seeded.code())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(BarcodeLookupDto.class)
                    .value(dto -> {
                        assertThat(dto.type()).isEqualTo("scrap");
                        assertThat(dto.id()).isEqualTo(seeded.linkedEntityId());
                    });
        }
    }

    // ── GET /api/barcodes/{code} — 404 branch ───────────────────────────────

    @Nested
    @DisplayName("GET /api/barcodes/{code} — not found")
    class NotFound {

        @Test
        @DisplayName("returns 404 when no barcode exists for the code")
        void returns404ForUnknownCode() {
            restTestClient.get().uri("/api/barcodes/{code}", 99999L)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).isEqualTo("Barcode not found");
                    });
        }

        @Test
        @DisplayName("returns 404 after the linked roll has been deleted (cascade)")
        void returns404AfterRollDeleted() {
            SeededBarcode seeded = seedRollBarcode();

            restTestClient.delete().uri("/api/rolls/{id}", seeded.linkedEntityId())
                    .exchange()
                    .expectStatus().isNoContent();

            assertThat(barcodeRepository.existsById(seeded.code()))
                    .as("DB-level ON DELETE CASCADE should drop the barcode row")
                    .isFalse();

            restTestClient.get().uri("/api/barcodes/{code}", seeded.code())
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).isEqualTo("Barcode not found"));
        }
    }

    // ── GET /api/barcodes/{code} — 400 branches ─────────────────────────────

    @Nested
    @DisplayName("GET /api/barcodes/{code} — malformed code")
    class BadRequestFormat {

        @Test
        @DisplayName("rejects zero as non-positive")
        void rejectsZero() {
            expectBadRequest("0", "Barcode must be a positive integer");
        }

        @Test
        @DisplayName("rejects negative values as non-positive")
        void rejectsNegative() {
            expectBadRequest("-1", "Barcode must be a positive integer");
        }

        @Test
        @DisplayName("rejects non-numeric input")
        void rejectsNonNumeric() {
            expectBadRequest("abc", "Barcode must be numeric");
        }

        @Test
        @DisplayName("rejects mixed alphanumeric input")
        void rejectsMixedAlphanumeric() {
            expectBadRequest("1a", "Barcode must be numeric");
        }

        @Test
        @DisplayName("rejects values that overflow a long")
        void rejectsLongOverflow() {
            expectBadRequest("99999999999999999999", "Barcode must be numeric");
        }

        private void expectBadRequest(String code, String expectedMessage) {
            restTestClient.get().uri("/api/barcodes/{code}", code)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(err.message()).isEqualTo(expectedMessage);
                    });
        }
    }
}
