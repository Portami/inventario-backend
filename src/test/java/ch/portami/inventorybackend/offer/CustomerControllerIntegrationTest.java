package ch.portami.inventorybackend.offer;

import ch.portami.inventorybackend.BaseIntegrationTest;
import ch.portami.inventorybackend.offer.dto.CreateCustomerDto;
import ch.portami.inventorybackend.offer.dto.UpdateCustomerDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.repository.CustomerRepository;
import java.util.List;
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
class CustomerControllerIntegrationTest extends BaseIntegrationTest {

    private static final String CUSTOMERS_URL = "/api/customers";

    @Autowired
    private RestTestClient restTestClient;
    @Autowired
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
    }

    @Nested
    @DisplayName("GET /api/customers - list")
    class ListCustomersTests {

        @Test
        void testListEmpty() {
            List<?> body = restTestClient.get()
                                         .uri(CUSTOMERS_URL)
                                         .exchange()
                                         .expectStatus()
                                         .isOk()
                                         .returnResult(List.class)
                                         .getResponseBody();

            assertThat(body).isEmpty();
        }
    }

    @Nested
    @DisplayName("POST /api/customers - create")
    class CreateCustomerTests {

        @Test
        void testCreateCustomer() {
            CreateCustomerDto dto = new CreateCustomerDto("Acme", "John", "a@b.c", "+41", "Street", "8000", "Zuerich",
                    "CH", "VAT123");

            var body = restTestClient.post()
                                     .uri(CUSTOMERS_URL)
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .body(dto)
                                     .exchange()
                                     .expectStatus()
                                     .isCreated()
                                     .returnResult(ch.portami.inventorybackend.offer.dto.CustomerDto.class)
                                     .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.id()).isGreaterThan(0);
            assertThat(body.name()).isEqualTo("Acme");
        }
    }

    @Nested
    @DisplayName("PATCH /api/customers/{id} - update")
    class PatchCustomerTests {

        @Test
        void testPatchCustomerUpdates() {
            Customer saved = customerRepository.save(new Customer("Old"));

            UpdateCustomerDto update = new UpdateCustomerDto(null, null, "new@a.b", null, null, null, null, null, null);

            var body = restTestClient.patch()
                                     .uri(CUSTOMERS_URL + "/{id}", saved.getId())
                                     .contentType(MediaType.APPLICATION_JSON)
                                     .body(update)
                                     .exchange()
                                     .expectStatus()
                                     .isOk()
                                     .returnResult(ch.portami.inventorybackend.offer.dto.CustomerDto.class)
                                     .getResponseBody();

            assertThat(body).isNotNull();
            assertThat(body.email()).isEqualTo("new@a.b");
        }
    }
}

