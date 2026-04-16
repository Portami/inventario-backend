package ch.portami.inventorybackend.api;

import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import ch.portami.inventorybackend.felt.dto.CreateFeltTypeDto;
import ch.portami.inventorybackend.felt.dto.FeltTypeDto;
import ch.portami.inventorybackend.felt.dto.UpdateFeltTypeDto;
import ch.portami.inventorybackend.felt.repository.FeltTypeRepository;
import java.util.List;
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
class FeltTypeControllerIntegrationTest {

    @Container
    @ServiceConnection
    static MariaDBContainer mariadb = new MariaDBContainer("mariadb:11.4")
            .withDatabaseName("inventory_test")
            .withUsername("test")
            .withPassword("test");

    private static final String BASE_URI = "/api/felt-types";
    private static final ParameterizedTypeReference<List<FeltTypeDto>> TYPE_LIST =
            new ParameterizedTypeReference<>() {};

    @Autowired private RestTestClient restTestClient;
    @Autowired private FeltTypeRepository feltTypeRepository;

    @BeforeEach
    void resetTypes() {
        feltTypeRepository.deleteAll();
    }

    private Long createTypeAndGetId(String name) {
        FeltTypeDto body = restTestClient.post().uri(BASE_URI)
                                         .contentType(MediaType.APPLICATION_JSON)
                                         .body(new CreateFeltTypeDto(name))
                                         .exchange()
                                         .expectStatus().isCreated()
                                         .expectBody(FeltTypeDto.class)
                                         .returnResult()
                                         .getResponseBody();

        assertThat(body).isNotNull();
        return body.id();
    }

    @Nested
    @DisplayName("GET /api/felt-types")
    class GetAllFeltTypes {

        @Test
        @DisplayName("returns empty list when no types exist")
        void returnsEmptyList() {
            restTestClient.get().uri(BASE_URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TYPE_LIST)
                    .value(types -> assertThat(types).isEmpty());
        }

        @Test
        @DisplayName("returns all felt types")
        void returnsAllTypes() {
            createTypeAndGetId("Wool");
            createTypeAndGetId("Synthetic");

            restTestClient.get().uri(BASE_URI)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(TYPE_LIST)
                    .value(types -> assertThat(types).hasSize(2));
        }
    }

    @Nested
    @DisplayName("POST /api/felt-types")
    class CreateFeltType {

        @Test
        @DisplayName("creates type and returns 201 with full body")
        void createsType() {
            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltTypeDto("Wool"))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody(FeltTypeDto.class)
                    .value(type -> {
                        assertThat(type.id()).isGreaterThan(0);
                        assertThat(type.name()).isEqualTo("Wool");
                    });
        }

        @Test
        @DisplayName("returns 400 when name is blank")
        void rejectsBlankName() {
            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltTypeDto(""))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> {
                        assertThat(err.status()).isEqualTo(HttpStatus.BAD_REQUEST.value());
                        assertThat(err.message()).contains("name");
                    });
        }

        @Test
        @DisplayName("returns 400 when name is null")
        void rejectsNullName() {
            restTestClient.post().uri(BASE_URI)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CreateFeltTypeDto(null))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("name"));
        }
    }

    @Nested
    @DisplayName("GET /api/felt-types/{id}")
    class GetFeltTypeById {

        @Test
        @DisplayName("returns type when it exists")
        void returnsExistingType() {
            Long id = createTypeAndGetId("Wool");

            restTestClient.get().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltTypeDto.class)
                    .value(type -> {
                        assertThat(type.id()).isEqualTo(id);
                        assertThat(type.name()).isEqualTo("Wool");
                    });
        }

        @Test
        @DisplayName("returns 404 when type does not exist")
        void returns404ForMissingType() {
            restTestClient.get().uri(BASE_URI + "/{id}", 9999)
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
    @DisplayName("PUT /api/felt-types/{id}")
    class UpdateFeltType {

        @Test
        @DisplayName("updates name")
        void updatesName() {
            Long id = createTypeAndGetId("Wool");

            restTestClient.put().uri(BASE_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltTypeDto("Synthetic"))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(FeltTypeDto.class)
                    .value(type -> {
                        assertThat(type.id()).isEqualTo(id);
                        assertThat(type.name()).isEqualTo("Synthetic");
                    });
        }

        @Test
        @DisplayName("returns 400 when name is blank")
        void rejectsBlankName() {
            Long id = createTypeAndGetId("Wool");

            restTestClient.put().uri(BASE_URI + "/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltTypeDto(""))
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.message()).contains("name"));
        }

        @Test
        @DisplayName("returns 404 when type does not exist")
        void returns404ForMissingType() {
            restTestClient.put().uri(BASE_URI + "/{id}", 9999)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new UpdateFeltTypeDto("Synthetic"))
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody(ErrorResponse.class)
                    .value(err -> assertThat(err.status()).isEqualTo(HttpStatus.NOT_FOUND.value()));
        }
    }

    @Nested
    @DisplayName("DELETE /api/felt-types/{id}")
    class DeleteFeltType {

        @Test
        @DisplayName("deletes existing type and returns 204")
        void deletesExistingType() {
            Long id = createTypeAndGetId("Wool");

            restTestClient.delete().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isNoContent();

            restTestClient.get().uri(BASE_URI + "/{id}", id)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("returns 404 when type does not exist")
        void returns404ForMissingType() {
            restTestClient.delete().uri(BASE_URI + "/{id}", 9999)
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
