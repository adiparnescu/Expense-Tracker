package com.tamarwell.expense_tracker.exception;

public class NotValidTransactionSubcategoryException extends RuntimeException{
    public NotValidTransactionSubcategoryException(String message) {
        super(message);
    }
}