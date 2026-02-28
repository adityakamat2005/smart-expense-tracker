package com.expensetracker.config;

import com.expensetracker.entity.Category;
import com.expensetracker.entity.Category.CategoryType;
import com.expensetracker.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    private static final List<String[]> INCOME_CATEGORIES = List.of(
        new String[]{"Salary"},
        new String[]{"Freelance"},
        new String[]{"Business"},
        new String[]{"Investment"},
        new String[]{"Rental Income"},
        new String[]{"Bonus"},
        new String[]{"Gift"},
        new String[]{"Other Income"}
    );

    private static final List<String[]> EXPENSE_CATEGORIES = List.of(
        new String[]{"Housing"},
        new String[]{"Food & Dining"},
        new String[]{"Transportation"},
        new String[]{"Healthcare"},
        new String[]{"Entertainment"},
        new String[]{"Shopping"},
        new String[]{"Education"},
        new String[]{"Utilities"},
        new String[]{"Travel"},
        new String[]{"Personal Care"},
        new String[]{"Insurance"},
        new String[]{"Subscriptions"},
        new String[]{"Other Expense"}
    );

    @Override
    public void run(String... args) {
        seedCategories(INCOME_CATEGORIES, CategoryType.INCOME);
        seedCategories(EXPENSE_CATEGORIES, CategoryType.EXPENSE);
        log.info("Category seeding complete.");
    }

    private void seedCategories(List<String[]> categories, CategoryType type) {
        for (String[] cat : categories) {
            String name = cat[0];
            if (!categoryRepository.existsByName(name)) {
                categoryRepository.save(Category.builder()
                    .name(name)
                    .type(type)
                    .build());
                log.info("Seeded category: {} [{}]", name, type);
            }
        }
    }
}
