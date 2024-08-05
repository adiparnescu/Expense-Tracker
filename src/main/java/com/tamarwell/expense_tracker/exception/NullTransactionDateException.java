package com.tamarwell.expense_tracker.exception;

public class NullTransactionDateException extends RuntimeException {
    public NullTransactionDateException(String message) {
        super(message);
    }
}

