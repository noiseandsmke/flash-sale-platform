package com.enterprise.flashsale.order.adapter.in.web;

import com.enterprise.flashsale.order.domain.exception.DomainException;
import com.enterprise.flashsale.order.domain.exception.InvalidOrderStateException;
import com.enterprise.flashsale.order.domain.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleOrderNotFound(OrderNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.NOT_FOUND, "Order Not Found", ex.getMessage(), ex.getErrorCode(), request);
        problem.setProperty("orderId", ex.getOrderId().value().toString());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(InvalidOrderStateException.class)
    public ResponseEntity<ProblemDetail> handleInvalidOrderState(
            InvalidOrderStateException ex, HttpServletRequest request) {
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.UNPROCESSABLE_CONTENT,
                "Invalid Order State Transition",
                ex.getMessage(),
                ex.getErrorCode(),
                request);
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT).body(problem);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ProblemDetail> handleDomainException(DomainException ex, HttpServletRequest request) {
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.BAD_REQUEST, "Business Rule Violation", ex.getMessage(), ex.getErrorCode(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        ProblemDetail problem = buildProblemDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid Request Payload",
                "Validation failed for one or more fields",
                "INVALID_INPUT",
                request);
        problem.setProperty("invalidParams", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnhandled(HttpServletRequest request) {
        ProblemDetail problem = buildProblemDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Internal Server Error",
                "An unexpected server error occurred",
                "INTERNAL_SERVER_ERROR",
                request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }

    private ProblemDetail buildProblemDetail(
            HttpStatus status, String title, String detail, String errorCode, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setTitle(title);
        problem.setType(URI.create(
                "https://api.flashsale.com/errors/" + errorCode.toLowerCase().replace('_', '-')));
        problem.setInstance(URI.create(request.getRequestURI()));
        problem.setProperty("errorCode", errorCode);
        problem.setProperty("timestamp", Instant.now());
        return problem;
    }
}
