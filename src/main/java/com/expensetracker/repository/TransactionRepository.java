package com.expensetracker.repository;

import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByDateDesc(User user);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
           "AND MONTH(t.date) = :month AND YEAR(t.date) = :year ORDER BY t.date DESC")
    List<Transaction> findByUserAndMonthAndYear(@Param("user") User user,
                                                @Param("month") int month,
                                                @Param("year") int year);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user " +
           "AND t.type = 'INCOME' AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    BigDecimal sumIncomeByUserAndMonthAndYear(@Param("user") User user,
                                              @Param("month") int month,
                                              @Param("year") int year);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user " +
           "AND t.type = 'EXPENSE' AND MONTH(t.date) = :month AND YEAR(t.date) = :year")
    BigDecimal sumExpenseByUserAndMonthAndYear(@Param("user") User user,
                                               @Param("month") int month,
                                               @Param("year") int year);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = 'INCOME'")
    BigDecimal sumTotalIncomeByUser(@Param("user") User user);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.user = :user AND t.type = 'EXPENSE'")
    BigDecimal sumTotalExpenseByUser(@Param("user") User user);

    Optional<Transaction> findByIdAndUser(Long id, User user);

    @Query("SELECT t FROM Transaction t WHERE t.user = :user ORDER BY t.date DESC LIMIT 5")
    List<Transaction> findTop5ByUserOrderByDateDesc(@Param("user") User user);
}
