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

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final OfferMapper offerMapper;

    public CustomerService(CustomerRepository customerRepository, OfferMapper offerMapper) {
        this.customerRepository = customerRepository;
        this.offerMapper = offerMapper;
    }

    @Transactional(readOnly = true)
    public List<CustomerDto> listCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(offerMapper::toCustomerDto)
                .toList();
    }

    public CustomerDto createCustomer(CreateCustomerDto dto) {
        Customer customer = offerMapper.toCustomer(dto);
        Customer saved = customerRepository.save(customer);
        return offerMapper.toCustomerDto(saved);
    }

    public CustomerDto updateCustomer(Long id, UpdateCustomerDto dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found: " + id));

        offerMapper.updateCustomer(dto, customer);
        Customer saved = customerRepository.save(customer);
        return offerMapper.toCustomerDto(saved);
    }
}

