package com.expensetracker.controller;

import com.expensetracker.dto.TransactionDto;
import com.expensetracker.entity.Transaction;
import com.expensetracker.entity.User;
import com.expensetracker.security.CustomUserDetails;
import com.expensetracker.service.CategoryService;
import com.expensetracker.service.TransactionService;
import com.expensetracker.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public String listTransactions(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   HttpServletRequest request,
                                   Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        List<Transaction> transactions = transactionService.getAllTransactions(user);
        model.addAttribute("transactions", transactions);
        model.addAttribute("currentUri", request.getRequestURI());
        return "transaction/list";
    }

    @GetMapping("/add")
    public String addTransactionPage(HttpServletRequest request, Model model) {
        TransactionDto dto = new TransactionDto();
        dto.setDate(LocalDate.now());
        model.addAttribute("transactionDto", dto);
        model.addAttribute("incomeCategories", categoryService.getIncomeCategories());
        model.addAttribute("expenseCategories", categoryService.getExpenseCategories());
        model.addAttribute("currentUri", request.getRequestURI());
        return "transaction/form";
    }

    @PostMapping("/add")
    public String addTransaction(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @Valid @ModelAttribute("transactionDto") TransactionDto dto,
                                 BindingResult bindingResult,
                                 HttpServletRequest request,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("incomeCategories", categoryService.getIncomeCategories());
            model.addAttribute("expenseCategories", categoryService.getExpenseCategories());
            model.addAttribute("currentUri", request.getRequestURI());
            return "transaction/form";
        }

        try {
            User user = userService.findByUsername(userDetails.getUsername());
            transactionService.createTransaction(dto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction added successfully!");
            return "redirect:/transactions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("incomeCategories", categoryService.getIncomeCategories());
            model.addAttribute("expenseCategories", categoryService.getExpenseCategories());
            model.addAttribute("currentUri", request.getRequestURI());
            return "transaction/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String editTransactionPage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                      @PathVariable Long id,
                                      HttpServletRequest request,
                                      Model model) {
        User user = userService.findByUsername(userDetails.getUsername());
        Transaction transaction = transactionService.findByIdAndUser(id, user);

        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setDescription(transaction.getDescription());
        dto.setDate(transaction.getDate());
        dto.setType(transaction.getType());
        dto.setCategoryId(transaction.getCategory().getId());

        model.addAttribute("transactionDto", dto);
        model.addAttribute("incomeCategories", categoryService.getIncomeCategories());
        model.addAttribute("expenseCategories", categoryService.getExpenseCategories());
        model.addAttribute("isEdit", true);
        model.addAttribute("currentUri", request.getRequestURI());
        return "transaction/form";
    }

    @PostMapping("/edit/{id}")
    public String editTransaction(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable Long id,
                                  @Valid @ModelAttribute("transactionDto") TransactionDto dto,
                                  BindingResult bindingResult,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("incomeCategories", categoryService.getIncomeCategories());
            model.addAttribute("expenseCategories", categoryService.getExpenseCategories());
            model.addAttribute("isEdit", true);
            model.addAttribute("currentUri", request.getRequestURI());
            return "transaction/form";
        }

        try {
            User user = userService.findByUsername(userDetails.getUsername());
            transactionService.updateTransaction(id, dto, user);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction updated successfully!");
            return "redirect:/transactions";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("incomeCategories", categoryService.getIncomeCategories());
            model.addAttribute("expenseCategories", categoryService.getExpenseCategories());
            model.addAttribute("isEdit", true);
            model.addAttribute("currentUri", request.getRequestURI());
            return "transaction/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteTransaction(@AuthenticationPrincipal CustomUserDetails userDetails,
                                    @PathVariable Long id,
                                    RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUsername());
            transactionService.deleteTransaction(id, user);
            redirectAttributes.addFlashAttribute("successMessage", "Transaction deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/transactions";
    }
}