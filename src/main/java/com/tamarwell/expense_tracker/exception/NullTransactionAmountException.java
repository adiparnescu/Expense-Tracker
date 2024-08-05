package com.tamarwell.expense_tracker.exception;

public class NullTransactionAmountException extends RuntimeException {
    public NullTransactionAmountException(String message) {
        super(message);
    }
}
