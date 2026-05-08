package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.FeltColorVariantRepository;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import ch.portami.inventorybackend.felt.repository.FeltVariantRepository;
import ch.portami.inventorybackend.felt.repository.SupplierRepository;
import java.math.BigDecimal;
import java.util.List;
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
import org.springframework.core.ParameterizedTypeReference;
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
class FeltControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final ParameterizedTypeReference<List<FeltDto>> FELT_LIST =
            new ParameterizedTypeReference<>() {};

    @Autowired private RestTestClient restTestClient;
    @Autowired private FeltColorVariantRepository feltColorVariantRepository;
    @Autowired private FeltVariantRepository feltVariantRepository;
    @Autowired private FeltRepository feltRepository;
    @Autowired private FeltTypeRepository feltTypeRepository;
    @Autowired private SupplierRepository supplierRepository;

    private Long supplierId;
    private Long supplierId2;
    private Long feltTypeId;
    private Long feltTypeId2;

    @BeforeAll
    void setupFixtures() {
        supplierId  = supplierRepository.save(new Supplier("Test Supplier")).getId();
        supplierId2 = supplierRepository.save(new Supplier("Test Supplier 2")).getId();
        feltTypeId  = feltTypeRepository.save(new FeltType("Wool")).getId();
        feltTypeId2 = feltTypeRepository.save(new FeltType("Polyester")).getId();
    }

    @BeforeEach
    void resetFelts() {
        feltColorVariantRepository.deleteAll();
        feltVariantRepository.deleteAll();
        feltRepository.deleteAll();
    }

    private CreateFeltDto validCreate() {
        return new CreateFeltDto(
                "Red", "Supplier Red",
                2.0, 300.0, new BigDecimal("12.50"),
                "ART-001", supplierId, feltTypeId
        );
    }

    private FeltDto postFelt(CreateFeltDto dto) {
        FeltDto body = restTestClient.post().uri("/api/felts")
                .contentType(MediaType.APPLICATION_JSON)
                .body(dto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(FeltDto.class)
                .returnResult()
                .getResponseBody();
        assertThat(body).isNotNull();
        return body;
    }

    // ── GET /api/felts ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/felts")
    class GetAllFelts {

        @Test
        @DisplayName("returns empty list when no felts exist")
        void returnsEmptyList() {
            restTestClient.get().uri("/api/felts")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FELT_LIST)
                    .value(felts -> assertThat(felts).isEmpty());
        }

        @Test
        @DisplayName("returns all created felts")
        void returnsAllFelts() {
            postFelt(validCreate());
            postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    3.0, 400.0, new BigDecimal("15.00"),
                    "ART-002", supplierId, feltTypeId2
            ));

            restTestClient.get().uri("/api/felts")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FELT_LIST)
                    .value(felts -> assertThat(felts).hasSize(2));
        }
    }

    // ── GET /api/felts/{id} ─────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/felts/{id}")
    class GetFeltById {

        @Test
        @DisplayName("returns felt when it exists")
        void returnsExistingFelt() {
            FeltDto created = postFelt(validCreate());

            restTestClient.get().uri("/api/felts/{id}", created.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.id()).isEqualTo(created.id());
                        assertThat(felt.color()).isEqualTo("Red");
                        assertThat(felt.feltTypeName()).isEqualTo("Wool");
                        assertThat(felt.supplierId()).isEqualTo(supplierId);
                    });
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.get().uri("/api/felts/{id}", 99999)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    // ── POST /api/felts ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/felts")
    class CreateFelt {

        @Test
        @DisplayName("creates felt and returns 201 with full body and Location header")
        void createsFelt() {
            var response = restTestClient.post().uri("/api/felts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(validCreate())
                    .exchange();

            response.expectStatus().isCreated()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.id()).isNotNull();
                        assertThat(felt.color()).isEqualTo("Red");
                        assertThat(felt.supplierColor()).isEqualTo("Supplier Red");
                        assertThat(felt.thickness()).isEqualTo(2.0);
                        assertThat(felt.density()).isEqualTo(300.0);
                        assertThat(felt.price()).isEqualByComparingTo(new BigDecimal("12.50"));
                        assertThat(felt.articleNumber()).isEqualTo("ART-001");
                        assertThat(felt.supplierId()).isEqualTo(supplierId);
                        assertThat(felt.feltTypeName()).isEqualTo("Wool");
                    });

            var location = response.returnResult(FeltDto.class).getResponseHeaders().getLocation();
            assertThat(location).isNotNull();
            assertThat(location.toString()).contains("/api/felts/");
        }

        @Test
        @DisplayName("reuses existing FeltType when feltTypeId matches")
        void reusesExistingFeltType() {
            FeltDto first = postFelt(validCreate());
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    3.0, 400.0, new BigDecimal("15.00"),
                    "ART-002", supplierId, feltTypeId   // same feltTypeId
            ));

            assertThat(first.feltTypeId()).isEqualTo(second.feltTypeId());
        }

        @Test
        @DisplayName("reuses existing Felt and FeltVariant when (type, supplier, articleNumber, specs) match")
        void reusesExistingFeltAndVariant() {
            FeltDto first  = postFelt(validCreate());
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            ));
            FeltDto third  = postFelt(new CreateFeltDto(
                    "Green", "Supplier Green",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            ));

            assertThat(first.feltId()).isEqualTo(second.feltId()).isEqualTo(third.feltId());
            assertThat(first.feltVariantId()).isEqualTo(second.feltVariantId()).isEqualTo(third.feltVariantId());
        }

        @Test
        @DisplayName("reuses Felt but creates separate FeltVariant when specs differ")
        void reusesExistingFeltButCreatesNewVariantForDifferentSpecs() {
            FeltDto first = postFelt(validCreate()); // thickness=2.0
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    5.0, 300.0, new BigDecimal("12.50"), // different thickness
                    "ART-001", supplierId, feltTypeId    // same Felt identity
            ));

            assertThat(first.feltId()).isEqualTo(second.feltId());
            assertThat(first.feltVariantId()).isNotEqualTo(second.feltVariantId());
        }

        @Test
        @DisplayName("creates separate Felt when same articleNumber belongs to a different supplier")
        void doesNotReuseFeltAcrossSuppliers() {
            FeltDto first  = postFelt(validCreate()); // supplierId, ART-001
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId2, feltTypeId  // supplierId2, same articleNumber
            ));

            assertThat(first.feltId()).isNotEqualTo(second.feltId());
            assertThat(first.supplierId()).isNotEqualTo(second.supplierId());
        }

        @Test
        @DisplayName("returns 400 when color is blank")
        void rejectsBlankColor() {
            var invalid = new CreateFeltDto(
                    "", "Supplier Red",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            );

            restTestClient.post().uri("/api/felts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("color"));
        }

        @Test
        @DisplayName("returns 422 when feltTypeId does not exist")
        void returns422ForUnknownFeltType() {
            var dto = new CreateFeltDto(
                    "Red", "Supplier Red",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, 99999L
            );

            restTestClient.post().uri("/api/felts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value()));
        }

        @Test
        @DisplayName("returns 422 when supplier does not exist")
        void returns422ForUnknownSupplier() {
            var dto = new CreateFeltDto(
                    "Red", "Supplier Red",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", 99999L, feltTypeId
            );

            restTestClient.post().uri("/api/felts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value()));
        }
    }

    // ── PATCH /api/felts/{id} ───────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /api/felts/{id}")
    class UpdateFelt {

        @Test
        @DisplayName("updates color only — no cascade to variant or felt")
        void updatesColorOnly() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch().uri("/api/felts/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto("Purple", null, null, null, null, null, null, null))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.color()).isEqualTo("Purple");
                        assertThat(felt.feltVariantId()).isEqualTo(created.feltVariantId());
                        assertThat(felt.feltId()).isEqualTo(created.feltId());
                    });
        }

        @Test
        @DisplayName("updates supplier — mutates Felt in-place when no collision (same feltId)")
        void updatesSupplier() {
            FeltDto created = postFelt(validCreate()); // supplierId

            restTestClient.patch().uri("/api/felts/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, null, null, null, null, supplierId2, null))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.supplierId()).isEqualTo(supplierId2);
                        // No pre-existing Felt(Wool, supplier2, ART-001) → in-place mutation, same row
                        assertThat(felt.feltId()).isEqualTo(created.feltId());
                    });
        }

        @Test
        @DisplayName("mutates Felt in-place when both Felt and Variant are exclusive (no new feltId)")
        void mutatesFeltInPlaceWhenExclusive() {
            FeltDto created = postFelt(validCreate()); // exclusive chain

            restTestClient.patch().uri("/api/felts/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, null, null, null, null, null, feltTypeId2))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeName()).isEqualTo("Polyester");
                        // Felt was mutated in-place — the feltId must not change
                        assertThat(felt.feltId()).isEqualTo(created.feltId());
                    });
        }

        @Test
        @DisplayName("re-points to a new FeltVariant when thickness changes and variant is shared")
        void rePointsVariantWhenShared() {
            FeltDto first = postFelt(validCreate());
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            ));
            assertThat(first.feltVariantId()).isEqualTo(second.feltVariantId());

            restTestClient.patch().uri("/api/felts/{id}", first.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, 5.0, null, null, null, null, null))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.thickness()).isEqualTo(5.0);
                        assertThat(felt.feltVariantId()).isNotEqualTo(second.feltVariantId());
                    });

            // second must still point to its original variant
            restTestClient.get().uri("/api/felts/{id}", second.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> assertThat(felt.feltVariantId()).isEqualTo(second.feltVariantId()));
        }

        @Test
        @DisplayName("re-points variant when Felt changes and variant is shared")
        void rePointsVariantWhenFeltChangesAndShared() {
            FeltDto first = postFelt(validCreate());
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            ));
            assertThat(first.feltVariantId()).isEqualTo(second.feltVariantId());

            // Change only the felt type on first — variant is shared, so it gets re-pointed
            restTestClient.patch().uri("/api/felts/{id}", first.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, null, null, null, null, null, feltTypeId2))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeName()).isEqualTo("Polyester");
                        assertThat(felt.feltVariantId()).isNotEqualTo(second.feltVariantId());
                    });

            // second must still have the original type and variant
            restTestClient.get().uri("/api/felts/{id}", second.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeName()).isEqualTo("Wool");
                        assertThat(felt.feltVariantId()).isEqualTo(second.feltVariantId());
                    });
        }

        @Test
        @DisplayName("re-points variant when Felt is shared by a sibling FeltVariant")
        void rePointsWhenFeltIsSharedByAnotherVariant() {
            // Two color variants share the same Felt but have different specs (→ different FeltVariants)
            FeltDto first = postFelt(validCreate()); // specs: 2.0/300.0/12.50
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    5.0, 400.0, new BigDecimal("20.00"), // different specs → different FeltVariant
                    "ART-001", supplierId, feltTypeId    // same Felt
            ));
            assertThat(first.feltId()).isEqualTo(second.feltId()); // confirm shared Felt
            assertThat(first.feltVariantId()).isNotEqualTo(second.feltVariantId());

            // Change the type on first — Felt is shared, so must find/create a new Felt (not mutate in-place)
            restTestClient.patch().uri("/api/felts/{id}", first.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, null, null, null, null, null, feltTypeId2))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeName()).isEqualTo("Polyester");
                        assertThat(felt.feltId()).isNotEqualTo(first.feltId()); // different Felt now
                    });

            // second must still have the original type
            restTestClient.get().uri("/api/felts/{id}", second.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> assertThat(felt.feltTypeName()).isEqualTo("Wool"));
        }

        @Test
        @DisplayName("mutates FeltVariant in-place when it is not shared")
        void mutatesVariantInPlaceWhenExclusive() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch().uri("/api/felts/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, 5.0, null, new BigDecimal("99.00"), null, null, null))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltVariantId()).isEqualTo(created.feltVariantId());
                        assertThat(felt.thickness()).isEqualTo(5.0);
                        assertThat(felt.price()).isEqualByComparingTo(new BigDecimal("99.00"));
                    });
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.patch().uri("/api/felts/{id}", 99999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, null, null, null, null, null, null))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("returns 422 when feltTypeId does not exist")
        void returns422ForUnknownFeltType() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch().uri("/api/felts/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, null, null, null, null, null, 99999L))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value()));
        }

        @Test
        @DisplayName("returns 422 when supplier does not exist")
        void returns422ForUnknownSupplier() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch().uri("/api/felts/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, null, null, null, null, 99999L, null))
                    .exchange()
                    .expectStatus().isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT.value()));
        }

        @Test
        @DisplayName("re-points to new Felt and new Variant when both change and variant is shared (matrix row 4)")
        void rePointsFeltAndVariantWhenBothChangeAndVariantIsShared() {
            // Two ColorVariants sharing the same Variant
            FeltDto first = postFelt(validCreate());
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            ));
            assertThat(first.feltVariantId()).isEqualTo(second.feltVariantId()); // shared

            // PATCH first: change both feltType AND thickness — shared variant branch fires
            restTestClient.patch().uri("/api/felts/{id}", first.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, 7.0, null, null, null, null, feltTypeId2))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeName()).isEqualTo("Polyester");
                        assertThat(felt.thickness()).isEqualTo(7.0);
                        assertThat(felt.feltVariantId()).isNotEqualTo(second.feltVariantId());
                        assertThat(felt.feltId()).isNotEqualTo(second.feltId());
                    });

            // second must still point to the original Felt and Variant
            restTestClient.get().uri("/api/felts/{id}", second.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeName()).isEqualTo("Wool");
                        assertThat(felt.feltVariantId()).isEqualTo(second.feltVariantId());
                    });
        }

        @Test
        @DisplayName("finds-or-creates Felt and mutates Variant in-place when Felt is shared but Variant is exclusive (matrix row 7)")
        void resolvesFeltAndMutatesVariantInPlaceWhenFeltShared() {
            // Two ColorVariants: same Felt, different specs (different exclusive Variants)
            FeltDto first = postFelt(validCreate()); // specs: 2.0/300.0/12.50
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    5.0, 400.0, new BigDecimal("20.00"), // different specs → exclusive Variant
                    "ART-001", supplierId, feltTypeId    // same Felt
            ));
            assertThat(first.feltId()).isEqualTo(second.feltId());
            assertThat(first.feltVariantId()).isNotEqualTo(second.feltVariantId());

            // PATCH first: change feltType (feltChanged) AND thickness (variantChanged)
            // feltIsShared=true → find-or-create new Felt; Variant mutated in-place
            restTestClient.patch().uri("/api/felts/{id}", first.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, 9.0, null, null, null, null, feltTypeId2))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeName()).isEqualTo("Polyester");
                        assertThat(felt.thickness()).isEqualTo(9.0);
                        assertThat(felt.feltId()).isNotEqualTo(first.feltId());          // new Felt (shared → find-or-create)
                        assertThat(felt.feltVariantId()).isEqualTo(first.feltVariantId()); // Variant mutated in-place
                    });

            // second must still have the original type and Felt
            restTestClient.get().uri("/api/felts/{id}", second.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> assertThat(felt.feltTypeName()).isEqualTo("Wool"));
        }

        @Test
        @DisplayName("re-points exclusive Felt to existing Felt and cleans up the orphan (matrix row 9)")
        void reUsesExistingFeltAndCleansUpOrphanOnExclusiveCollision() {
            FeltDto felt1 = postFelt(validCreate()); // (Wool, supplier1, ART-001)
            FeltDto felt2 = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-002", supplierId, feltTypeId // different article → separate exclusive Felt
            ));
            assertThat(felt1.feltId()).isNotEqualTo(felt2.feltId());
            Long orphanFeltId = felt2.feltId();

            // PATCH felt2's articleNumber to match felt1's Felt identity — collision path
            restTestClient.patch().uri("/api/felts/{id}", felt2.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, null, null, null, "ART-001", null, null))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> assertThat(felt.feltId()).isEqualTo(felt1.feltId()));

            // Orphaned Felt must have been deleted
            assertThat(feltRepository.existsById(orphanFeltId)).isFalse();
        }

        @Test
        @DisplayName("mutates both Felt and Variant in-place when the exclusive chain has no siblings (matrix row 10)")
        void mutatesBothFeltAndVariantInPlaceOnExclusiveChain() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch().uri("/api/felts/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, 5.0, null, null, null, null, feltTypeId2))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> {
                        assertThat(felt.feltTypeName()).isEqualTo("Polyester");
                        assertThat(felt.thickness()).isEqualTo(5.0);
                        assertThat(felt.feltId()).isEqualTo(created.feltId());           // Felt mutated in-place
                        assertThat(felt.feltVariantId()).isEqualTo(created.feltVariantId()); // Variant mutated in-place
                    });
        }

        @Test
        @DisplayName("returns 400 when patched thickness is not positive")
        void validationRejectsNonPositiveThicknessOnPatch() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch().uri("/api/felts/{id}", created.id())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltDto(null, null, -1.0, null, null, null, null, null))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("thickness"));
        }
    }

    // ── DELETE /api/felts/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/felts/{id}")
    class DeleteFelt {

        @Test
        @DisplayName("deletes existing felt and returns 204")
        void deletesExistingFelt() {
            FeltDto created = postFelt(validCreate());

            restTestClient.delete().uri("/api/felts/{id}", created.id())
                    .exchange()
                    .expectStatus().isNoContent();

            restTestClient.get().uri("/api/felts/{id}", created.id())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("does not cascade to shared FeltVariant")
        void doesNotCascadeToSharedVariant() {
            FeltDto first = postFelt(validCreate());
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            ));
            assertThat(first.feltVariantId()).isEqualTo(second.feltVariantId());

            restTestClient.delete().uri("/api/felts/{id}", first.id())
                    .exchange()
                    .expectStatus().isNoContent();

            restTestClient.get().uri("/api/felts/{id}", second.id())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltDto.class)
                    .value(felt -> assertThat(felt.feltVariantId()).isEqualTo(second.feltVariantId()));
        }

        @Test
        @DisplayName("orphanRemoval cascades up: deleting last ColorVariant also removes Variant and Felt")
        void deletingLastColorVariantCleansUpOrphanedVariantAndFelt() {
            FeltDto created = postFelt(validCreate());
            Long variantId = created.feltVariantId();
            Long feltDbId  = created.feltId();

            restTestClient.delete().uri("/api/felts/{id}", created.id())
                    .exchange()
                    .expectStatus().isNoContent();

            assertThat(feltVariantRepository.existsById(variantId)).isFalse();
            assertThat(feltRepository.existsById(feltDbId)).isFalse();
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.delete().uri("/api/felts/{id}", 99999)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }
}
