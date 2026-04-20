package com.nemia.core.common.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(FluxNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleFluxNotFound(FluxNotFoundException ex, HttpServletRequest request) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.NOT_FOUND.value(),
      HttpStatus.NOT_FOUND.name(),
      ex.getMessage(),
      request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidationException(MethodArgumentNotValidException ex, HttpServletRequest request) {
    Map<String, String> validationErrors = ex
      .getBindingResult()
      .getFieldErrors()
      .stream()
      .collect(Collectors.toMap(error -> error.getField(), error -> error.getDefaultMessage(), (existing, replacement) -> existing));

    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.BAD_REQUEST.value(),
      HttpStatus.BAD_REQUEST.name(),
      "Validation failed",
      request.getRequestURI()
    );

    errorResponse.setValidationErrors(validationErrors);

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.BAD_REQUEST.value(),
      HttpStatus.BAD_REQUEST.name(),
      ex.getMessage(),
      request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
    logger.error("Erreur interne sur {} : {}", request.getRequestURI(), ex.getMessage(), ex);

    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.INTERNAL_SERVER_ERROR.value(),
      HttpStatus.INTERNAL_SERVER_ERROR.name(),
      "Une erreur interne est survenue.",
      request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }

  @ExceptionHandler(BienNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleBienNotFound(BienNotFoundException ex, HttpServletRequest request) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.NOT_FOUND.value(),
      HttpStatus.NOT_FOUND.name(),
      ex.getMessage(),
      request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(BienAlreadyExistsException.class)
  public ResponseEntity<ApiErrorResponse> handleBienAlreadyExists(BienAlreadyExistsException ex, HttpServletRequest request) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.CONFLICT.value(),
      HttpStatus.CONFLICT.name(),
      ex.getMessage(),
      request.getRequestURI()
    );

    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(ExerciceNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleExerciceNotFound(ExerciceNotFoundException ex, HttpServletRequest request) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.NOT_FOUND.value(),
      HttpStatus.NOT_FOUND.name(),
      ex.getMessage(),
      request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(ExerciceAlreadyExistsException.class)
  public ResponseEntity<ApiErrorResponse> handleExerciceAlreadyExists(ExerciceAlreadyExistsException ex, HttpServletRequest request) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.CONFLICT.value(),
      HttpStatus.CONFLICT.name(),
      ex.getMessage(),
      request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
  }

  @ExceptionHandler(JustificatifNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleJustificatifNotFound(JustificatifNotFoundException ex, HttpServletRequest request) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(
      LocalDateTime.now(),
      HttpStatus.NOT_FOUND.value(),
      HttpStatus.NOT_FOUND.name(),
      ex.getMessage(),
      request.getRequestURI()
    );
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }
}
