package ch.portami.inventorybackend.products.exceptions;

/**
 * Thrown when a product with the requested ID cannot be found.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(int id) {
        super("Product with id %d not found".formatted(id));
    }
}
