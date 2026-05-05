package ch.portami.inventorybackend.product.exception;

import ch.portami.inventorybackend.core.exceptions.ErrorResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "ch.portami.inventorybackend.product")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ProductExceptionHandler {

    @ExceptionHandler(NotEnoughInventoryException.class)
    public ResponseEntity<ErrorResponse> handleNotEnoughInventoryException(NotEnoughInventoryException ex) {
        ErrorResponse errorResponse = ErrorResponse.of(HttpStatus.CONFLICT.value(), ex.getMessage());
        return ResponseEntity.status(errorResponse.status())
                             .body(errorResponse);
    }

}
