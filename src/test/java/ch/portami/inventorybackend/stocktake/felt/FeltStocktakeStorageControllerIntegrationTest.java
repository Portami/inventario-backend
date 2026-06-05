package ch.portami.inventorybackend.stocktake.felt;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.CreateFeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.dto.stocktake.FeltStocktakeDto;
import ch.portami.inventorybackend.stocktake.felt.repository.FeltStocktakeRepository;
import ch.portami.inventorybackend.storage.entity.Storage;
import ch.portami.inventorybackend.storage.repository.StorageRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FeltStocktakeStorageControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private StorageRepository storageRepository;
    @Autowired
    private FeltStocktakeRepository stocktakeRepository;

    private Long storageAId;
    private Long storageBId;

    @BeforeAll
    void beforeAll() {
        storageAId = storageRepository.save(new Storage("Storage A"))
                                      .getId();
        storageBId = storageRepository.save(new Storage("Storage B"))
                                      .getId();
    }

    @BeforeEach
    void reset() {
        stocktakeRepository.deleteAll();
    }

    private FeltStocktakeDto postStocktake(List<Long> storageIds) {
        FeltStocktakeDto body = restTestClient.post()
                                              .uri("/api/stocktakes")
                                              .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                                              .body(new CreateFeltStocktakeDto("Storage flow", false, storageIds))
                                              .exchange()
                                              .expectStatus()
                                              .isCreated()
                                              .expectBody(FeltStocktakeDto.class)
                                              .returnResult()
                                              .getResponseBody();
        assertThat(body).isNotNull();
        return body;
    }

    private void closeStorage(Long stocktakeId, Long storageId) {
        restTestClient.post()
                      .uri("/api/stocktakes/{id}/storages/{storageId}/close", stocktakeId, storageId)
                      .exchange()
                      .expectStatus()
                      .isNoContent();
    }

    @Nested
    @DisplayName("POST /api/stocktakes/{id}/storages/{storageId}/close")
    class CloseStorage {

        @Test
        @DisplayName("returns 204 when storage is closed successfully")
        void closesStorageSuccessfully() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId, storageBId));

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/storages/{storageId}/close", created.id(), storageAId)
                          .exchange()
                          .expectStatus()
                          .isNoContent();
        }

        @Test
        @DisplayName("does nothing when closing an already closed storage")
        void closesStorage() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId, storageBId));

            closeStorage(created.id(), storageAId);
            closeStorage(created.id(), storageAId);
        }

        @Test
        @DisplayName("returns 404 when storage link does not exist")
        void returns404ForUnknownStorage() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId));

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/storages/{storageId}/close", created.id(), 99999)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 409 when stocktake is completed")
        void rejectsCloseOnCompleted() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId));
            closeStorage(created.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/storages/{storageId}/close", created.id(), storageAId)
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }
    }

    @Nested
    @DisplayName("POST /api/stocktakes/{id}/storages/{storageId}/reopen")
    class ReopenStorage {

        @Test
        @DisplayName("returns 204 when storage is reopened successfully")
        void reopensStorageSuccessfully() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId));
            closeStorage(created.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/storages/{storageId}/reopen", created.id(), storageAId)
                          .exchange()
                          .expectStatus()
                          .isNoContent();
        }

        @Test
        @DisplayName("does nothing when reopening an already open storage")
        void reopensStorage() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId));

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/storages/{storageId}/reopen", created.id(), storageAId)
                          .exchange()
                          .expectStatus()
                          .isNoContent();
        }

        @Test
        @DisplayName("returns 404 when stocktake does not exist")
        void returns404ForUnknownStocktake() {
            restTestClient.post()
                          .uri("/api/stocktakes/99999/storages/{storageId}/reopen", storageAId)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }

        @Test
        @DisplayName("returns 409 when stocktake is completed")
        void rejectsReopenOnCompleted() {
            FeltStocktakeDto created = postStocktake(List.of(storageAId));
            closeStorage(created.id(), storageAId);

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/complete", created.id())
                          .exchange()
                          .expectStatus()
                          .isOk();

            restTestClient.post()
                          .uri("/api/stocktakes/{id}/storages/{storageId}/reopen", created.id(), storageAId)
                          .exchange()
                          .expectStatus()
                          .isEqualTo(HttpStatus.CONFLICT);
        }
    }
}

