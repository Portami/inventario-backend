package ch.portami.inventorybackend.felt;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.felt.dto.CreateFeltDto;
import ch.portami.inventorybackend.felt.dto.FeltDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltDto;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
import ch.portami.inventorybackend.felt.repository.FeltRepository;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeltControllerIntegrationTest extends BaseIntegrationTest {

    private static final ParameterizedTypeReference<List<FeltDto>> FELT_LIST =
            new ParameterizedTypeReference<>() {
            };

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private FeltRepository feltRepository;
    @Autowired
    private FeltTypeRepository feltTypeRepository;
    @Autowired
    private SupplierRepository supplierRepository;

    private Long supplierId;
    private Long supplierId2;
    private Long feltTypeId;
    private Long feltTypeId2;

    @BeforeAll
    void setupFixtures() {
        supplierId = supplierRepository.save(new Supplier("Test Supplier"))
                                       .getId();
        supplierId2 = supplierRepository.save(new Supplier("Test Supplier 2"))
                                        .getId();
        feltTypeId = feltTypeRepository.save(new FeltType("Wool"))
                                       .getId();
        feltTypeId2 = feltTypeRepository.save(new FeltType("Polyester"))
                                        .getId();
    }

    @BeforeEach
    void resetFelts() {
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
        FeltDto body = restTestClient.post()
                                     .uri("/api/felts")
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .body(dto)
                                     .exchange()
                                     .expectStatus()
                                     .isCreated()
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
            restTestClient.get()
                          .uri("/api/felts")
                          .exchange()
                          .expectStatus()
                          .isOk()
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

            restTestClient.get()
                          .uri("/api/felts")
                          .exchange()
                          .expectStatus()
                          .isOk()
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

            restTestClient.get()
                          .uri("/api/felts/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
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
            restTestClient.get()
                          .uri("/api/felts/{id}", 99999)
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    // ── POST /api/felts ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/felts")
    class CreateFelt {

        @Test
        @DisplayName("creates felt and returns 201 with full body and Location header")
        void createsFelt() {
            var response = restTestClient.post()
                                         .uri("/api/felts")
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .body(validCreate())
                                         .exchange();

            response.expectStatus()
                    .isCreated()
                    .expectHeader()
                    .valueMatches("Location", "/api/felts/\\d+")
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

            var location = response.returnResult(FeltDto.class)
                                   .getResponseHeaders()
                                   .getLocation();
            assertThat(location).isNotNull();
            assertThat(location.toString()).contains("/api/felts/");
        }

        @Test
        @DisplayName("creates separate felts even with identical fields")
        void createsSeparateFelts() {
            FeltDto first = postFelt(validCreate());
            FeltDto second = postFelt(validCreate());

            assertThat(first.id()).isNotEqualTo(second.id());
        }

        @Test
        @DisplayName("returns 400 when color is blank")
        void rejectsBlankColor() {
            var invalid = new CreateFeltDto(
                    "", "Supplier Red",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            );

            restTestClient.post()
                          .uri("/api/felts")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(invalid)
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getDetail()).contains("color"));
        }

        @Test
        @DisplayName("returns 422 when feltTypeId does not exist")
        void returns422ForUnknownFeltType() {
            var dto = new CreateFeltDto(
                    "Red", "Supplier Red",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, 99999L
            );

            restTestClient.post()
                          .uri("/api/felts")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(dto)
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(
                                  HttpStatus.UNPROCESSABLE_CONTENT.value()));
        }

        @Test
        @DisplayName("returns 422 when supplier does not exist")
        void returns422ForUnknownSupplier() {
            var dto = new CreateFeltDto(
                    "Red", "Supplier Red",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", 99999L, feltTypeId
            );

            restTestClient.post()
                          .uri("/api/felts")
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(dto)
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(
                                  HttpStatus.UNPROCESSABLE_CONTENT.value()));
        }
    }

    // ── PATCH /api/felts/{id} ───────────────────────────────────────────────

    @Nested
    @DisplayName("PATCH /api/felts/{id}")
    class UpdateFelt {

        @Test
        @DisplayName("updates color only")
        void updatesColorOnly() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch()
                          .uri("/api/felts/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto("Purple", null, null, null, null, null, null, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltDto.class)
                          .value(felt -> {
                              assertThat(felt.color()).isEqualTo("Purple");
                              assertThat(felt.thickness()).isEqualTo(2.0);
                          });
        }

        @Test
        @DisplayName("updates supplier")
        void updatesSupplier() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch()
                          .uri("/api/felts/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto(null, null, null, null, null, null, supplierId2, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltDto.class)
                          .value(felt -> assertThat(felt.supplierId()).isEqualTo(supplierId2));
        }

        @Test
        @DisplayName("updates felt type")
        void updatesFeltType() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch()
                          .uri("/api/felts/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto(null, null, null, null, null, null, null, feltTypeId2, null, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltDto.class)
                          .value(felt -> assertThat(felt.feltTypeName()).isEqualTo("Polyester"));
        }

        @Test
        @DisplayName("updates thickness and price")
        void updatesThicknessAndPrice() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch()
                          .uri("/api/felts/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto(null, null, 5.0, null, new BigDecimal("99.00"), null, null, null,
                                  null, null))
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltDto.class)
                          .value(felt -> {
                              assertThat(felt.thickness()).isEqualTo(5.0);
                              assertThat(felt.price()).isEqualByComparingTo(new BigDecimal("99.00"));
                          });
        }

        @Test
        @DisplayName("does not affect other felts")
        void doesNotAffectOtherFelts() {
            FeltDto first = postFelt(validCreate());
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    3.0, 400.0, new BigDecimal("15.00"),
                    "ART-002", supplierId, feltTypeId
            ));

            restTestClient.patch()
                          .uri("/api/felts/{id}", first.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto("Purple", null, 9.0, null, null, null, null, feltTypeId2, null, null))
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.get()
                          .uri("/api/felts/{id}", second.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltDto.class)
                          .value(felt -> {
                              assertThat(felt.color()).isEqualTo("Blue");
                              assertThat(felt.thickness()).isEqualTo(3.0);
                              assertThat(felt.feltTypeName()).isEqualTo("Wool");
                          });
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.patch()
                          .uri("/api/felts/{id}", 99999)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto(null, null, null, null, null, null, null, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }

        @Test
        @DisplayName("returns 422 when feltTypeId does not exist")
        void returns422ForUnknownFeltType() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch()
                          .uri("/api/felts/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto(null, null, null, null, null, null, null, 99999L, null, null))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(
                                  HttpStatus.UNPROCESSABLE_CONTENT.value()));
        }

        @Test
        @DisplayName("returns 422 when supplier does not exist")
        void returns422ForUnknownSupplier() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch()
                          .uri("/api/felts/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto(null, null, null, null, null, null, 99999L, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.UNPROCESSABLE_CONTENT)
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(
                                  HttpStatus.UNPROCESSABLE_CONTENT.value()));
        }

        @Test
        @DisplayName("returns 400 when patched thickness is not positive")
        void validationRejectsNonPositiveThicknessOnPatch() {
            FeltDto created = postFelt(validCreate());

            restTestClient.patch()
                          .uri("/api/felts/{id}", created.id())
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateFeltDto(null, null, -1.0, null, null, null, null, null, null, null))
                          .exchange()
                          .expectStatus()
                          .isBadRequest()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getDetail()).contains("thickness"));
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

            restTestClient.delete()
                          .uri("/api/felts/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            restTestClient.get()
                          .uri("/api/felts/{id}", created.id())
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("does not affect other felts when one is deleted")
        void doesNotAffectOtherFelts() {
            FeltDto first = postFelt(validCreate());
            FeltDto second = postFelt(new CreateFeltDto(
                    "Blue", "Supplier Blue",
                    2.0, 300.0, new BigDecimal("12.50"),
                    "ART-001", supplierId, feltTypeId
            ));

            restTestClient.delete()
                          .uri("/api/felts/{id}", first.id())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            restTestClient.get()
                          .uri("/api/felts/{id}", second.id())
                          .exchange()
                          .expectStatus()
                          .isOk()
                          .expectBody(FeltDto.class)
                          .value(felt -> assertThat(felt.color()).isEqualTo("Blue"));
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.delete()
                          .uri("/api/felts/{id}", 99999)
                          .exchange()
                          .expectStatus()
                          .isNotFound()
                          .expectBody(ProblemDetail.class)
                          .value(err -> assertThat(err.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }
}
