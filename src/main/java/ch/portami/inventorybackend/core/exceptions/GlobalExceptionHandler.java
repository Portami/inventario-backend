package ch.portami.inventorybackend.core.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Centralised exception handling for all REST controllers.
 *
 * <p>Translates application and framework exceptions into RFC 7807 {@link ProblemDetail} responses
 * with appropriate HTTP statuses. {@link ResourceSpecificException} subtypes are mapped to their
 * conventional statuses (404 / 422 / 409), validation failures to 400, and anything unhandled falls
 * back to a logged 500.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<@NonNull Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        pd.setTitle("Validation failed");

        List<Map<String, Object>> errors = ex.getBindingResult()
                                             .getFieldErrors()
                                             .stream()
                                             .map(fieldErrorMapFunction())
                                             .toList();

        pd.setProperty("errors", errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body(pd);
    }

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

    /**
     * Fallback for any {@link ResourceSpecificException} not caught by a more specific handler above.
     * Reaching this handler indicates a missing dedicated handler, so it is logged as a warning and
     * reported as a 500.
     *
     * @param ex the resource-specific exception that no specific handler claimed
     * @return a 500 problem detail describing the unhandled exception
     */
    @ExceptionHandler(ResourceSpecificException.class)
    public ProblemDetail handleResourceSpecificException(ResourceSpecificException ex) {
        log.warn(
                "ResourceSpecificException reached fallback handler. A specific @ExceptionHandler should have handled this exception: [{}] {}",
                ex.getClass()
                  .getSimpleName(), ex.getMessage(), ex);

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

    /**
     * Handles database integrity violations, which most commonly occur when deleting a resource that
     * is still referenced by other inventory items, and reports them as a 409 (Conflict).
     *
     * @param ex the data integrity violation raised by the persistence layer
     * @return a 409 problem detail explaining the resource is still referenced
     */
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
                                                 .map(constraintViolationMapFunction())
                                                 .toList();

        pd.setProperty("errors", violations);

        return pd;
    }

    private static @NonNull Function<FieldError, Map<String, Object>> fieldErrorMapFunction() {
        return fe -> toErrorMap(fe.getField(), fe.getRejectedValue(), fe.getDefaultMessage());
    }

    private static @NonNull Function<ConstraintViolation<?>, Map<String, Object>> constraintViolationMapFunction() {
        return cv -> toErrorMap(cv.getPropertyPath()
                                  .toString(), cv.getInvalidValue(), cv.getMessage());
    }

    private static @NonNull Map<String, Object> toErrorMap(String field, Object rejected, String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("field", field);
        map.put("rejected", rejected);
        map.put("message", message);
        return map;
    }

    /**
     * Last-resort fallback for any exception not handled elsewhere. Logs the error and returns a
     * generic 500 without leaking internal details to the client.
     *
     * @param ex the otherwise unhandled exception
     * @return a 500 problem detail with a generic message
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error(
                "Exception reached fallback handler. A specific @ExceptionHandler should have handled this exception: [{}] {}",
                ex.getClass()
                  .getName(), ex.getMessage(), ex);

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred");
        pd.setTitle("Unhandled exception");
        return pd;
    }
}
