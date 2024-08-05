package com.tamarwell.expense_tracker.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
public class TransactionDto {

    @NotNull(message = "Amount cannot be null")
    private BigDecimal amount;

    @NotNull(message = "Date cannot be null")
    private LocalDate date;

    @NotEmpty(message = "Description cannot be empty")
    private String description;

    @NotEmpty(message = "Category should be selected")
    private String categoryName;

    @NotEmpty(message = "Subcategory should be selected")
    private String subcategoryName;
}
