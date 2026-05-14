package ch.portami.inventorybackend.offer.mapper;

import ch.portami.inventorybackend.offer.dto.CreateOfferDto;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemDto;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemOptionalDto;
import ch.portami.inventorybackend.offer.dto.CustomerDto;
import ch.portami.inventorybackend.offer.dto.CreateCustomerDto;
import ch.portami.inventorybackend.offer.dto.UpdateCustomerDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import ch.portami.inventorybackend.offer.dto.OfferItemDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.entity.Offer;
import ch.portami.inventorybackend.offer.entity.OfferItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface OfferMapper {

    @Mapping(source = "customer", target = "customerDto")
    @Mapping(source = "offerItems", target = "items")
    OfferDto toOfferDto(Offer offer);

    @Mapping(source = "customerName", target = "customer", qualifiedByName = "customerFromName")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "items", target = "offerItems")
    Offer toOffer(CreateOfferDto dto);

    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(source = "items", target = "offerItems")
    void updateOffer(UpdateOfferDto dto, @MappingTarget Offer offer);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "offerId", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OfferItem toOfferItem(CreateOfferItemDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "offerId", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    OfferItem toOfferItem(CreateOfferItemOptionalDto dto);

    OfferItemDto toOfferItemDto(OfferItem offerItem);

    CustomerDto toCustomerDto(Customer customer);

    Customer toCustomer(CreateCustomerDto dto);

    void updateCustomer(UpdateCustomerDto dto, @MappingTarget Customer customer);

    @Named("customerFromName")
    default Customer customerFromName(String customerName) {
        if (customerName == null) {
            return null;
        }
        return new Customer(customerName);
    }
}