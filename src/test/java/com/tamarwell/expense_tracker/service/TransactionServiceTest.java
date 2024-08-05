package com.tamarwell.expense_tracker.service;

import com.tamarwell.expense_tracker.dto.TransactionDto;
import com.tamarwell.expense_tracker.entity.Category;
import com.tamarwell.expense_tracker.entity.Subcategory;
import com.tamarwell.expense_tracker.entity.Transaction;
import com.tamarwell.expense_tracker.entity.User;
import com.tamarwell.expense_tracker.exception.*;
import com.tamarwell.expense_tracker.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {
    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private SubcategoryService subcategoryService;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    private TransactionDto transactionDto;
    private Transaction transaction;
    private Category category;
    private Subcategory subcategory;

    private User user;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);

        transactionDto = new TransactionDto();
        transactionDto.setAmount(new BigDecimal("100.0"));
        transactionDto.setDate(LocalDate.now());
        transactionDto.setDescription("Test Description");
        transactionDto.setCategoryName("Food");
        transactionDto.setSubcategoryName("Groceries");

        user = new User();
        user.setId(1L);

        transaction = new Transaction();
        transaction.setAmount(new BigDecimal("100.0"));
        transaction.setDate(LocalDate.now());
        transaction.setDescription("Test Description");
        transaction.setUser(user);

        category = new Category();
        category.setId(1L);
        category.setName("Food");

        subcategory = new Subcategory();
        subcategory.setId(1L);
        subcategory.setName("Groceries");
        subcategory.setCategory(category);

        transaction.setCategory(category);
        transaction.setSubcategory(subcategory);

    }

    @Test
    public void testGetTransactionByIdFound(){
        Long transactionId = 1L;

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));

        Optional<Transaction> result = transactionService.getTransactionById(transactionId);

        assertTrue(result.isPresent());
        assertEquals(result.get(), transaction);
        verify(transactionRepository, times(1)).getTransactionById(1L);
    }

    @Test
    public void testGetTransactionByIdNotFound(){
        Long transactionId = 2L;

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.empty());

        Optional<Transaction> result = transactionService.getTransactionById(transactionId);

        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).getTransactionById(2L);
    }

    @Test
    public void testGetAllTransactionsByUserIdFound(){
        Long userId = 1L;

        List<Transaction> transactionList = new ArrayList<>();

        transactionList.add(transaction);

        when(transactionRepository.getAllTransactionsByUserId(userId)).thenReturn(transactionList);

        List<Transaction> result = transactionService.getAllTransactionsByUserId(userId);

        assertFalse(result.isEmpty());
        assertEquals(result, transactionList);
        verify(transactionRepository, times(1)).getAllTransactionsByUserId(1L);
    }

    @Test
    public void testGetAllTransactionsByUserIdNotFound(){
        Long userId = 2L;

        when(transactionRepository.getAllTransactionsByUserId(userId)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getAllTransactionsByUserId(userId);

        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).getAllTransactionsByUserId(2L);
    }

    @Test
    public void testGetAllTransactionsByUserIdAndCategoryIdFound(){
        Long userId = 1L;
        Long categoryId = 1L;

        List<Transaction> transactionList = new ArrayList<>();

        transactionList.add(transaction);

        when(transactionRepository.getAllTransactionsByUserIdAndCategoryId(userId, categoryId)).thenReturn(transactionList);

        List<Transaction> result = transactionService.getAllTransactionsByUserIdAndCategoryId(userId, categoryId);

        assertFalse(result.isEmpty());
        assertEquals(result, transactionList);
        verify(transactionRepository, times(1)).getAllTransactionsByUserIdAndCategoryId(userId, categoryId);
    }

    @Test
    public void testGetAllTransactionsByUserIdAndCategoryIdNotFound(){
        Long userId = 2L;
        Long categoryId = 2L;

        when(transactionRepository.getAllTransactionsByUserIdAndCategoryId(userId, categoryId)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getAllTransactionsByUserIdAndCategoryId(userId, categoryId);

        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).getAllTransactionsByUserIdAndCategoryId(userId, categoryId);
    }

    @Test
    public void testGetAllTransactionsByUserIdAndSubcategoryIdFound(){
        Long userId = 1L;
        Long subcategoryId = 1L;

        List<Transaction> transactionList = new ArrayList<>();

        transactionList.add(transaction);

        when(transactionRepository.getAllTransactionsByUserIdAndSubcategoryId(userId, subcategoryId)).thenReturn(transactionList);

        List<Transaction> result = transactionService.getAllTransactionsByUserIdAndSubcategoryId(userId, subcategoryId);

        assertFalse(result.isEmpty());
        assertEquals(result, transactionList);
        verify(transactionRepository, times(1)).getAllTransactionsByUserIdAndSubcategoryId(userId, subcategoryId);
    }

    @Test
    public void testGetAllTransactionsByUserIdAndSubcategoryIdNotFound(){
        Long userId = 2L;
        Long subcategoryId = 2L;

        when(transactionRepository.getAllTransactionsByUserIdAndSubcategoryId(userId, subcategoryId)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getAllTransactionsByUserIdAndSubcategoryId(userId, subcategoryId);

        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).getAllTransactionsByUserIdAndSubcategoryId(userId, subcategoryId);
    }

    @Test
    public void testGetAllTransactionsByDate(){
        LocalDate date = LocalDate.now();
        Long userId = 1L;

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);

        when(transactionRepository.getAllTransactionsByDate(date, userId)).thenReturn(transactionList);

        List<Transaction> result = transactionService.getAllTransactionsByDate(date, userId);

        assertFalse(result.isEmpty());
        assertEquals(result, transactionList);
        verify(transactionRepository, times(1)).getAllTransactionsByDate(date, userId);
    }

    @Test
    public void testGetAllTransactionsByDateNotFound(){
        LocalDate date = LocalDate.now();
        Long userId = 1L;

        when(transactionRepository.getAllTransactionsByDate(date, userId)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getAllTransactionsByDate(date, userId);

        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).getAllTransactionsByDate(date, userId);
    }


    @Test
    public void testGetAllTransactionsByTimePeriodFound(){
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        Long userId = 1L;

        List<Transaction> transactionList = new ArrayList<>();
        transactionList.add(transaction);

        when(transactionRepository.getAllTransactionsByTimePeriod(startDate, endDate, userId)).thenReturn(transactionList);

        List<Transaction> result = transactionService.getAllTransactionsByTimePeriod(startDate, endDate, userId);

        assertFalse(result.isEmpty());
        assertEquals(result, transactionList);
        verify(transactionRepository, times(1)).getAllTransactionsByTimePeriod(startDate, endDate, userId);
    }

    @Test
    public void testGetAllTransactionsByTimePeriodNotFound(){
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(1);
        Long userId = 1L;


        when(transactionRepository.getAllTransactionsByTimePeriod(startDate, endDate, userId)).thenReturn(Collections.emptyList());

        List<Transaction> result = transactionService.getAllTransactionsByTimePeriod(startDate, endDate, userId);

        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).getAllTransactionsByTimePeriod(startDate, endDate, userId);
    }

    @Test
    public void testDeleteTransactionById(){
        Long transactionId = 1L;

        transactionService.deleteTransactionById(transactionId);

        verify(transactionRepository, times(1)).deleteTransactionById(1L);
    }

    @Test
    public void testUpdateTransactionSuccess(){
        Long userId = 1L;
        Long transactionId = 1L;

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));
        when(categoryService.findCategoryByName(transactionDto.getCategoryName())).thenReturn(Optional.of(category));
        when(subcategoryService.findByNameAndCategoryId(transactionDto.getSubcategoryName(), category.getId())).thenReturn(Optional.of(subcategory));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.updateTransaction(transactionDto, transactionId, userId);

        assertNotNull(result);
        assertEquals(transactionDto.getDescription(), result.getDescription());
        assertEquals(transactionDto.getAmount(), result.getAmount());
        assertEquals(transactionDto.getDate(), result.getDate());
        assertEquals(transactionDto.getCategoryName(), result.getCategory().getName());
        assertEquals(transactionDto.getSubcategoryName(), result.getSubcategory().getName());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testUpdateNotExistingTransaction(){
        Long userId = 1L;
        Long transactionId = 1L;

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.empty());

        Transaction result = transactionService.updateTransaction(transactionDto, transactionId, userId);

        assertNull(result);
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    public void testUpdateTransactionWithNullAmount(){
        Long transactionId = 1L;

        TransactionDto invalidTransactionDto = new TransactionDto();
        invalidTransactionDto.setDate(LocalDate.now());
        invalidTransactionDto.setDescription("Test Description");
        invalidTransactionDto.setCategoryName("Food");
        invalidTransactionDto.setSubcategoryName("Groceries");

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));

        verify(transactionRepository, never()).save(any(Transaction.class));
        assertThrows(NullTransactionAmountException.class, () ->
                transactionService.updateTransaction(invalidTransactionDto, 1L, 1L)
        );
    }

    @Test
    public void testUpdateTransactionWithNullDate(){
        Long transactionId = 1L;

        TransactionDto invalidTransactionDto = new TransactionDto();
        invalidTransactionDto.setAmount(new BigDecimal("100.00"));
        invalidTransactionDto.setDescription("Test Description");
        invalidTransactionDto.setCategoryName("Food");
        invalidTransactionDto.setSubcategoryName("Groceries");

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));

        verify(transactionRepository, never()).save(any(Transaction.class));
        assertThrows(NullTransactionDateException.class, () ->
                transactionService.updateTransaction(invalidTransactionDto, 1L, 1L)
        );
    }

    @Test
    public void testUpdateTransactionWithNullCategory(){
        Long transactionId = 1L;

        TransactionDto invalidTransactionDto = new TransactionDto();
        invalidTransactionDto.setAmount(new BigDecimal("100.00"));
        invalidTransactionDto.setDate(LocalDate.now());
        invalidTransactionDto.setDescription("Test Description");
        invalidTransactionDto.setCategoryName("");
        invalidTransactionDto.setSubcategoryName("Groceries");

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));

        verify(transactionRepository, never()).save(any(Transaction.class));
        assertThrows(NullTransactionCategoryException.class, () ->
                transactionService.updateTransaction(invalidTransactionDto, 1L, 1L)
        );
    }

    @Test
    public void testUpdateTransactionWithNullSubcategory(){
        Long transactionId = 1L;

        TransactionDto invalidTransactionDto = new TransactionDto();
        invalidTransactionDto.setAmount(new BigDecimal("100.00"));
        invalidTransactionDto.setDate(LocalDate.now());
        invalidTransactionDto.setDescription("Test Description");
        invalidTransactionDto.setCategoryName("Food");
        invalidTransactionDto.setSubcategoryName("");

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));

        verify(transactionRepository, never()).save(any(Transaction.class));
        assertThrows(NullTransactionSubcategoryException.class, () ->
                transactionService.updateTransaction(invalidTransactionDto, 1L, 1L)
        );
    }

    @Test
    public void testUpdateTransactionWithInvalidCategory(){
        Long transactionId = 1L;

        TransactionDto invalidTransactionDto = new TransactionDto();
        invalidTransactionDto.setAmount(new BigDecimal("100.00"));
        invalidTransactionDto.setDate(LocalDate.now());
        invalidTransactionDto.setDescription("Test Description");
        invalidTransactionDto.setCategoryName("Invalid Category");
        invalidTransactionDto.setSubcategoryName("Groceries");

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));
        when(categoryService.findCategoryByName(invalidTransactionDto.getCategoryName())).thenReturn(Optional.empty());

        verify(transactionRepository, never()).save(any(Transaction.class));
        assertThrows(NotValidTransactionCategoryException.class, () ->
                transactionService.updateTransaction(invalidTransactionDto, 1L, 1L)
        );
    }

    @Test
    public void testUpdateTransactionWithInvalidSubcategory(){
        Long transactionId = 1L;

        TransactionDto invalidTransactionDto = new TransactionDto();
        invalidTransactionDto.setAmount(new BigDecimal("100.00"));
        invalidTransactionDto.setDate(LocalDate.now());
        invalidTransactionDto.setDescription("Test Description");
        invalidTransactionDto.setCategoryName("Food");
        invalidTransactionDto.setSubcategoryName("Invalid Subcategory");

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));
        when(categoryService.findCategoryByName(invalidTransactionDto.getCategoryName())).thenReturn(Optional.of(category));
        when(subcategoryService.findByNameAndCategoryId(invalidTransactionDto.getSubcategoryName(), category.getId())).thenReturn(Optional.empty());

        verify(transactionRepository, never()).save(any(Transaction.class));
        NotValidTransactionSubcategoryException exception = assertThrows(NotValidTransactionSubcategoryException.class, () ->
                transactionService.updateTransaction(invalidTransactionDto, 1L, 1L)
        );
        assertEquals("Subcategory should be valid", exception.getMessage());
    }

    @Test
    public void testUpdateTransactionWithInvalidCategoryAndSubcategory(){
        Long transactionId = 1L;

        TransactionDto invalidTransactionDto = new TransactionDto();
        invalidTransactionDto.setAmount(new BigDecimal("100.00"));
        invalidTransactionDto.setDate(LocalDate.now());
        invalidTransactionDto.setDescription("Test Description");
        invalidTransactionDto.setCategoryName("Invalid Category");
        invalidTransactionDto.setSubcategoryName("Invalid Subcategory");

        Category invalidCategory = new Category();
        invalidCategory.setId(2L);
        invalidCategory.setName("Invalid Category");

        Subcategory invalidSubcategory = new Subcategory();
        invalidSubcategory.setId(2L);
        invalidSubcategory.setCategory(category);
        invalidSubcategory.setName("Invalid Subcategory");

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));
        when(categoryService.findCategoryByName(invalidTransactionDto.getCategoryName())).thenReturn(Optional.of(invalidCategory));
        when(subcategoryService.findByNameAndCategoryId(invalidTransactionDto.getSubcategoryName(), invalidCategory.getId())).thenReturn(Optional.of(invalidSubcategory));

        verify(transactionRepository, never()).save(any(Transaction.class));
        NotValidTransactionSubcategoryException exception = assertThrows(NotValidTransactionSubcategoryException.class, () ->
                transactionService.updateTransaction(invalidTransactionDto, 1L, 1L)
        );
        assertEquals("Invalid Subcategory is not a subcategory of Invalid Category", exception.getMessage());
    }

    @Test
    public void testUpdateTransactionUnauthorizedUser(){
        Long userId = 2L;
        Long transactionId = 1L;

        when(transactionRepository.getTransactionById(transactionId)).thenReturn(Optional.of(transaction));
        when(categoryService.findCategoryByName(transactionDto.getCategoryName())).thenReturn(Optional.of(category));
        when(subcategoryService.findByNameAndCategoryId(transactionDto.getSubcategoryName(), category.getId())).thenReturn(Optional.of(subcategory));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        UnauthorizedTransactionAccessException exception = assertThrows(UnauthorizedTransactionAccessException.class, () ->
                transactionService.updateTransaction(transactionDto, transactionId, userId)
        );
        assertEquals("You are not authorized to update this transaction", exception.getMessage());
    }

    @Test
    public void testCreateTransactionSuccess(){
        Long userId = 1L;

        when(categoryService.findCategoryByName(transactionDto.getCategoryName())).thenReturn(Optional.of(category));
        when(subcategoryService.findByNameAndCategoryId(transactionDto.getSubcategoryName(), category.getId())).thenReturn(Optional.of(subcategory));
        when(userService.findById(userId)).thenReturn(Optional.of(user));
        when(transactionRepository.save(transaction)).thenReturn(transaction);

        Transaction result = transactionService.createNewTransaction(transactionDto, userId);

        assertEquals(transaction, result);
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }
}
