package ch.portami.inventorybackend.offer.mapper;

import ch.portami.inventorybackend.offer.domain.OfferState;
import ch.portami.inventorybackend.offer.dto.CreateOfferDto;
import ch.portami.inventorybackend.offer.dto.CreateOfferItemDto;
import ch.portami.inventorybackend.offer.dto.CustomerDto;
import ch.portami.inventorybackend.offer.dto.OfferDto;
import ch.portami.inventorybackend.offer.dto.OfferItemDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferDto;
import ch.portami.inventorybackend.offer.dto.UpdateOfferItemDto;
import ch.portami.inventorybackend.offer.entity.Customer;
import ch.portami.inventorybackend.offer.entity.Offer;
import ch.portami.inventorybackend.offer.entity.OfferItem;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    // ── Customer ──────────────────────────────────────────────────────────────

    CustomerDto toCustomerDto(Customer customer);

    // ── OfferItem ─────────────────────────────────────────────────────────────

    @Mapping(source = "productVariantId", target = "productId")
    @Mapping(target = "quantity", expression = "java(offerItem.getQuantity().intValue())")
    OfferItemDto toOfferItemDto(OfferItem offerItem);

    @Mapping(source = "productId", target = "productVariantId")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "offerId", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "quantity", expression = "java(java.math.BigDecimal.valueOf(dto.quantity()))")
    OfferItem toOfferItem(CreateOfferItemDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "productId", target = "productVariantId")
    @Mapping(target = "offerId", ignore = true)
    @Mapping(target = "totalPrice", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "quantity", expression = "java(dto.quantity() != null ? java.math.BigDecimal.valueOf(dto.quantity()) : offerItem.getQuantity())")
    void updateOfferItem(UpdateOfferItemDto dto, @MappingTarget OfferItem offerItem);

    // ── Offer ─────────────────────────────────────────────────────────────────

    @Mapping(source = "customer", target = "customerDto")
    @Mapping(target = "items", source = "items")
    OfferDto toOfferDto(Offer offer, @Context List<OfferItem> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "state", constant = "OFFER")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Offer toOffer(CreateOfferDto dto);

    @AfterMapping
    default void setCustomerOnCreate(CreateOfferDto dto, @MappingTarget Offer offer) {
        offer.setCustomer(new Customer(dto.customerName()));
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateOffer(UpdateOfferDto dto, @MappingTarget Offer offer);

    @AfterMapping
    default void setCustomerOnUpdate(UpdateOfferDto dto, @MappingTarget Offer offer) {
        if (dto.customerName() != null) {
            offer.getCustomer().setName(dto.customerName());
        }
    }

    // ── OfferItem list update (mirrors ProductMapper's attribute handling) ────

    default void updateOfferItems(List<UpdateOfferItemDto> dtos, List<OfferItem> existingItems, long offerId) {
        if (dtos == null) {
            return;
        }

        Map<Long, OfferItem> untouched = existingItems.stream()
                                                      .collect(Collectors.toMap(OfferItem::getId, Function.identity()));

        for (UpdateOfferItemDto dto : dtos) {
            if (dto.id() != null) {
                OfferItem existing = untouched.remove(dto.id());
                if (existing == null) {
                    throw new IllegalArgumentException(
                            "OfferItem with id " + dto.id() + " does not belong to offer " + offerId);
                }
                updateOfferItem(dto, existing);
            } else {
                OfferItem newItem = new OfferItem(
                        offerId,
                        dto.productId(),
                        dto.description(),
                        dto.quantity() != null ? BigDecimal.valueOf(dto.quantity()) : BigDecimal.ZERO,
                        dto.unitPrice() != null ? dto.unitPrice() : BigDecimal.ZERO,
                        BigDecimal.ZERO
                );
                existingItems.add(newItem);
            }
        }

        existingItems.removeAll(untouched.values());
    }
}