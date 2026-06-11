package br.com.lapes.commerce.common;

import br.com.lapes.commerce.auth.EmailAlreadyRegisteredException;
import br.com.lapes.commerce.auth.InvalidCredentialsException;
import br.com.lapes.commerce.cart.CartItemNotFoundException;
import br.com.lapes.commerce.cart.CartNotFoundException;
import br.com.lapes.commerce.cart.InsufficientStockException;
import br.com.lapes.commerce.order.EmptyCartException;
import br.com.lapes.commerce.order.IdempotencyConflictException;
import br.com.lapes.commerce.order.InvalidCouponException;
import br.com.lapes.commerce.order.InvalidOrderTransitionException;
import br.com.lapes.commerce.order.OrderCancellationNotAllowedException;
import br.com.lapes.commerce.order.OrderNotFoundException;
import br.com.lapes.commerce.payment.InvalidPaymentWebhookException;
import br.com.lapes.commerce.payment.PaymentGatewayException;
import br.com.lapes.commerce.product.ProductNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiError> handleValidation(
      MethodArgumentNotValidException exception, HttpServletRequest request) {
    Map<String, String> fields = new LinkedHashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(error -> fields.put(error.getField(), error.getDefaultMessage()));

    return ResponseEntity.badRequest()
        .body(ApiError.validation("Invalid request body", request.getRequestURI(), fields));
  }

  @ExceptionHandler({
    MethodArgumentTypeMismatchException.class,
    MissingServletRequestParameterException.class,
    HttpMessageNotReadableException.class
  })
  public ResponseEntity<ApiError> handleBadRequest(Exception exception, HttpServletRequest request) {
    return ResponseEntity.badRequest()
        .body(ApiError.of(400, "Bad Request", "Invalid request parameters", request.getRequestURI()));
  }

  @ExceptionHandler(EmailAlreadyRegisteredException.class)
  public ResponseEntity<ApiError> handleEmailAlreadyRegistered(
      EmailAlreadyRegisteredException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiError.of(409, "Conflict", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<ApiError> handleInvalidCredentials(
      InvalidCredentialsException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiError.of(401, "Unauthorized", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ApiError> handleProductNotFound(
      ProductNotFoundException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.of(404, "Not Found", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler({CartNotFoundException.class, CartItemNotFoundException.class})
  public ResponseEntity<ApiError> handleCartNotFound(RuntimeException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.of(404, "Not Found", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(OrderNotFoundException.class)
  public ResponseEntity<ApiError> handleOrderNotFound(
      OrderNotFoundException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ApiError.of(404, "Not Found", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(InsufficientStockException.class)
  public ResponseEntity<ApiError> handleInsufficientStock(
      InsufficientStockException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiError.of(422, "Unprocessable Entity", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler({EmptyCartException.class, InvalidCouponException.class, InvalidOrderTransitionException.class})
  public ResponseEntity<ApiError> handleBusinessRule(RuntimeException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
        .body(ApiError.of(422, "Unprocessable Entity", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(IdempotencyConflictException.class)
  public ResponseEntity<ApiError> handleIdempotencyConflict(
      IdempotencyConflictException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiError.of(409, "Conflict", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(OrderCancellationNotAllowedException.class)
  public ResponseEntity<ApiError> handleCancellationNotAllowed(
      OrderCancellationNotAllowedException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .body(ApiError.of(409, "Conflict", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(InvalidPaymentWebhookException.class)
  public ResponseEntity<ApiError> handleInvalidPaymentWebhook(
      InvalidPaymentWebhookException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(ApiError.of(401, "Unauthorized", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(PaymentGatewayException.class)
  public ResponseEntity<ApiError> handlePaymentGateway(
      PaymentGatewayException exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .body(ApiError.of(502, "Bad Gateway", exception.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiError> handleUnexpected(Exception exception, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(ApiError.of(500, "Internal Server Error", "Unexpected server error", request.getRequestURI()));
  }
}
