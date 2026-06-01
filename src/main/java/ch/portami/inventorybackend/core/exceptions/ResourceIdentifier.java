package ch.portami.inventorybackend.core.exceptions;

import java.io.Serial;
import java.io.Serializable;

/**
 * Identifies a single resource involved in a {@link ResourceSpecificException}.
 *
 * <p>The {@code type} names the resource attribute (e.g. {@code "feltId"}) and {@code id} is its
 * value; {@link GlobalExceptionHandler} surfaces these as properties on the error response.
 *
 * @param type the name of the resource identifier (used as the property key on the error response)
 * @param id   the value of the identifier
 */
public record ResourceIdentifier(String type, Serializable id) implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
}
