package ch.portami.inventorybackend.offer;

import ch.portami.inventorybackend.offer.dto.CreateCustomerDto;
import ch.portami.inventorybackend.offer.dto.CustomerDto;
import ch.portami.inventorybackend.offer.dto.UpdateCustomerDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.mapper.OfferMapper;
import ch.portami.inventorybackend.offer.mapper.OfferMapperImpl;
import ch.portami.inventorybackend.offer.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    private final OfferMapper offerMapper = new OfferMapperImpl();

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerService(customerRepository, offerMapper);
    }

    @Test
    void listCustomers_mapsList() {
        Customer c = new Customer(1L, "Acme");
        given(customerRepository.findAll()).willReturn(List.of(c));

        var result = customerService.listCustomers();

        assertThat(result).hasSize(1);
        CustomerDto dto = result.get(0);
        assertThat(dto.name()).isEqualTo("Acme");
    }

    @Test
    void createCustomer_persistsAndReturnsDto() {
        CreateCustomerDto dto = new CreateCustomerDto("Acme", "John", "a@b.c", "+41", "Street", "8000", "Zuerich", "CH", "VAT123");
        given(customerRepository.save(any(Customer.class))).willAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(2L);
            return c;
        });

        var result = customerService.createCustomer(dto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.name()).isEqualTo("Acme");
    }

    @Test
    void updateCustomer_updatesFields() {
        Customer existing = new Customer(3L, "Old");
        given(customerRepository.findById(3L)).willReturn(Optional.of(existing));
        given(customerRepository.save(any(Customer.class))).willAnswer(inv -> inv.getArgument(0));

        UpdateCustomerDto update = new UpdateCustomerDto("New", null, "new@a.b", null, null, null, null, null, null);

        var result = customerService.updateCustomer(3L, update);

        assertThat(result).isNotNull();
        assertThat(result.name()).isEqualTo("New");
        assertThat(result.email()).isEqualTo("new@a.b");
    }
}

