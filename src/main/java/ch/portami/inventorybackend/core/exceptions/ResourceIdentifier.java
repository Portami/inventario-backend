package ch.portami.inventorybackend.core.exceptions;

import java.io.Serial;
import java.io.Serializable;

public record ResourceIdentifier(String type, Serializable id) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
