package br.com.lapes.commerce.order;

public class IdempotencyConflictException extends RuntimeException {
  public IdempotencyConflictException() {
    super("Idempotency key belongs to a different user");
  }
}
