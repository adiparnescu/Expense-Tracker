package com.tamarwell.expense_tracker.service;

import com.tamarwell.expense_tracker.dto.TransactionDto;
import com.tamarwell.expense_tracker.entity.Category;
import com.tamarwell.expense_tracker.entity.Subcategory;
import com.tamarwell.expense_tracker.entity.Transaction;
import com.tamarwell.expense_tracker.exception.*;
import com.tamarwell.expense_tracker.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SubcategoryService subcategoryService;

    @Autowired
    private UserService userService;

    public Optional<Transaction> getTransactionById(Long id)
    {
        return repository.getTransactionById(id);
    }

    public List<Transaction> getAllTransactionsByUserId(Long userId)
    {
        return repository.getAllTransactionsByUserId(userId);
    }

    public List<Transaction> getAllTransactionsByUserIdAndCategoryId(Long userId, Long categoryId){
        return repository.getAllTransactionsByUserIdAndCategoryId(userId, categoryId);
    }

    public List<Transaction> getAllTransactionsByUserIdAndSubcategoryId(Long userId, Long subcategoryId){
        return repository.getAllTransactionsByUserIdAndSubcategoryId(userId, subcategoryId);
    }

    public List<Transaction> getAllTransactionsByDate(LocalDate date, Long userId){
        return repository.getAllTransactionsByDate(date, userId);
    }

    public List<Transaction> getAllTransactionsByTimePeriod(LocalDate startOfTimePeriod, LocalDate endOfTimePeriod, Long userId){
        return repository.getAllTransactionsByTimePeriod(startOfTimePeriod, endOfTimePeriod, userId);
    }

    @Transactional
    public void deleteTransactionById(Long id){
        repository.deleteTransactionById(id);
    }

    @Transactional
    public Transaction updateTransaction(TransactionDto transaction, Long transactionId, Long userId){
        Optional<Transaction> oldTransaction = repository.getTransactionById(transactionId);

        if(oldTransaction.isPresent())
        {
            isTransactionValid(transaction);
            Transaction transactionToUpdate = oldTransaction.get();

            if(!Objects.equals(userId, transactionToUpdate.getUser().getId()))
            {
                throw new UnauthorizedTransactionAccessException("You are not authorized to update this transaction");
            }

            Transaction updatedTransaction = convertTransactionDto(transaction);

            transactionToUpdate.setAmount(updatedTransaction.getAmount());
            transactionToUpdate.setDate(updatedTransaction.getDate());
            transactionToUpdate.setDescription(updatedTransaction.getDescription());
            transactionToUpdate.setCategory(updatedTransaction.getCategory());
            transactionToUpdate.setSubcategory(updatedTransaction.getSubcategory());
            return repository.save(transactionToUpdate);
        }
        return null;
    }

    @Transactional
    public Transaction createNewTransaction(TransactionDto newTransaction, Long userId){
        //validate amount, date, categories
        isTransactionValid(newTransaction);

        Transaction registeredTransaction = convertTransactionDto(newTransaction);

        registeredTransaction.setUser(userService.findById(userId).get());
        return repository.save(registeredTransaction);
    }

    private Transaction convertTransactionDto(TransactionDto transactionDto){
        Transaction convertedTransaction = new Transaction();

        convertedTransaction.setAmount(transactionDto.getAmount());
        convertedTransaction.setDate(transactionDto.getDate());
        convertedTransaction.setDescription(transactionDto.getDescription());

        Optional<Category> category = categoryService.findCategoryByName(transactionDto.getCategoryName());
        Optional<Subcategory> subcategory = subcategoryService.findByNameAndCategoryId(transactionDto.getSubcategoryName(), category.get().getId());

        convertedTransaction.setCategory(category.get());
        convertedTransaction.setSubcategory(subcategory.get());

        return convertedTransaction;
    }

    private boolean isTransactionValid(TransactionDto newTransaction){
        //validate amount, date, categories
        if(newTransaction.getAmount() == null){
            throw new NullTransactionAmountException("Amount should not be null");
        }

        if(newTransaction.getDate() == null){
            throw new NullTransactionDateException("Date should not be null");
        }

        if(newTransaction.getCategoryName().isEmpty()){
            throw new NullTransactionCategoryException("Category should not be null");
        }

        if(newTransaction.getSubcategoryName().isEmpty()){
            throw new NullTransactionSubcategoryException("Subcategory should not be null");
        }

    Optional<Category> newTransactionCategory = categoryService.findCategoryByName(newTransaction.getCategoryName());
        if(newTransactionCategory.isEmpty()){
            throw new NotValidTransactionCategoryException("Category should be valid");
        }

        Optional<Subcategory> newTransactionSubcategory = subcategoryService.findByNameAndCategoryId(newTransaction.getSubcategoryName(), newTransactionCategory.get().getId());
        if(newTransactionSubcategory.isEmpty()){
            throw new NotValidTransactionSubcategoryException("Subcategory should be valid");
        }

        Long newTransactionCategoryId = newTransactionCategory.get().getId();
        Long newTransactionSubcategoryId = newTransactionSubcategory.get().getCategory().getId();

        if(!Objects.equals(newTransactionCategoryId, newTransactionSubcategoryId)){
            throw new NotValidTransactionSubcategoryException(newTransactionSubcategory.get().getName() + " is not a subcategory of " + newTransactionCategory.get().getName());
        }

        return true;
    }


}
