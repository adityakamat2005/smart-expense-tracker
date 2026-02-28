# 💰 Smart Expense Tracker

A production-ready personal finance tracker built with Spring Boot, Thymeleaf, and PostgreSQL. Track your income and expenses with a clean, modern dashboard.

---

## ✨ Features

- **Authentication**: Register/Login with BCrypt-encrypted passwords
- **Dashboard**: Monthly income/expense overview with Chart.js doughnut chart
- **Transactions**: Add, edit, and delete income/expense transactions
- **Categories**: 21 predefined categories (auto-seeded on startup)
- **Responsive UI**: Bootstrap 5 sidebar layout, mobile-friendly

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2, Spring Security, Spring Data JPA |
| Database | PostgreSQL |
| Frontend | Thymeleaf, Bootstrap 5, Chart.js |
| Build | Maven |

---

## 🚀 Local Setup

### Prerequisites
- Java 17+
- PostgreSQL 14+
- Maven 3.8+

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/smart-expense-tracker.git
cd smart-expense-tracker
```

### 2. Create the Database
```sql
CREATE DATABASE expense_tracker;
```

### 3. Configure Environment Variables
Copy `application.properties` and set your values:
```bash
export DATABASE_URL=jdbc:postgresql://localhost:5432/expense_tracker
export DATABASE_USERNAME=postgres
export DATABASE_PASSWORD=yourpassword
```

Or edit `src/main/resources/application.properties` directly.

### 4. Build and Run
```bash
mvn clean package -DskipTests
java -jar target/smart-expense-tracker-1.0.0.jar
```

### 5. Access the App
Open: `http://localhost:8080`

---

## ☁️ Railway Deployment

### Step 1: Push to GitHub
```bash
git init
git add .
git commit -m "Initial commit"
git remote add origin https://github.com/YOUR_USERNAME/smart-expense-tracker.git
git push -u origin main
```

### Step 2: Create Railway Project
1. Go to [railway.app](https://railway.app) and sign in
2. Click **New Project → Deploy from GitHub Repo**
3. Select your repository

### Step 3: Add PostgreSQL
1. In your Railway project, click **+ New → Database → Add PostgreSQL**
2. Railway will automatically provision a PostgreSQL instance

### Step 4: Set Environment Variables
In Railway project settings → Variables, add:

| Variable | Value |
|----------|-------|
| `DATABASE_URL` | `${{Postgres.DATABASE_URL}}` (Railway auto-fills) |
| `DATABASE_USERNAME` | `${{Postgres.PGUSER}}` |
| `DATABASE_PASSWORD` | `${{Postgres.PGPASSWORD}}` |
| `PORT` | `8080` |

> **Tip**: Railway's PostgreSQL plugin provides a `DATABASE_URL` in JDBC format. Use `${{Postgres.JDBC_URL}}` for the DATABASE_URL variable.

### Step 5: Deploy
Railway automatically detects the Maven project and builds it. Your app will be live at the generated Railway URL.

---

## 📁 Project Structure

```
src/main/java/com/expensetracker/
├── SmartExpenseTrackerApplication.java
├── config/
│   ├── DataSeeder.java          # Auto-seeds categories
│   ├── GlobalExceptionHandler.java
│   └── SecurityConfig.java
├── controller/
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── HomeController.java
│   └── TransactionController.java
├── dto/
│   ├── DashboardDto.java
│   ├── RegisterDto.java
│   └── TransactionDto.java
├── entity/
│   ├── Category.java
│   ├── Transaction.java
│   └── User.java
├── repository/
│   ├── CategoryRepository.java
│   ├── TransactionRepository.java
│   └── UserRepository.java
├── security/
│   ├── CustomUserDetails.java
│   └── CustomUserDetailsService.java
└── service/
    ├── CategoryService.java
    ├── TransactionService.java
    └── UserService.java

src/main/resources/
├── application.properties
├── static/
│   ├── css/style.css
│   └── js/app.js
└── templates/
    ├── auth/
    │   ├── login.html
    │   └── register.html
    ├── dashboard/
    │   └── index.html
    ├── fragments/
    │   ├── navbar.html
    │   └── sidebar.html
    ├── transaction/
    │   ├── form.html
    │   └── list.html
    └── error.html
```

---

## 🔐 Security Notes

- Passwords are hashed with BCrypt (strength 12)
- CSRF protection enabled on all forms
- Users can only access their own transactions
- No hardcoded credentials — use environment variables

---

## 📝 Password Policy

Passwords must meet:
- Minimum 8 characters
- At least 1 uppercase letter
- At least 1 number

---

## 🛠️ Categories

**Income**: Salary, Freelance, Business, Investment, Rental Income, Bonus, Gift, Other Income

**Expense**: Housing, Food & Dining, Transportation, Healthcare, Entertainment, Shopping, Education, Utilities, Travel, Personal Care, Insurance, Subscriptions, Other Expense

Categories are auto-inserted on first run and never deleted.

---

## 📄 License

MIT License — free to use and modify.
