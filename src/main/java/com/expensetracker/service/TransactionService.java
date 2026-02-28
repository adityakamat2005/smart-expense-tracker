package com.expensetracker.service;

import com.expensetracker.dto.TransactionDto;
import com.expensetracker.entity.Category;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryService categoryService;

    @Transactional
    public Transaction createTransaction(TransactionDto dto, User user) {
        Category category = categoryService.findById(dto.getCategoryId());

        // Ensure type matches category type
        if (!category.getType().name().equals(dto.getType().name())) {
            throw new IllegalArgumentException("Transaction type does not match category type.");
        }

        Transaction transaction = Transaction.builder()
            .amount(dto.getAmount())
            .description(dto.getDescription())
            .date(dto.getDate())
            .type(dto.getType())
            .category(category)
            .user(user)
            .build();

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction updateTransaction(Long id, TransactionDto dto, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found or access denied."));

        Category category = categoryService.findById(dto.getCategoryId());

        if (!category.getType().name().equals(dto.getType().name())) {
            throw new IllegalArgumentException("Transaction type does not match category type.");
        }

        transaction.setAmount(dto.getAmount());
        transaction.setDescription(dto.getDescription());
        transaction.setDate(dto.getDate());
        transaction.setType(dto.getType());
        transaction.setCategory(category);

        return transactionRepository.save(transaction);
    }

    @Transactional
    public void deleteTransaction(Long id, User user) {
        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found or access denied."));
        transactionRepository.delete(transaction);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions(User user) {
        return transactionRepository.findByUserOrderByDateDesc(user);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByMonthYear(User user, int month, int year) {
        return transactionRepository.findByUserAndMonthAndYear(user, month, year);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getRecentTransactions(User user) {
        return transactionRepository.findTop5ByUserOrderByDateDesc(user);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalIncome(User user) {
        return transactionRepository.sumTotalIncomeByUser(user);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalExpense(User user) {
        return transactionRepository.sumTotalExpenseByUser(user);
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyIncome(User user, int month, int year) {
        return transactionRepository.sumIncomeByUserAndMonthAndYear(user, month, year);
    }

    @Transactional(readOnly = true)
    public BigDecimal getMonthlyExpense(User user, int month, int year) {
        return transactionRepository.sumExpenseByUserAndMonthAndYear(user, month, year);
    }

    @Transactional(readOnly = true)
    public Transaction findByIdAndUser(Long id, User user) {
        return transactionRepository.findByIdAndUser(id, user)
            .orElseThrow(() -> new IllegalArgumentException("Transaction not found or access denied."));
    }
}
