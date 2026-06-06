package ch.portami.inventorybackend.offer;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import ch.portami.inventorybackend.offer.dto.CreateCustomerDto;
import ch.portami.inventorybackend.offer.dto.CustomerDto;
import ch.portami.inventorybackend.offer.dto.UpdateCustomerDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.mapper.OfferMapper;
import ch.portami.inventorybackend.offer.repository.CustomerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for retrieving and managing customers.
 */
@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OfferMapper offerMapper;

    public CustomerService(CustomerRepository customerRepository, OfferMapper offerMapper) {
        this.customerRepository = customerRepository;
        this.offerMapper = offerMapper;
    }

    /**
     * Retrieves all customers.
     *
     * @return a list of DTOs for all customers
     */
    @Transactional(readOnly = true)
    public List<CustomerDto> listCustomers() {
        return customerRepository.findAll()
                                 .stream()
                                 .map(offerMapper::toCustomerDto)
                                 .toList();
    }

    /**
     * Creates a new customer.
     *
     * @param dto the data for the new customer
     * @return the DTO of the created customer
     */
    public CustomerDto createCustomer(CreateCustomerDto dto) {
        Customer customer = offerMapper.toCustomer(dto);
        Customer saved = customerRepository.save(customer);
        return offerMapper.toCustomerDto(saved);
    }

    /**
     * Applies a partial update to a customer. Only non-null fields of the DTO are applied.
     *
     * @param id  the ID of the customer to update
     * @param dto the requested updates; null fields are left unchanged
     * @return the DTO of the updated customer
     * @throws ResourceNotFoundException if no customer with the given ID exists
     */
    public CustomerDto updateCustomer(Long id, UpdateCustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                                              .orElseThrow(
                                                      () -> new ResourceNotFoundException("Customer not found: " + id));

        offerMapper.updateCustomer(dto, customer);
        Customer saved = customerRepository.save(customer);
        return offerMapper.toCustomerDto(saved);
    }
}

