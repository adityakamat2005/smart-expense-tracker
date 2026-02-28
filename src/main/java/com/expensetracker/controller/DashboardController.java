package com.expensetracker.controller;

import com.expensetracker.dto.DashboardDto;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.security.CustomUserDetails;
import com.expensetracker.service.TransactionService;
import com.expensetracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final TransactionService transactionService;
    private final UserService userService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @RequestParam(required = false) Integer month,
                            @RequestParam(required = false) Integer year,
                            HttpServletRequest request,
                            Model model) {

        User user = userService.findByUsername(userDetails.getUsername());

        LocalDate now = LocalDate.now();
        int selectedMonth = (month != null) ? month : now.getMonthValue();
        int selectedYear = (year != null) ? year : now.getYear();

        BigDecimal totalIncome = transactionService.getMonthlyIncome(user, selectedMonth, selectedYear);
        BigDecimal totalExpense = transactionService.getMonthlyExpense(user, selectedMonth, selectedYear);
        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        List<Transaction> recentTransactions = transactionService.getTransactionsByMonthYear(user, selectedMonth, selectedYear);

        DashboardDto dashboard = DashboardDto.builder()
                .username(user.getUsername())
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .recentTransactions(recentTransactions)
                .selectedMonth(selectedMonth)
                .selectedYear(selectedYear)
                .build();

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("currentYear", now.getYear());
        model.addAttribute("currentUri", request.getRequestURI());

        return "dashboard/index";
    }
}