package ch.portami.inventorybackend.core.exceptions;

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Translates domain exceptions into the {@link ErrorResponse} contract defined in the OpenAPI spec.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        return buildProblemDetail(HttpStatus.NOT_FOUND, ex.getResourceType() + " not found", ex);
    }

    @ExceptionHandler(InvalidResourceReferenceException.class)
    public ProblemDetail handleInvalidReference(InvalidResourceReferenceException ex) {
        return buildProblemDetail(HttpStatus.UNPROCESSABLE_CONTENT, ex.getResourceType() + " not found", ex);
    }

    private ProblemDetail buildProblemDetail(HttpStatus httpStatus, String title, ResourceSpecificException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(
                httpStatus, ex.getMessage());
        pd.setTitle(title);
        pd.setProperty(ex.getResourceType(), ex.getResourceId());
        return pd;
    }

    //OLD ------------------------------------------------------------------------------------------------------------------------------------------------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                           .getFieldErrors()
                           .stream()
                           .map(fe -> "'%s' %s".formatted(fe.getField(), fe.getDefaultMessage()))
                           .collect(Collectors.joining("; "));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), message));
    }

    // Handles ResponseStatusException thrown by services (e.g. NOT_FOUND with a detail message).
    // Spring picks this over the ErrorResponseException handler below because it is more specific.
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatus(ResponseStatusException ex) {
        int status = ex.getStatusCode()
                       .value();
        String message = ex.getReason() != null ? ex.getReason() : reasonPhrase(status);

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ErrorResponse.of(status, message));
    }

    // Handles framework exceptions such as NoResourceFoundException (404 for unknown routes)
    // and MethodNotAllowedException (405), which extend ErrorResponseException but NOT
    // ResponseStatusException.
    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ErrorResponse> handleErrorResponse(ErrorResponseException ex) {
        int status = ex.getStatusCode()
                       .value();

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ErrorResponse.of(status, reasonPhrase(status)));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(),
                        "Cannot delete: resource is still referenced by other inventory items"));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(BusinessRuleViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.of(HttpStatus.CONFLICT.value(), ex.getMessage()));
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<ErrorResponse> handleInvalidInput(InvalidInputException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(ResourceSpecificException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceSpecificException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "An unexpected error occurred"));
    }

    private static String reasonPhrase(int status) {
        HttpStatus resolved = HttpStatus.resolve(status);
        return resolved != null ? resolved.getReasonPhrase() : String.valueOf(status);
    }
}
