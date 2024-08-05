package com.tamarwell.expense_tracker.repository;

import com.tamarwell.expense_tracker.entity.Transaction;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> getTransactionById(Long id);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId")
    List<Transaction> getAllTransactionsByUserId(@Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId")
    List<Transaction> getAllTransactionsByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.subcategory.id = :subcategoryId")
    List<Transaction> getAllTransactionsByUserIdAndSubcategoryId(@Param("userId") Long userId, @Param("subcategoryId") Long subcategoryId);

    @Query("SELECT t FROM Transaction t WHERE t.date = :date AND t.user.id = :userId")
    List<Transaction> getAllTransactionsByDate(@Param("date") LocalDate date, @Param("userId") Long userId);

    @Query("SELECT t FROM Transaction t WHERE t.user.id = :userId AND t.date BETWEEN :startOfTimePeriod AND :endOfTimePeriod")
    List<Transaction> getAllTransactionsByTimePeriod(@Param("startOfTimePeriod") LocalDate startOfTimePeriod, @Param("endOfTimePeriod") LocalDate endOfTimePeriod, @Param("userId") Long userId);


    @Modifying
    @Transactional
    void deleteTransactionById(Long id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.user.id = :userId AND t.category.id = :categoryId")
    List<Transaction> deleteAllTransactionsByUserIdAndCategoryId(@Param("userId") Long userId, @Param("categoryId") Long categoryId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Transaction t WHERE t.user.id = :userId AND t.subcategory.id = :subcategoryId")
    List<Transaction> deleteAllTransactionsByUserIdAndSubcategoryId(@Param("userId") Long userId, @Param("subcategoryId") Long subcategoryId);
}
