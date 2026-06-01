package ch.portami.inventorybackend.offer;

import ch.portami.inventorybackend.core.exceptions.ResourceNotFoundException;
import ch.portami.inventorybackend.offer.domain.OfferState;
import ch.portami.inventorybackend.offer.dto.CreateOfferDto;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import ch.portami.inventorybackend.offer.dto.OfferItemDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferItemDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.entity.Offer;
import ch.portami.inventorybackend.offer.entity.OfferItem;
import ch.portami.inventorybackend.offer.mapper.OfferMapper;
import ch.portami.inventorybackend.offer.mapper.OfferMapperImpl;
import ch.portami.inventorybackend.offer.repository.CustomerRepository;
import ch.portami.inventorybackend.offer.repository.OfferItemRepository;
import ch.portami.inventorybackend.offer.repository.OfferRepository;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class OfferServiceTest {

    @Mock
    OfferRepository offerRepository;

    @Mock
    OfferItemRepository offerItemRepository;

    @Mock
    CustomerRepository customerRepository;

    private final OfferMapper offerMapper = new OfferMapperImpl();

    private OfferService offerService;

    @BeforeEach
    void setUp() {
        offerService = new OfferService(offerMapper, offerRepository, offerItemRepository, customerRepository);
    }

    @Test
    void addOfferItem_savesAndReturnsCreatedItem() {
        Offer offer = persistedOffer();
        given(offerRepository.findById(ID)).willReturn(Optional.of(offer));
        given(offerItemRepository.save(any(OfferItem.class))).willAnswer(inv -> {
            OfferItem it = inv.getArgument(0);
            it.setId(2L);
            return it;
        });

        var dto = new ch.portami.inventorybackend.offer.dto.CreateOfferItemOptionalDto(
                ch.portami.inventorybackend.offer.domain.OfferItemKind.PRODUCT, 10L, "Desc", 1,
                new BigDecimal("1.00"));
        var result = offerService.addOfferItem(ID, dto);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.productVariantId()).isEqualTo(10L);
    }

    @Test
    void deleteOfferItem_deletesWhenBelongsToOffer() {
        OfferItem item = new OfferItem(ID, 10L, "Desc", 1, new BigDecimal("1.00"), new BigDecimal("1.00"));
        item.setId(5L);

        given(offerItemRepository.findById(5L)).willReturn(Optional.of(item));

        offerService.deleteOfferItem(ID, 5L);

        verify(offerItemRepository).deleteById(5L);
    }

    @Test
    void deleteOfferItem_noopWhenNotFound() {
        given(offerItemRepository.findById(99L)).willReturn(Optional.empty());

        offerService.deleteOfferItem(ID, 99L);

        verify(offerItemRepository).findById(99L);
        verifyNoMoreInteractions(offerItemRepository);
    }

    @Test
    void deleteOfferItem_throwsWhenItemNotBelongingToOffer() {
        OfferItem item = new OfferItem(999L, 10L, "Desc", 1, new BigDecimal("1.00"), new BigDecimal("1.00"));
        item.setId(6L);

        given(offerItemRepository.findById(6L)).willReturn(Optional.of(item));

        assertThatThrownBy(() -> offerService.deleteOfferItem(ID, 6L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    private static final Long ID = 1L;
    private static final String CUSTOMER_NAME = "Acme";
    private static final Customer CUSTOMER = new Customer(ID, CUSTOMER_NAME);

    private static Offer persistedOffer() {
        Offer offer = new Offer(ID, CUSTOMER, OfferState.OFFER, ZonedDateTime.now(), ZonedDateTime.now());
        OfferItem item = new OfferItem(ID, 10L, "A widget", 2, new BigDecimal("9.99"), new BigDecimal("19.98"));

        item.setId(1L);
        offer.getOfferItems()
             .add(item);

        return offer;
    }

    @Test
    void createOffer_mapsFieldsCorrectly() {
        CreateOfferItemDto itemDto = new CreateOfferItemDto(10L, "A widget", 2, new BigDecimal("9.99"));
        CreateOfferDto dto = new CreateOfferDto(CUSTOMER_NAME, List.of(itemDto));

        given(customerRepository.findByNameIgnoreCase(CUSTOMER_NAME)).willReturn(Optional.of(CUSTOMER));
        given(offerRepository.save(any(Offer.class))).willAnswer(inv -> {
            Offer o = inv.getArgument(0);
            o.setId(ID);
            return o;
        });
        given(offerItemRepository.save(any(OfferItem.class))).willAnswer(inv -> {
            OfferItem item = inv.getArgument(0);
            item.setId(1L);
            return item;
        });

        OfferDto result = offerService.createOffer(dto);

        assertThat(result.state()).isEqualTo(OfferState.OFFER);
        assertThat(result.customerDto()
                         .name()).isEqualTo(CUSTOMER_NAME);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items()
                         .getFirst()
                         .productVariantId()).isEqualTo(10L);
        assertThat(result.items()
                         .getFirst()
                         .quantity()).isEqualTo(2);
        assertThat(result.items()
                         .getFirst()
                         .unitPrice()).isEqualByComparingTo("9.99");
    }

    @Test
    void getOfferById_mapsAllFields() {
        Offer offer = persistedOffer();

        given(offerRepository.findById(ID)).willReturn(Optional.of(offer));

        OfferDto result = offerService.getOfferById(ID);

        assertThat(result.id()).isEqualTo(ID);
        assertThat(result.state()).isEqualTo(OfferState.OFFER);
        assertThat(result.customerDto()
                         .id()).isEqualTo(ID);
        assertThat(result.customerDto()
                         .name()).isEqualTo(CUSTOMER_NAME);
        assertThat(result.items()).hasSize(1);
        assertThat(result.items()
                         .getFirst()
                         .productVariantId()).isEqualTo(10L);
    }

    @Test
    void getOfferById_notFound_throws() {
        given(offerRepository.findById(ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> offerService.getOfferById(ID))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining(ID.toString());
    }

    @Test
    void listOffers_withParam_mapsQueriedList() {
        given(offerRepository.findByState(OfferState.OFFER)).willReturn(List.of(persistedOffer()));

        List<OfferDto> result = offerService.listOffers(OfferState.OFFER);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()
                         .customerDto()
                         .name()).isEqualTo(CUSTOMER_NAME);

        verify(offerRepository).findByState(OfferState.OFFER);
    }

    @Test
    void listOffers_mapsFullList() {
        given(offerRepository.findAll()).willReturn(List.of(persistedOffer()));

        List<OfferDto> result = offerService.listOffers(null);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()
                         .customerDto()
                         .name()).isEqualTo(CUSTOMER_NAME);

        verify(offerRepository).findAll();
    }

    @Test
    void updateOffer_partialUpdate_onlyChangesProvidedFields() {
        Offer offer = persistedOffer();

        given(offerRepository.findById(ID)).willReturn(Optional.of(offer));
        given(offerRepository.save(any(Offer.class))).willAnswer(inv -> inv.getArgument(0));
        given(customerRepository.findByNameIgnoreCase("NewCo")).willReturn(Optional.empty());
        given(customerRepository.save(any(Customer.class))).willAnswer(inv -> inv.getArgument(0));

        OfferDto result = offerService.updateOffer(ID, new UpdateOfferDto("NewCo", null, null, null, null));

        assertThat(result.customerDto()
                         .name()).isEqualTo("NewCo");
        assertThat(result.state()).isEqualTo(offer.getState());
        assertThat(result.items()).isEmpty();
    }

    @Test
    void updateOffer_itemFields_areMappedCorrectly() {
        Offer offer = persistedOffer();
        UpdateOfferItemDto updatedItem = new UpdateOfferItemDto(ID, 99L, "Updated desc", 5, new BigDecimal("4.50"));

        given(offerRepository.findById(ID)).willReturn(Optional.of(offer));
        given(offerRepository.save(any(Offer.class))).willAnswer(inv -> inv.getArgument(0));

        OfferDto updatedOffer = offerService.updateOffer(ID,
                new UpdateOfferDto(null, null, List.of(updatedItem), null, null));

        OfferItemDto item = updatedOffer.items()
                                        .stream()
                                        .findFirst()
                                        .orElse(null);

        assertThat(item).isNotNull();
        assertThat(item.productVariantId()).isEqualTo(99L);
        assertThat(item.description()).isEqualTo("Updated desc");
        assertThat(item.quantity()).isEqualTo(5);
        assertThat(item.unitPrice()).isEqualByComparingTo("4.50");
    }

    @Test
    void updateOffer_withNullCustomerName_skipsCustomerResolution() {
        given(offerRepository.findById(ID)).willReturn(Optional.of(persistedOffer()));

        offerService.updateOffer(ID, new UpdateOfferDto(null, OfferState.OFFER, null, null, null));

        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    void updateOffer_notFound_throws() {
        given(offerRepository.findById(ID)).willReturn(Optional.empty());

        UpdateOfferDto dto = new UpdateOfferDto(null, null, null, null, null);

        assertThatThrownBy(() -> offerService.updateOffer(ID, dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteOffer_delegatesToRepository() {
        offerService.deleteOffer(ID);

        verify(offerRepository).deleteById(ID);
    }
}