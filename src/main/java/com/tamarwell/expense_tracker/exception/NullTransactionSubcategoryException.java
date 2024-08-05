package com.tamarwell.expense_tracker.exception;

public class NullTransactionSubcategoryException extends RuntimeException{
    public NullTransactionSubcategoryException(String message) {
        super(message);
    }
}