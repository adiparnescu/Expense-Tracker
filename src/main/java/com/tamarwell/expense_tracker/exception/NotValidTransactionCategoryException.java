package com.tamarwell.expense_tracker.exception;

public class NotValidTransactionCategoryException extends RuntimeException{
    public NotValidTransactionCategoryException(String message) {
        super(message);
    }
}