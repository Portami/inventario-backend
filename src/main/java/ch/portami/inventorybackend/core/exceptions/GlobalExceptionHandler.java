package ch.portami.inventorybackend.core.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Translates domain exceptions into the {@link ErrorResponse} contract defined in the OpenAPI spec.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, "Resource not found", ex);
    }

    @ExceptionHandler(InvalidResourceReferenceException.class)
    public ProblemDetail handleInvalidReference(InvalidResourceReferenceException ex) {
        return buildProblemDetail(HttpStatus.UNPROCESSABLE_CONTENT, "A referenced id does not exit", ex);
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ProblemDetail handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        return buildProblemDetail(HttpStatus.CONFLICT, "Business rule violated", ex);
    }

    @ExceptionHandler(ResourceSpecificException.class)
    public ProblemDetail handleResourceSpecificException(ResourceSpecificException ex) {
        log.warn("ResourceSpecificException reached fallback handler. A specific @ExceptionHandler should have handled this exception: [{}] {}",
                ex.getClass().getSimpleName(), ex.getMessage(), ex);

        return buildProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unhandled resource-specific exception", ex);
    }

    private ProblemDetail buildProblemDetail(HttpStatus httpStatus, String title, ResourceSpecificException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(httpStatus, ex.getMessage());
        pd.setTitle(title);
        for (ResourceIdentifier ri : ex.getResourceIdentifiers()) {
            pd.setProperty(ri.type(), ri.id());
        }

        return pd;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataIntegrity(DataIntegrityViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        pd.setTitle("Cannot delete: resource is still referenced by other inventory items");
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Validation failed");

        List<Map<String, Object>> violations = ex.getConstraintViolations()
                                                 .stream()
                                                 .map(GlobalExceptionHandler::toProblemDetailErrorFormat)
                                                 .toList();

        pd.setProperty("errors", violations);

        return pd;
    }

    private static @NonNull Map<String, Object> toProblemDetailErrorFormat(ConstraintViolation<?> constraintViolation) {
        Map<String, Object> map = new HashMap<>();

        map.put("field", constraintViolation.getPropertyPath().toString());
        map.put("rejected", constraintViolation.getInvalidValue());
        map.put("message", constraintViolation.getMessage());

        return map;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Exception reached fallback handler. A specific @ExceptionHandler should have handled this exception: [{}] {}",
                ex.getClass().getName(), ex.getMessage(), ex);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred");
        pd.setTitle("Unhandled exception");
        return pd;
    }
}
