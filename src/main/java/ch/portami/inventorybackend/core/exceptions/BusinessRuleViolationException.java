package ch.portami.inventorybackend.core.exceptions;

public class BusinessRuleViolationException extends RuntimeException {

    public BusinessRuleViolationException(String message) {
        super(message);
    }

}
