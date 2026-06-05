package ch.portami.inventorybackend.offer.domain;

public enum OfferState {
    OFFER,
    ORDER_CONFIRMATION,
    INVOICE,
    PAYMENT_REMINDER,
    FIRST_DUNNING_NOTICE,
    SECOND_DUNNING_NOTICE,
    COMPLETED,
    CANCELLED,
    NO_RESPONSE
}

