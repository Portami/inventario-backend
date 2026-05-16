package ch.portami.inventorybackend.offer;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.offer.domain.OfferState;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemOptionalDto;
import ch.portami.inventorybackend.offer.dto.OfferItemDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.entity.Offer;
import ch.portami.inventorybackend.offer.entity.OfferItem;
import ch.portami.inventorybackend.offer.repository.CustomerRepository;
import ch.portami.inventorybackend.offer.repository.OfferItemRepository;
import ch.portami.inventorybackend.offer.repository.OfferRepository;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OfferItemControllerIntegrationTest extends BaseIntegrationTest {

    private static final String OFFERS_URL = "/api/offers";

    @Autowired private RestTestClient restTestClient;
    @Autowired private OfferRepository offerRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private OfferItemRepository offerItemRepository;

    @BeforeEach
    void setUp() {
        offerItemRepository.deleteAll();
        offerRepository.deleteAll();
        customerRepository.deleteAll();
    }

    private Offer createTestOffer(String customerName, OfferState state) {
        Customer customer = customerRepository.save(new Customer(customerName));
        Offer offer = new Offer(null, customer, state, ZonedDateTime.now(), ZonedDateTime.now());
        return offerRepository.save(offer);
    }

    @Nested
    @DisplayName("POST /api/offers/{id}/items - add item")
    class AddItemTests {

        @Test
        void testAddItemCreatesItem() {
            Offer offer = createTestOffer("Acme", OfferState.OFFER);

            CreateOfferItemOptionalDto dto = new CreateOfferItemOptionalDto(5L, "desc", 1, new BigDecimal("1.00"));

            OfferItemDto body = restTestClient.post()
                                            .uri(OFFERS_URL + "/{id}/items", offer.getId())
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .body(dto)
                                            .exchange()
                                            .expectStatus()
                                            .isCreated()
                                            .returnResult(OfferItemDto.class)
                                            .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isGreaterThan(0);
            assertThat(body.productVariantId()).isEqualTo(5L);
        }
    }

    @Nested
    @DisplayName("DELETE /api/offers/{id}/items/{itemId} - delete item")
    class DeleteItemTests {

        @Test
        void testDeleteItemSuccess() {
            Offer offer = createTestOffer("Acme", OfferState.OFFER);
            OfferItem it = new OfferItem(offer.getId(), 7L, "x", 1, new BigDecimal("1.00"), new BigDecimal("1.00"));
            OfferItem saved = offerItemRepository.save(it);

            restTestClient.delete()
                          .uri(OFFERS_URL + "/{id}/items/{itemId}", offer.getId(), saved.getId())
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            assertThat(offerItemRepository.existsById(saved.getId())).isFalse();
        }
    }
}

