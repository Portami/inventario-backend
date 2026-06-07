package ch.portami.inventorybackend.offer;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.offer.domain.OfferState;
import ch.portami.inventorybackend.offer.dto.CreateOfferDto;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.entity.Offer;
import ch.portami.inventorybackend.offer.repository.CustomerRepository;
import ch.portami.inventorybackend.offer.repository.OfferRepository;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OfferControllerIntegrationTest extends BaseIntegrationTest {

    private static final String OFFERS_URL = "/api/offers";

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private OfferRepository offerRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        offerRepository.deleteAll();
        customerRepository.deleteAll();
    }

    private Offer createTestOffer(String customerName, OfferState state) {
        Customer customer = customerRepository.save(new Customer(customerName));
        Offer offer = new Offer(null, customer, state);
        return offerRepository.save(offer);
    }

    private CreateOfferItemDto testItemDto() {
        return new CreateOfferItemDto(1L, "Test item", 2, new BigDecimal("9.99"));
    }

    @Nested
    @DisplayName("POST /api/offers - Create Offer")
    class CreateOfferTests {

        @Test
        @DisplayName("Should create offer with valid data")
        void testCreateOfferSuccess() {
            var dto = new CreateOfferDto("Acme", List.of(testItemDto()));

            OfferDto body = restTestClient.post()
                                          .uri(OFFERS_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .body(dto)
                                          .exchange()
                                          .expectStatus()
                                          .isCreated()
                                          .returnResult(OfferDto.class)
                                          .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isGreaterThan(0);
            assertThat(body.customerDto()
                           .name()).isEqualTo("Acme");
            assertThat(body.state()).isEqualTo(OfferState.OFFER);
            assertThat(body.offerSent()).isFalse();
            assertThat(body.dueAt()).isNotNull();
            assertThat(body.items()).hasSize(1);
        }

        @Test
        @DisplayName("Should reuse existing customer when name matches")
        void testCreateOfferReusesExistingCustomer() {
            customerRepository.save(new Customer("Acme"));

            restTestClient.post()
                          .uri(OFFERS_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateOfferDto("acme", List.of(testItemDto())))
                          .exchange()
                          .expectStatus()
                          .isCreated();

            assertThat(customerRepository.count()).isEqualTo(1);
        }

        @Test
        @DisplayName("Should return 400 when customerName is blank")
        void testCreateOfferBlankCustomerName() {
            restTestClient.post()
                          .uri(OFFERS_URL)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new CreateOfferDto("", List.of(testItemDto())))
                          .exchange()
                          .expectStatus()
                          .isBadRequest();
        }

        @Test
        @DisplayName("Should create offer with null items list (no lines)")
        void testCreateOfferNullItems() {
            OfferDto body = restTestClient.post()
                                          .uri(OFFERS_URL)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .body(new CreateOfferDto("Acme", null))
                                          .exchange()
                                          .expectStatus()
                                          .isCreated()
                                          .returnResult(OfferDto.class)
                                          .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.items()).isEmpty();
        }
    }

    @Nested
    @DisplayName("GET /api/offers/{id} - Get Offer By ID")
    class GetOfferByIdTests {

        @Test
        @DisplayName("Should return offer by ID")
        void testGetOfferByIdSuccess() {
            Offer offer = createTestOffer("Acme", OfferState.OFFER);

            OfferDto body = restTestClient.get()
                                          .uri(OFFERS_URL + "/{id}", offer.getId())
                                          .exchange()
                                          .expectStatus()
                                          .isOk()
                                          .returnResult(OfferDto.class)
                                          .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isEqualTo(offer.getId());
            assertThat(body.customerDto()
                           .name()).isEqualTo("Acme");
        }

        @Test
        @DisplayName("Should return 404 when offer does not exist")
        void testGetOfferByIdNotFound() {
            restTestClient.get()
                          .uri(OFFERS_URL + "/{id}", 99999L)
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }

    @Nested
    @DisplayName("GET /api/offers?state= - List Offers")
    class ListOffersTests {

        @Test
        @DisplayName("Should return offers filtered by state")
        void testListOffersByState() {
            createTestOffer("Acme", OfferState.OFFER);
            createTestOffer("Globex", OfferState.OFFER);
            createTestOffer("Initech", OfferState.ORDER_CONFIRMATION);

            List<OfferDto> body = restTestClient.get()
                                                .uri(OFFERS_URL + "?state=OFFER")
                                                .exchange()
                                                .expectStatus()
                                                .isOk()
                                                .returnResult(new ParameterizedTypeReference<List<OfferDto>>() {
                                                })
                                                .getResponseBody();

            assertThat(body).hasSize(2);
            assertThat(body).extracting(o -> o.customerDto()
                                              .name())
                            .containsExactlyInAnyOrder("Acme", "Globex");
        }

        @Test
        @DisplayName("Should return empty list when no offers match state")
        void testListOffersByStateEmpty() {
            createTestOffer("Acme", OfferState.ORDER_CONFIRMATION);

            List<OfferDto> body = restTestClient.get()
                                                .uri(OFFERS_URL + "?state=OFFER")
                                                .exchange()
                                                .expectStatus()
                                                .isOk()
                                                .returnResult(new ParameterizedTypeReference<List<OfferDto>>() {
                                                })
                                                .getResponseBody();

            assertThat(body).isEmpty();
        }
    }

    @Nested
    @DisplayName("PATCH /api/offers/{id} - Update Offer")
    class UpdateOfferTests {

        private Long offerId;

        @BeforeEach
        void setup() {
            offerId = createTestOffer("Acme", OfferState.OFFER).getId();
        }

        @Test
        @DisplayName("Should update customer name")
        void testUpdateOfferCustomerName() {
            OfferDto body = restTestClient.patch()
                                          .uri(OFFERS_URL + "/{id}", offerId)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .body(new UpdateOfferDto("NewCo", null, new ArrayList<>(), null, null))
                                          .exchange()
                                          .expectStatus()
                                          .isOk()
                                          .returnResult(OfferDto.class)
                                          .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.customerDto()
                           .name()).isEqualTo("NewCo");
            assertThat(body.state()).isEqualTo(OfferState.OFFER);
        }

        @Test
        @DisplayName("Should update state to INVOICE and set due date 10 days out")
        void testUpdateOfferStateToInvoice() {
            OfferDto body = restTestClient.patch()
                                          .uri(OFFERS_URL + "/{id}", offerId)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .body(new UpdateOfferDto(null, OfferState.INVOICE, new ArrayList<>(), null,
                                                  null))
                                          .exchange()
                                          .expectStatus()
                                          .isOk()
                                          .returnResult(OfferDto.class)
                                          .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.state()).isEqualTo(OfferState.INVOICE);
            assertThat(body.dueAt()).isNotNull();
            assertThat(body.dueAt()).isAfter(ZonedDateTime.now()
                                                          .plusDays(9));
            assertThat(body.dueAt()).isBefore(ZonedDateTime.now()
                                                           .plusDays(11));
        }

        @Test
        @DisplayName("Should update state to PAYMENT_REMINDER and set due date 5 days out")
        void testUpdateOfferStateToPaymentReminder() {
            OfferDto body = restTestClient.patch()
                                          .uri(OFFERS_URL + "/{id}", offerId)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .body(new UpdateOfferDto(null, OfferState.PAYMENT_REMINDER, new ArrayList<>(),
                                                  null, null))
                                          .exchange()
                                          .expectStatus()
                                          .isOk()
                                          .returnResult(OfferDto.class)
                                          .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.state()).isEqualTo(OfferState.PAYMENT_REMINDER);
            assertThat(body.dueAt()).isNotNull();
            assertThat(body.dueAt()).isAfter(ZonedDateTime.now()
                                                          .plusDays(4));
            assertThat(body.dueAt()).isBefore(ZonedDateTime.now()
                                                           .plusDays(6));
        }

        @Test
        @DisplayName("Should mark offer as sent")
        void testMarkOfferSent() {
            OfferDto body = restTestClient.patch()
                                          .uri(OFFERS_URL + "/{id}", offerId)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .body(new UpdateOfferDto(null, null, new ArrayList<>(), null, true))
                                          .exchange()
                                          .expectStatus()
                                          .isOk()
                                          .returnResult(OfferDto.class)
                                          .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.offerSent()).isTrue();
        }

        @Test
        @DisplayName("Should reset offerSent to false on state change")
        void testOfferSentResetsOnStateChange() {
            restTestClient.patch()
                          .uri(OFFERS_URL + "/{id}", offerId)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateOfferDto(null, null, new ArrayList<>(), null, true))
                          .exchange()
                          .expectStatus()
                          .isOk();

            OfferDto body = restTestClient.patch()
                                          .uri(OFFERS_URL + "/{id}", offerId)
                                          .contentType(MediaType.APPLICATION_JSON)
                                          .body(new UpdateOfferDto(null, OfferState.INVOICE, new ArrayList<>(), null,
                                                  null))
                                          .exchange()
                                          .expectStatus()
                                          .isOk()
                                          .returnResult(OfferDto.class)
                                          .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.offerSent()).isFalse();
        }

        @Test
        @DisplayName("Should return 404 when offer does not exist")
        void testUpdateOfferNotFound() {
            restTestClient.patch()
                          .uri(OFFERS_URL + "/{id}", 99999L)
                          .contentType(MediaType.APPLICATION_JSON)
                          .body(new UpdateOfferDto("NewCo", null, new ArrayList<>(), null, null))
                          .exchange()
                          .expectStatus()
                          .isNotFound();
        }
    }

    @Nested
    @DisplayName("DELETE /api/offers/{id} - Delete Offer")
    class DeleteOfferTests {

        @Test
        @DisplayName("Should delete offer successfully")
        void testDeleteOfferSuccess() {
            Offer offer = createTestOffer("Acme", OfferState.OFFER);
            long offerId = offer.getId();

            restTestClient.delete()
                          .uri(OFFERS_URL + "/{id}", offerId)
                          .exchange()
                          .expectStatus()
                          .isNoContent();

            assertThat(offerRepository.existsById(offerId)).isFalse();
        }

        @Test
        @DisplayName("Should return 204 even when offer does not exist")
        void testDeleteOfferNotFound() {
            restTestClient.delete()
                          .uri(OFFERS_URL + "/{id}", 99999L)
                          .exchange()
                          .expectStatus()
                          .isNoContent();
        }
    }
}