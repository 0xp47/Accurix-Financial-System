# Accurix Financial — Setup & User Manual

A modern, enterprise-grade Double-Entry Accounting & Ledger Suite built with **Java Servlets**, **Java Swing Desktop GUI**, **JDBC**, **MySQL**, and a sleek **Vercel Geist-inspired minimalist Light UI**.

---

## 1. System Requirements & Prerequisites

- **Java Development Kit (JDK)**: Java 17+ (LTS recommended)
- **Servlet Container**: Apache Tomcat 9.x or 10.x
- **Relational Database**: MySQL 8.0+ or MariaDB 10.5+
- **Build Tool**: Apache Maven 3.8+ (A portable Maven wrapper is included in `.tools/apache-maven-3.9.6`)
- **Modern Web Browser**: Google Chrome, Microsoft Edge, Mozilla Firefox, or Safari

---

## 2. Database Installation & Configuration

### Step 1: Initialize MySQL Database
1. Launch MySQL CLI or MySQL Workbench.
2. Execute the provided schema creation script located at [`database/schema.sql`](file:///c:/Users/0xp47/Desktop/KIM/database/schema.sql):
   ```sql
   source /path/to/database/schema.sql;
   ```
   *This automatically creates `accounting_db`, sets up constraints/foreign keys, and seeds sample users, accounts, and balanced journal entries.*

### Step 2: Configure Database Credentials
Open [`src/main/resources/db.properties`](file:///c:/Users/0xp47/Desktop/KIM/src/main/resources/db.properties):
```properties
db.driver=com.mysql.cj.jdbc.Driver
db.url=jdbc:mysql://localhost:3306/accounting_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8
db.user=root
db.password=your_mysql_password
```
*(Alternatively, configure environment variables `DB_URL`, `DB_USER`, and `DB_PASSWORD` without changing code.)*

---

## 3. Running the Application (Unified Master Launcher)

The application provides a unified interactive launcher [`run.bat`](run.bat) at the project root (alongside a Windows desktop shortcut [`Accounting System.lnk`](Accounting%20System.lnk)):

```text
======================================================================
         ENTERPRISE ACCOUNTING SYSTEM -- DOUBLE-ENTRY LEDGER
                Philippine GAAP / IFRS Compliant [PHP]
======================================================================

  Select which system you would like to run:

  [1] Native Desktop Java GUI (100% Desktop Application)
  [2] Web-Based Application (Apache Tomcat and Browser)
  [3] Launch BOTH Systems Concurrently (Desktop GUI + Web System)
  [4] Stop Web Application (Shut down Apache Tomcat server)
  [5] Exit
======================================================================
```

### Option A: 100% Native Desktop Java GUI (Recommended Desktop Mode)
The application includes a standalone Java Desktop GUI window built with Swing and FlatLaf, featuring pristine Vercel Geist Light mode theming, high-DPI scaling, Java2D vector geometric icons, and direct MySQL connectivity:
1. Ensure MySQL is running on port 3306 (via XAMPP).
2. Double-click `run.bat` (or `Accounting System.lnk`) and press **`1`**.
   - Or launch directly via Maven:
   ```cmd
   mvn compile exec:java
   ```
3. The desktop GUI launches directly with custom window decorations, crisp Vercel button hover interactions, and custom Windows taskbar dock branding (`app_icon.ico`).

### Option B: Web Browser Application via Apache Tomcat
1. Ensure MySQL and Tomcat are running in your XAMPP Control Panel.
2. Double-click `run.bat` and press **`2`**.
   - The script builds `target/accounting-system.war`, synchronizes it to `<TOMCAT_HOME>/webapps/accounting.war`, starts Tomcat on port 8080, and opens your default browser at:
   ```text
   http://localhost:8080/accounting
   ```

### Option C: Launch BOTH Systems Concurrently
Double-click `run.bat` and press **`3`**. Both the Desktop GUI and the Web application start simultaneously, sharing the exact same live MySQL database transactions.

> [!NOTE]
> Secondary individual scripts are neatly archived in the [`scripts/`](../scripts/) folder:
> - `scripts/start-accounting-gui.bat`
> - `scripts/start-accounting-system.bat`
> - `scripts/stop-accounting-system.bat`

---

## 4. User Manual & Module Walkthrough

### 4.1 Authentication & Default Credentials
Navigate to `http://localhost:8080/accounting-system/login`.

| Username | Password | Role | Description |
|:---|:---|:---|:---|
| `admin` | `admin123` | Administrator | Full administrative & accounting access |
| `accountant` | `password123` | Senior Accountant | Transaction posting & reports access |
| `igabutan` | `password123` | Accountant | Ivan G. Gabutan (Team Member Account) |

> [!NOTE]
> Passwords are encrypted with BCrypt (10 salt rounds). Plain-text passwords are never stored.

---

### 4.2 Dashboard Overview (`/dashboard`)
Upon signing in, you are greeted with the **Overview Dashboard**:
- **Equilibrium Status Pill**: Live indicator confirming $\text{Total Debits} = \text{Total Credits}$.
- **KPI Stat Cards**:
  - Total recorded journal vouchers
  - Chart of accounts count
  - Net operating income (highlighted in emerald green for profit, rose for loss)
  - Cumulative volume of debits posted
- **Recent Activity Table**: View details of the 5 most recent vouchers.

---

### 4.3 Journal Entries (`/journals`)
- **View Journal Entries (`/journals`)**:
  - Filter transactions by **Start Date**, **End Date**, or keyword search on particulars.
  - Review debit and credit totals with double-entry balance validation.
- **Record a New Entry (`/journals?action=new`)**:
  1. Pick a transaction date and optional voucher reference.
  2. Enter descriptive particulars.
  3. Select accounts from the dropdown and input debit or credit amounts.
  4. Click **+ Add Line** to create compound entries (e.g. 1 debit, 2 credits).
  5. The real-time calculator checks that $\text{Total Debit} = \text{Total Credit}$. The **Save** button is protected against submitting unbalanced transactions.
- **View Voucher (`/journals?action=view&id={id}`)**:
  - Formatted accounting voucher with full breakdown, timestamps, and print capability.
- **Edit & Delete**:
  - Modify dates, accounts, or amounts while preserving balance integrity.
  - Deleting an entry removes its associated debit/credit postings in an atomic database transaction.

---

### 4.4 General Ledger (`/ledger`)
- Select any account from the Chart of Accounts (e.g. `1010 - Cash on Hand & Bank`).
- Features:
  - **Account Classification Badge**: Displays normal balance rule (Normal Debit vs. Normal Credit).
  - **Beginning Balance**: Carryover calculated from transactions prior to the chosen start date.
  - **Running Balance Column**: Cumulative account balance dynamically computed row-by-row according to accounting rules.
  - **Date Range Filter**: Filter postings for a specific month or fiscal quarter.

---

### 4.5 Financial Reports (`/reports`)
1. **Trial Balance (`/reports?type=trial_balance`)**:
   - Lists active account debit/credit balances as of any cut-off date.
   - Shows double-underlined accounting totals.
   - Live equilibrium badge validates $\text{Total Debits} = \text{Total Credits}$.
2. **Income Statement (`/reports?type=income_statement`)**:
   - Separates Operating Revenues from Operating Expenses for any period.
   - Automatically calculates:
     $$\text{Net Income} = \text{Total Revenue} - \text{Total Expenses}$$
3. **Balance Sheet (`/reports?type=balance_sheet`)**:
   - Classifies Assets, Liabilities, and Owner's Equity.
   - Dynamically rolls current period Net Income into Owner's Equity.
   - Enforces the fundamental accounting equation:
     $$\text{Assets} = \text{Liabilities} + \text{Equity}$$
4. **Print-Friendly Views**:
   - Click **Print Report** on any report view. Navigation, sidebars, and buttons automatically hide via `@media print`.

---

### 4.6 Chart of Accounts (`/accounts`)
- Master listing of all 5 classifications: Assets (1000s), Liabilities (2000s), Equity (3000s), Revenues (4000s), Expenses (5000s).
- Click **Add Account** to define new ledger accounts with code uniqueness checks.

---

### 4.7 Account Settings (`/settings`)
- Change your display name.
- Change your password with current password verification and BCrypt hashing.
