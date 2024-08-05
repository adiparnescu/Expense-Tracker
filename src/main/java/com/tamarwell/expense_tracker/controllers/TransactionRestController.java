package com.tamarwell.expense_tracker.controllers;

import com.tamarwell.expense_tracker.dto.TransactionDto;
import com.tamarwell.expense_tracker.entity.Transaction;
import com.tamarwell.expense_tracker.exception.*;
import com.tamarwell.expense_tracker.service.CategoryService;
import com.tamarwell.expense_tracker.service.SubcategoryService;
import com.tamarwell.expense_tracker.service.TransactionService;
import com.tamarwell.expense_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("api/user/transactions")
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubcategoryService subcategoryService;

    @Autowired
    private UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> getAllTransactionsForAUser() {
        Long authenticatedUserId = getAuthenticatedUserId();
        List<Transaction> returnedTransaction = transactionService.getAllTransactionsByUserId(authenticatedUserId);
        return ResponseEntity.ok(returnedTransaction);
    }

    @PostMapping("/")
    public ResponseEntity<?> createTransaction(@RequestBody @Valid TransactionDto transactionDto) {
        try {
            Long authenticatedUserId = getAuthenticatedUserId();
            Transaction createdTransaction = transactionService.createNewTransaction(transactionDto, authenticatedUserId);
            return ResponseEntity.ok(createdTransaction);
        } catch (NullTransactionAmountException | NullTransactionDateException | NullTransactionCategoryException |
                 NullTransactionSubcategoryException | NotValidTransactionCategoryException |
                 NotValidTransactionSubcategoryException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransactionById(@PathVariable Long id) {
        Optional<Transaction> returnedTransaction = transactionService.getTransactionById(id);
        if (returnedTransaction.isPresent()) {
            return ResponseEntity.ok(returnedTransaction.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Transaction not found!");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTransaction(@RequestBody @Valid TransactionDto transactionDto, @PathVariable Long id){
        try{
            Long authenticatedUserId = getAuthenticatedUserId();
            Transaction updatedTransaction = transactionService.updateTransaction(transactionDto, id, authenticatedUserId);
            return ResponseEntity.ok(updatedTransaction);
        }catch (NullTransactionAmountException | NullTransactionDateException | NullTransactionCategoryException |
                NullTransactionSubcategoryException | NotValidTransactionCategoryException |
                NotValidTransactionSubcategoryException e){
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
        catch (UnauthorizedTransactionAccessException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "You are not authorized to update this transaction");
            return ResponseEntity.status(401).body(response);
        }
        catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "An unexpected error occurred");
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTransactionById(@PathVariable Long id){
        try{
            Long authenticatedUserId = getAuthenticatedUserId();
            Optional<Transaction> selectedTransaction = transactionService.getTransactionById(id);
            if(selectedTransaction.isEmpty()){
                Map<String, String> response = new HashMap<>();
                response.put("error", "Transaction not found!");
                return ResponseEntity.status(404).body(response);
            }

            if(Objects.equals(authenticatedUserId, selectedTransaction.get().getUser().getId())) {
                transactionService.deleteTransactionById(id);
                return ResponseEntity.ok("Expense deleted");
            }
            Map<String, String> response = new HashMap<>();
            response.put("error", "You are not authorized to delete this expense");
            return ResponseEntity.status(401).body(response);
        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }


    }

    @GetMapping("/category")
    public ResponseEntity<?> getAllTransactionsByCategory(@RequestParam String categoryName){
        Long authenticatedUserId = getAuthenticatedUserId();
        Long categoryId = categoryService.findCategoryByName(categoryName).get().getId();
        List<Transaction> returnedTransactions = transactionService.getAllTransactionsByUserIdAndCategoryId(authenticatedUserId,categoryId);
        return ResponseEntity.ok(returnedTransactions);
    }

    @GetMapping("/subcategory")
    public ResponseEntity<?> getAllTransactionsBySubcategory(@RequestParam String categoryName, @RequestParam String subcategoryName){
        Long authenticatedUserId = getAuthenticatedUserId();
        Long categoryId = categoryService.findCategoryByName(categoryName).get().getId();
        Long subcategoryId = subcategoryService.findByNameAndCategoryId(subcategoryName, categoryId).get().getId();
        List<Transaction> returnedTransactions = transactionService.getAllTransactionsByUserIdAndSubcategoryId(authenticatedUserId, subcategoryId);
        return ResponseEntity.ok(returnedTransactions);
    }

    @GetMapping("/date")
    public ResponseEntity<?> getAllTransactionsByDate(@RequestParam LocalDate date){
        Long authenticatedUserId = getAuthenticatedUserId();
        List<Transaction> returnedTransactions = transactionService.getAllTransactionsByDate(date, authenticatedUserId);
        return ResponseEntity.ok(returnedTransactions);
    }

    @GetMapping("/date-period")
    public ResponseEntity<?> getAllTransactionsByDatePeriod(@RequestParam LocalDate startOfTimePeriod, @RequestParam LocalDate endOfTimePeriod){
        Long authenticatedUserId = getAuthenticatedUserId();
        List<Transaction> returnedTransactions = transactionService.getAllTransactionsByTimePeriod(startOfTimePeriod, endOfTimePeriod, authenticatedUserId);
        return ResponseEntity.ok(returnedTransactions);
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            if (userDetails.getUsername().isEmpty())
                throw new UsernameNotFoundException("Authenticated user not found");
            else
                return userService.findByUsername(userDetails.getUsername()).get().getId();
        }
        return null;
    }
}

