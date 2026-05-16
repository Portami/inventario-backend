package ch.portami.inventorybackend.offer;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import ch.portami.inventorybackend.offer.domain.OfferState;
import ch.portami.inventorybackend.offer.dto.CreateOfferDto;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemOptionalDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import ch.portami.inventorybackend.offer.dto.OfferItemDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.entity.Offer;
import ch.portami.inventorybackend.offer.entity.OfferItem;
import ch.portami.inventorybackend.offer.mapper.OfferMapper;
import ch.portami.inventorybackend.offer.repository.CustomerRepository;
import ch.portami.inventorybackend.offer.repository.OfferItemRepository;
import ch.portami.inventorybackend.offer.repository.OfferRepository;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OfferService {

    private final OfferMapper offerMapper;
    private final OfferRepository offerRepository;
    private final OfferItemRepository offerItemRepository;
    private final CustomerRepository customerRepository;

    public OfferService(OfferMapper offerMapper, OfferRepository offerRepository,
            OfferItemRepository offerItemRepository, CustomerRepository customerRepository) {
        this.offerMapper = offerMapper;
        this.offerRepository = offerRepository;
        this.offerItemRepository = offerItemRepository;
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
        if (state == null) {
            return offerRepository.findAll()
                                  .stream()
                                  .map(offerMapper::toOfferDto)
                                  .toList();
        }

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

        if (offer.getState() == OfferState.INVOICE && offer.getDueAt() == null) {
            offer.setDueAt(ZonedDateTime.now().plusDays(30));
        }

        return offerMapper.toOfferDto(offerRepository.save(offer));
    }

    public void deleteOffer(Long id) {
        offerRepository.deleteById(id);
    }

    public OfferItemDto addOfferItem(Long offerId, CreateOfferItemOptionalDto dto) {
        Offer offer = findById(offerId);

        OfferItem item = offerMapper.toOfferItem(dto);
        item.setOfferId(offer.getId());

        OfferItem saved = offerItemRepository.save(item);
        return offerMapper.toOfferItemDto(saved);
    }

    public void deleteOfferItem(Long offerId, Long itemId) {
        offerItemRepository.findById(itemId).ifPresent(item -> {
            if (!offerId.equals(item.getOfferId())) {
                throw new ResourceNotFoundException("Offer item not found: " + itemId);
            }
            offerItemRepository.deleteById(itemId);
        });
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