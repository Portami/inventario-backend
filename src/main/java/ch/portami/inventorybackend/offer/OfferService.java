package ch.portami.inventorybackend.offer;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import ch.portami.inventorybackend.offer.domain.OfferState;
import ch.portami.inventorybackend.offer.dto.CreateOfferDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.entity.Offer;
import ch.portami.inventorybackend.offer.mapper.OfferMapper;
import ch.portami.inventorybackend.offer.repository.CustomerRepository;
import ch.portami.inventorybackend.offer.repository.OfferRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OfferService {

    private final OfferMapper offerMapper;
    private final OfferRepository offerRepository;
    private final CustomerRepository customerRepository;

    public OfferService(OfferMapper offerMapper, OfferRepository offerRepository,
            CustomerRepository customerRepository) {
        this.offerMapper = offerMapper;
        this.offerRepository = offerRepository;
        this.customerRepository = customerRepository;
    }

    public OfferDto createOffer(CreateOfferDto dto) {
        Offer offer = offerMapper.toOffer(dto);
        offer.setCustomer(resolveCustomer(dto.customerName()));
        offer.setState(OfferState.OFFER);

        return offerMapper.toOfferDto(offerRepository.save(offer));
    }

    @Transactional(readOnly = true)
    public OfferDto getOfferById(Long id) {
        return offerMapper.toOfferDto(findById(id));
    }

    @Transactional(readOnly = true)
    public List<OfferDto> listOffers(OfferState state) {
        return offerRepository.findByState(state)
                              .stream()
                              .map(offerMapper::toOfferDto)
                              .toList();
    }

    public OfferDto updateOffer(Long id, UpdateOfferDto dto) {
        Offer offer = findById(id);

        if (dto.customerName() != null) {
            offer.setCustomer(resolveCustomer(dto.customerName()));
        }

        offerMapper.updateOffer(dto, offer);

        return offerMapper.toOfferDto(offerRepository.save(offer));
    }

    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }

    private Offer findById(Long id) {
        return offerRepository.findById(id)
                              .orElseThrow(() -> new ResourceNotFoundException("Offer not found: " + id));
    }

    private Customer resolveCustomer(String name) {
        return customerRepository.findByNameIgnoreCase(name.trim())
                                 .orElseGet(() -> customerRepository.save(new Customer(name.trim())));
    }
}