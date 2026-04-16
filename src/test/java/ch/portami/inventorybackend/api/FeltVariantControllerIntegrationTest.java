package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.felt.dto.CreateFeltVariantDto;
import ch.portami.inventorybackend.felt.dto.FeltVariantDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltVariantDto;
import ch.portami.inventorybackend.felt.entity.Felt;
import ch.portami.inventorybackend.felt.entity.FeltType;
import ch.portami.inventorybackend.felt.entity.Supplier;
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
class FeltVariantControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final ParameterizedTypeReference<List<FeltVariantDto>> VARIANT_LIST =
            new ParameterizedTypeReference<>() {};

    @Autowired private RestTestClient restTestClient;
    @Autowired private FeltVariantRepository feltVariantRepository;
    @Autowired private FeltRepository feltRepository;
    @Autowired private FeltTypeRepository feltTypeRepository;
    @Autowired private SupplierRepository supplierRepository;

    private Long feltId;

    private String baseUri() {
        return "/api/felts/" + feltId + "/variants";
    }

    @BeforeAll
    void setupHierarchy() {
        FeltType feltType = feltTypeRepository.save(new FeltType("Wool"));
        Supplier supplier = supplierRepository.save(new Supplier("Supplier A"));
        feltId = feltRepository.save(new Felt(feltType, supplier, "ART-001")).getId();
    }

    @BeforeEach
    void resetVariants() {
        feltVariantRepository.deleteAll();
    }

    private CreateFeltVariantDto validRequest() {
        return new CreateFeltVariantDto(5.0, 300.0, new BigDecimal("12.99"));
    }

    private Long createVariantAndGetId(CreateFeltVariantDto request) {
        FeltVariantDto body = restTestClient.post().uri(baseUri())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(request)
                                            .exchange()
                                            .expectStatus().isCreated()
                                            .expectBody(FeltVariantDto.class)
                                            .returnResult()
                                            .getResponseBody();

        assertThat(body).isNotNull();
        return body.id();
    }

    private void createVariant(CreateFeltVariantDto request) {
        createVariantAndGetId(request);
    }

    @Nested
    @DisplayName("GET /api/felts/{feltId}/variants")
    class GetAllFeltVariants {

        @Test
        @DisplayName("returns empty list when no variants exist")
        void returnsEmptyList() {
            restTestClient.get().uri(baseUri())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(VARIANT_LIST)
                    .value(variants -> assertThat(variants).isEmpty());
        }

        @Test
        @DisplayName("returns all variants for the felt")
        void returnsAllVariants() {
            createVariant(validRequest());
            createVariant(new CreateFeltVariantDto(3.0, 200.0, new BigDecimal("8.50")));

            restTestClient.get().uri(baseUri())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(VARIANT_LIST)
                    .value(variants -> assertThat(variants).hasSize(2));
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.get().uri("/api/felts/9999/variants")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).contains("9999");
                    });
        }
    }

    @Nested
    @DisplayName("POST /api/felts/{feltId}/variants")
    class CreateFeltVariant {

        @Test
        @DisplayName("creates variant and returns 201 with full body")
        void createsVariant() {
            restTestClient.post().uri(baseUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(validRequest())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(FeltVariantDto.class)
                    .value(variant -> {
                        assertThat(variant.id()).isGreaterThan(0);
                        assertThat(variant.feltId()).isEqualTo(feltId);
                        assertThat(variant.articleNumber()).isEqualTo("ART-001");
                        assertThat(variant.feltTypeName()).isEqualTo("Wool");
                        assertThat(variant.supplierName()).isEqualTo("Supplier A");
                        assertThat(variant.thickness()).isEqualTo(5.0);
                        assertThat(variant.density()).isEqualTo(300.0);
                        assertThat(variant.price()).isEqualByComparingTo(new BigDecimal("12.99"));
                    });
        }

        @Test
        @DisplayName("returns 400 when thickness is missing")
        void rejectsMissingThickness() {
            var invalid = new CreateFeltVariantDto(null, 300.0, new BigDecimal("12.99"));

            restTestClient.post().uri(baseUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("thickness"));
        }

        @Test
        @DisplayName("returns 400 when density is missing")
        void rejectsMissingDensity() {
            var invalid = new CreateFeltVariantDto(5.0, null, new BigDecimal("12.99"));

            restTestClient.post().uri(baseUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("density"));
        }

        @Test
        @DisplayName("returns 400 when price is missing")
        void rejectsMissingPrice() {
            var invalid = new CreateFeltVariantDto(5.0, 300.0, null);

            restTestClient.post().uri(baseUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("price"));
        }

        @Test
        @DisplayName("returns 400 when thickness is not positive")
        void rejectsNonPositiveThickness() {
            var invalid = new CreateFeltVariantDto(-1.0, 300.0, new BigDecimal("12.99"));

            restTestClient.post().uri(baseUri())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(invalid)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("thickness"));
        }

        @Test
        @DisplayName("returns 404 when felt does not exist")
        void returns404ForUnknownFelt() {
            restTestClient.post().uri("/api/felts/9999/variants")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(validRequest())
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).contains("9999");
                    });
        }
    }

    @Nested
    @DisplayName("GET /api/felts/{feltId}/variants/{id}")
    class GetFeltVariantById {

        @Test
        @DisplayName("returns variant when it exists")
        void returnsExistingVariant() {
            Long id = createVariantAndGetId(validRequest());

            restTestClient.get().uri(baseUri() + "/{id}", id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltVariantDto.class)
                    .value(variant -> {
                        assertThat(variant.id()).isEqualTo(id);
                        assertThat(variant.feltId()).isEqualTo(feltId);
                        assertThat(variant.thickness()).isEqualTo(5.0);
                    });
        }

        @Test
        @DisplayName("returns 404 when variant does not exist")
        void returns404ForMissingVariant() {
            restTestClient.get().uri(baseUri() + "/{id}", 9999)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).contains("9999");
                    });
        }
    }

    @Nested
    @DisplayName("PUT /api/felts/{feltId}/variants/{id}")
    class UpdateFeltVariant {

        @Test
        @DisplayName("updates thickness, density and price")
        void updatesVariant() {
            Long id = createVariantAndGetId(validRequest());
            var update = new UpdateFeltVariantDto(8.0, 400.0, new BigDecimal("19.99"));

            restTestClient.put().uri(baseUri() + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(update)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltVariantDto.class)
                    .value(variant -> {
                        assertThat(variant.id()).isEqualTo(id);
                        assertThat(variant.thickness()).isEqualTo(8.0);
                        assertThat(variant.density()).isEqualTo(400.0);
                        assertThat(variant.price()).isEqualByComparingTo(new BigDecimal("19.99"));
                    });
        }

        @Test
        @DisplayName("partial update leaves unchanged fields intact")
        void partialUpdatePreservesFields() {
            Long id = createVariantAndGetId(validRequest());
            var update = new UpdateFeltVariantDto(8.0, null, null);

            restTestClient.put().uri(baseUri() + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(update)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltVariantDto.class)
                    .value(variant -> {
                        assertThat(variant.thickness()).isEqualTo(8.0);
                        assertThat(variant.density()).isEqualTo(300.0);
                        assertThat(variant.price()).isEqualByComparingTo(new BigDecimal("12.99"));
                    });
        }

        @Test
        @DisplayName("returns 404 when variant does not exist")
        void returns404ForMissingVariant() {
            restTestClient.put().uri(baseUri() + "/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltVariantDto(8.0, null, null))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("DELETE /api/felts/{feltId}/variants/{id}")
    class DeleteFeltVariant {

        @Test
        @DisplayName("deletes existing variant and returns 204")
        void deletesExistingVariant() {
            Long id = createVariantAndGetId(validRequest());

            restTestClient.delete().uri(baseUri() + "/{id}", id)
                    .exchange()
                    .expectStatus().isNoContent();

            restTestClient.get().uri(baseUri() + "/{id}", id)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("returns 404 when variant does not exist")
        void returns404ForMissingVariant() {
            restTestClient.delete().uri(baseUri() + "/{id}", 9999)
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value());
                        assertThat(err.message()).contains("9999");
                    });
        }
    }
}
