package ch.portami.inventorybackend.offer.domain;

/**
 * The stages an offer moves through, from initial quote to settlement.
 */
public enum OfferState {
    /** Initial quote sent to the customer. */
    OFFER,
    /** The customer has confirmed the order. */
    ORDER_CONFIRMATION,
    /** An invoice has been issued. */
    INVOICE,
    /** A first reminder that payment is due. */
    PAYMENT_REMINDER,
    /** First formal dunning notice for overdue payment. */
    FIRST_DUNNING_NOTICE,
    /** Second formal dunning notice for overdue payment. */
    SECOND_DUNNING_NOTICE,
    /** The offer has been settled and closed. */
    COMPLETED,
    /** The customer has rejected the order. */
    CANCELLED,
    /** The customer didn't respond in the allowed timeframe after receiving the order. */
    NO_RESPONSE
}

