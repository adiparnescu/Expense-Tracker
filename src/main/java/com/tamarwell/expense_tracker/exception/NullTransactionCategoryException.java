package com.tamarwell.expense_tracker.exception;

public class NullTransactionCategoryException extends RuntimeException{
    public NullTransactionCategoryException(String message) {
        super(message);
    }
}