package com.tamarwell.expense_tracker.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TransactionController {

    @GetMapping("/transaction/create")
    public String handleTransactionRegister() {
        return "expenseRegister";
    }
}
