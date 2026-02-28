package com.expensetracker.dto;

import com.expensetracker.entity.Transaction;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class DashboardDto {
    private String username;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;
    private List<Transaction> recentTransactions;
    private int selectedMonth;
    private int selectedYear;
}
