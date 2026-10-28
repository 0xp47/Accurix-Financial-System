# Accounting System — Quality Assurance & Testing Report

**Document Version:** 1.0.0  
**Project:** Web-Based Accounting System (Java Servlets • JDBC • MySQL)  
**Lead QA Analyst / Assigned Lead:** Mamites, Angelique P.  
**Collaborators:** Cabase, Wincel James A. (Journal & JDBC), Pacaña, Kim Y. (General Ledger), Gimena, Byn Jumer L. (Financial Reports), Gabutan, Ivan G. (Authentication)  
**Date of Execution:** October 2026  
**Test Status:** **PASSED — ALL MODULES VERIFIED & ACCEPTED (100% PASS RATE)**

---

## 1. Executive Summary

This Quality Assurance & Testing Report validates that the **Web-Based Accounting System** meets all functional, non-functional, security, and accounting accuracy criteria specified in the project development document.

All major modules—including **User Authentication & Session Guard**, **Chart of Accounts**, **Double-Entry Journal Management**, **General Ledger**, **Trial Balance**, **Income Statement**, **Balance Sheet**, and **Date Filtering**—were tested using both automated JUnit unit tests and manual end-to-end integration test scenarios.

### Summary Metrics
| Metric | Count / Value |
|---|---|
| **Total Automated Unit Tests** | 22 tests (100% Passed) |
| **Total Manual End-to-End Test Cases** | 28 scenarios (100% Passed) |
| **Total Defects Identified & Resolved** | 10 defects (10 Fixed, 0 Open) |
| **Accounting Equality Verification** | **Passed** ($\sum \text{Debits} = \sum \text{Credits}$; $\text{Assets} = \text{Liabilities} + \text{Equity}$) |
| **Cross-Platform Verification** | Windows 10/11 (Desktop GUI) & Chrome 120+, Edge 120+, Firefox 121+ (Web) |
| **Final QA Sign-off** | **APPROVED FOR DEPLOYMENT & FINAL DEFENSE** |

---

## 2. Test Strategy & Scope

### 2.1 Scope of Testing
1. **Security & Authentication:** Session invalidation, BCrypt hashing verification, unauthorized URL bypass blocking, AJAX 401 response handling.
2. **Data Integrity & Double-Entry Accounting:** Enforcing balanced entries ($\text{Total Debits} = \text{Total Credits}$), prevention of negative amounts, single-line rejection, and ACID transaction rollback on failure.
3. **General Ledger Posting:** Accurate posting by account type (Asset, Liability, Equity, Revenue, Expense), running balance progression, and historical carryover calculation.
4. **Financial Reporting:** Trial balance debit/credit equilibrium, Income Statement net income computation, and Balance Sheet reconciliation with net income absorption into Owner's Equity.
5. **UI & Usability:** Vercel Geist design compliance, clean typography, responsive layout, dynamic multi-row JavaScript calculation, and `@media print` clean views.

### 2.2 Test Environment
- **Server:** Apache Tomcat 9.0.x / 10.1.x
- **Runtime:** Java OpenJDK 17 LTS
- **Database:** MySQL Server 8.0.x (`accounting_db` with InnoDB engine)
- **Database Driver:** MySQL Connector/J 8.3.0
- **Build Tool:** Apache Maven 3.9.6
- **Test Framework:** JUnit 4.13.2

---

## 3. Automated Unit Test Execution Results

All 22 unit tests run automatically during Maven packaging (`mvn test` / `mvn package`).

```
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.accounting.service.FinancialReportsCalculationTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.052 s - in com.accounting.service.FinancialReportsCalculationTest
[INFO] Running com.accounting.service.JournalValidationServiceTest
[INFO] Tests run: 9, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.009 s - in com.accounting.service.JournalValidationServiceTest
[INFO] Running com.accounting.service.LedgerCalculationTest
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.005 s - in com.accounting.service.LedgerCalculationTest
[INFO] Running com.accounting.util.PasswordUtilTest
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.354 s - in com.accounting.util.PasswordUtilTest
[INFO] 
[INFO] Results:
[INFO] 
[INFO] Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Breakdown of Unit Test Coverage
1. **`PasswordUtilTest` (5 tests):**
   - Hashing non-null string generates valid BCrypt hash format.
   - Exact plaintext password verifies as `true`.
   - Incorrect plaintext password verifies as `false`.
   - Distinct salt generates unique hash strings for identical passwords.
   - Graceful rejection of `null` and empty password strings.
2. **`JournalValidationServiceTest` (9 tests):**
   - Valid 2-line balanced entry returns empty error list.
   - Multi-line compound balanced entry returns empty error list.
   - Unbalanced entry ($\text{Debit} \ne \text{Credit}$) returns descriptive imbalance error.
   - Empty particulars/description triggers required field error.
   - Missing entry date triggers required date error.
   - Entry with fewer than 2 lines is rejected.
   - Lines with $0.00$ or negative debit and credit amounts are rejected.
   - Lines with both debit and credit filled simultaneously are rejected.
   - Unassigned/invalid account ID triggers account selection error.
3. **`LedgerCalculationTest` (3 tests):**
   - Normal debit account (Asset/Expense) running balance increases with debits and decreases with credits.
   - Normal credit account (Liability/Equity/Revenue) running balance increases with credits and decreases with debits.
   - Opening carryover balance accurately aggregates preceding historical transactions prior to selected date range.
4. **`FinancialReportsCalculationTest` (5 tests):**
   - Trial Balance verifies zero difference between total debits and total credits.
   - Income Statement verifies $\text{Net Income} = \text{Total Revenues} - \text{Total Expenses}$.
   - Income Statement handles net loss scenario correctly without crashing.
   - Balance Sheet equation rigorously holds: $\text{Total Assets} = \text{Total Liabilities} + (\text{Total Equity} + \text{Net Income})$.
   - Balance Sheet correctly flags unbalanced state if synthetic imbalance is injected.

---

## 4. Manual End-to-End Test Matrix

The following test cases were manually executed against a live MySQL database and Apache Tomcat server.

### 4.1 Authentication & User Management (Gabutan, Ivan G.)
| Test Case ID | Test Scenario | Steps to Execute | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-AUTH-01** | Valid User Login | 1. Navigate to `/login`<br>2. Enter `admin` / `admin123`<br>3. Click "Sign in" | Redirects to `/dashboard`; session initialized; username visible in sidebar. | Matches expected. | **PASS** |
| **TC-AUTH-02** | Invalid Password | 1. Enter `admin` / `wrongpass`<br>2. Submit form | Stays on `/login`; shows red error: "Invalid username or password". | Matches expected. | **PASS** |
| **TC-AUTH-03** | Non-existent Username | 1. Enter `ghost` / `secret`<br>2. Submit form | Stays on `/login`; shows error message without revealing user existence. | Matches expected. | **PASS** |
| **TC-AUTH-04** | Route Protection | 1. Clear cookies/session<br>2. Directly type `http://localhost:8080/accounting/dashboard` | Request intercepted by `AuthFilter`; 302 redirect to `/login`. | Matches expected. | **PASS** |
| **TC-AUTH-05** | Secure Logout | 1. Logged in as user<br>2. Click "Sign out" button | Session invalidated; redirected to `/login?logged_out=true`; Back button cannot access protected pages. | Matches expected. | **PASS** |
| **TC-AUTH-06** | Profile Settings Update | 1. Go to `/settings`<br>2. Change Full Name to "Administrator User"<br>3. Save | Profile updated in MySQL; header immediately displays updated name. | Matches expected. | **PASS** |

### 4.2 Chart of Accounts (Villorejo, Joshua Niño A.)
| Test Case ID | Test Scenario | Steps to Execute | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-ACC-01** | View Chart of Accounts | 1. Navigate to `/accounts` | Complete list of all accounts displayed grouped by Asset, Liability, Equity, Revenue, Expense. | Matches expected. | **PASS** |
| **TC-ACC-02** | Create New Valid Account | 1. Enter Code: `1030`, Name: `Petty Cash`, Type: `Asset`<br>2. Submit | Account created in DB; displayed in table and available in Journal Entry dropdowns. | Matches expected. | **PASS** |
| **TC-ACC-03** | Duplicate Account Code Rejection | 1. Enter Code: `1010` (Cash on Hand already exists)<br>2. Submit | System displays SQL duplicate key error cleanly; does not crash. | Matches expected. | **PASS** |

### 4.3 Journal Entry Module & Validation (Cabase, Wincel James A.)
| Test Case ID | Test Scenario | Steps to Execute | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-JRN-01** | Client-Side Live Balance Calculation | 1. Navigate to `/journals?action=create`<br>2. Add Debit $5,000 to Cash<br>3. Add Credit $4,000 to Sales | Real-time totals show Debit $5,000, Credit $4,000, Difference $1,000; "Save Entry" button is disabled. | Matches expected. | **PASS** |
| **TC-JRN-02** | Create Simple Balanced Entry | 1. Select Date: 2026-10-20<br>2. Particulars: "Office Supplies Purchase"<br>3. Debit Office Supplies ₱3,500; Credit Cash ₱3,500<br>4. Click "Save Entry" | Database transaction commits; redirects to `/journals`; voucher created. | Matches expected. | **PASS** |
| **TC-JRN-03** | Compound Entry Creation | 1. Debit Cash ₱8,000<br>2. Debit Accounts Receivable ₱2,000<br>3. Credit Service Revenue ₱10,000<br>4. Submit | Successfully saves 1 header and 3 lines atomically. | Matches expected. | **PASS** |
| **TC-JRN-04** | Server-Side Imbalance Rejection | 1. Bypass client JS with forged POST (Debit 100, Credit 50) | `JournalValidationService` rejects; transaction rolls back; error displayed. | Matches expected. | **PASS** |
| **TC-JRN-05** | View Journal Voucher | 1. Click "View" on entry #1 | Displays clean read-only voucher with account codes, lines, totals, and print button. | Matches expected. | **PASS** |
| **TC-JRN-06** | Edit Journal Entry | 1. Click "Edit" on entry #1<br>2. Modify particulars to "Initial Capital Investment - Revised"<br>3. Save | Updated particulars and line items persisted; updated_at timestamp updated. | Matches expected. | **PASS** |
| **TC-JRN-07** | Delete Journal Entry | 1. Click "Delete" on draft entry<br>2. Confirm browser dialog | Header and cascading lines removed cleanly from DB; ledger balances update immediately. | Matches expected. | **PASS** |

### 4.4 General Ledger & Postings (Pacaña, Kim Y.)
| Test Case ID | Test Scenario | Steps to Execute | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-LED-01** | Account Ledger Display | 1. Navigate to `/ledger?accountId=1` (Cash on Hand) | Lists all debits, credits, and chronological running balance. | Matches expected. | **PASS** |
| **TC-LED-02** | Running Balance Sign Convention | 1. Inspect Asset account (Cash)<br>2. Inspect Liability account (Accounts Payable) | Cash increases with Debits; Accounts Payable increases with Credits. | Matches expected. | **PASS** |
| **TC-LED-03** | Date Range Filter on Ledger | 1. Select Start: 2026-10-05, End: 2026-10-15<br>2. Filter | Shows "Beginning Balance" carryover before Oct 5; only lists transactions within range. | Matches expected. | **PASS** |
| **TC-LED-04** | Instant Search / Account Switch | 1. Select different account from dropdown | Page loads ledger for selected account immediately with all postings. | Matches expected. | **PASS** |

### 4.5 Financial Reports (Gimena, Byn Jumer L.)
| Test Case ID | Test Scenario | Steps to Execute | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-REP-01** | Trial Balance Debit/Credit Balance | 1. Navigate to `/reports?type=trial_balance` | Total Debits exactly equals Total Credits; status badge displays "BALANCED". | Matches expected. | **PASS** |
| **TC-REP-02** | Trial Balance Date Filtering | 1. Filter up to 2026-10-10 | Recalculates trial balance as of that cutoff; debits equal credits. | Matches expected. | **PASS** |
| **TC-REP-03** | Income Statement Computation | 1. Navigate to `/reports?type=income_statement` | Lists Revenues, Expenses, and calculates $\text{Net Income} = \text{Revenues} - \text{Expenses}$. | Matches expected. | **PASS** |
| **TC-REP-04** | Balance Sheet Fundamental Equation | 1. Navigate to `/reports?type=balance_sheet` | Total Assets exactly equals Total Liabilities + Total Equity (including Net Income). | Matches expected. | **PASS** |
| **TC-REP-05** | Balance Sheet Equilibrium Badge | 1. Inspect summary row | Shows green checkmark pill: "✓ Equilibrium Confirmed: Assets = Liabilities + Equity". | Matches expected. | **PASS** |
| **TC-REP-06** | Print-Friendly View | 1. Open any report<br>2. Click "Print Report" (or Ctrl+P) | `@media print` hides sidebar, navigation bars, filters, and action buttons; displays clean paper statement. | Matches expected. | **PASS** |

### 4.6 Dashboard & System Overview (Melgar, Ashley Rosse & Aranez, Hannah Bea C.)
| Test Case ID | Test Scenario | Steps to Execute | Expected Result | Actual Result | Status |
|---|---|---|---|---|---|
| **TC-DSH-01** | Live Metric Cards | 1. Navigate to `/dashboard` | Metric cards (Total Assets, Liabilities, Equity, Revenues, Net Income) match database records. | Matches expected. | **PASS** |
| **TC-DSH-02** | Equilibrium Status Card | 1. View top-right card on dashboard | Displays "Trial Balance: IN BALANCE" with identical debit/credit figures. | Matches expected. | **PASS** |
| **TC-DSH-03** | Recent Entries Feed | 1. Inspect recent table on dashboard | Displays last 5 journal entries with live links to view vouchers. | Matches expected. | **PASS** |

---

## 5. Defect Log & Tracking (Section 13 Format)

During the development and testing cycle, the following 8 defects were detected, recorded, addressed by the responsible leads, and re-tested to closure.

### BUG-001
- **Bug ID:** BUG-001
- **Module:** Authentication (`LoginServlet`)
- **Summary:** Generic NullPointerException when submitting blank login form.
- **Steps to Reproduce:**
  1. Open `/login`.
  2. Leave username and password empty.
  3. Click "Sign in".
- **Expected Result:** Clean error message "Please enter both username and password."
- **Actual Result:** HTTP 500 error due to null pointer in `trim()`.
- **Severity:** High
- **Assigned To:** Gabutan, Ivan G.
- **Status:** **Fixed & Verified** (Added null check guard before string manipulation).

### BUG-002
- **Bug ID:** BUG-002
- **Module:** Journal Entry (`form.jsp`)
- **Summary:** Save button remained active when debit and credit had floating-point cent differences.
- **Steps to Reproduce:**
  1. Add line: ₱100.00 debit.
  2. Add line: ₱99.99 credit.
  3. Client script compared numbers using loose `!= 0` with IEEE float precision.
- **Expected Result:** "Save Entry" disabled; difference shown as ₱0.01.
- **Actual Result:** Small fractional jitter caused intermittent button state toggling.
- **Severity:** Medium
- **Assigned To:** Cabase, Wincel James A.
- **Status:** **Fixed & Verified** (Normalized arithmetic in JS using `Math.round(val * 100)`).

### BUG-003
- **Bug ID:** BUG-003
- **Module:** Journal Entry (`JournalEntryDAO`)
- **Summary:** Uncaught SQL foreign key constraint exception when deleting an account with posted lines.
- **Steps to Reproduce:**
  1. Attempt to delete account `1010` via direct SQL/admin call.
- **Expected Result:** Database blocks deletion; application handles exception gracefully.
- **Actual Result:** DDL initially lacked explicit `RESTRICT` clause on account foreign key.
- **Severity:** High
- **Assigned To:** Villorejo, Joshua Niño A.
- **Status:** **Fixed & Verified** (Added `ON DELETE RESTRICT` constraint in `schema.sql`).

### BUG-004
- **Bug ID:** BUG-004
- **Module:** General Ledger (`LedgerDAO`)
- **Summary:** Liability accounts displayed negative running balance when credited.
- **Steps to Reproduce:**
  1. Post credit of ₱20,000 to Accounts Payable.
  2. View Ledger.
- **Expected Result:** Running balance shows +₱20,000 (normal credit balance).
- **Actual Result:** Showed -₱20,000 because formula always assumed Debit $-$ Credit.
- **Severity:** Critical
- **Assigned To:** Pacaña, Kim Y.
- **Status:** **Fixed & Verified** (Implemented `AccountType.calculateBalance()` dynamically: Debit $-$ Credit for Assets/Expenses, Credit $-$ Debit for Liabilities/Equity/Revenues).

### BUG-005
- **Bug ID:** BUG-005
- **Module:** Financial Reports (`ReportDAO`)
- **Summary:** Balance Sheet did not balance when current year operations had positive net profit.
- **Steps to Reproduce:**
  1. Post initial capital ₱50,000.
  2. Post service revenue ₱10,000.
  3. View Balance Sheet.
- **Expected Result:** Assets (₱60,000) = Liabilities (₱0) + Equity (₱50,000 Capital + ₱10,000 Net Income).
- **Actual Result:** Assets was ₱60,000 while Equity was only ₱50,000 (₱10,000 out of balance).
- **Severity:** Critical
- **Assigned To:** Gimena, Byn Jumer L.
- **Status:** **Fixed & Verified** (Integrated calculated Net Income into Owner's Equity section of Balance Sheet as per GAAP standards).

### BUG-006
- **Bug ID:** BUG-006
- **Module:** UI / Printing (`css/geist.css`)
- **Summary:** Dark navigation sidebar printed on paper output during browser print.
- **Steps to Reproduce:**
  1. Open Trial Balance report.
  2. Press Ctrl+P.
- **Expected Result:** Clean white sheet showing only company header, date, and tabular report.
- **Actual Result:** Left navigation bar and filter controls occupied half the paper printout.
- **Severity:** Medium
- **Assigned To:** Melgar, Ashley Rosse
- **Status:** **Fixed & Verified** (Added comprehensive `@media print` rules hiding `.sidebar`, `.filter-card`, `.btn-action`, and stripping backgrounds).

### BUG-007
- **Bug ID:** BUG-007
- **Module:** Journal Entry (`JournalServlet`)
- **Summary:** Editing a journal entry created duplicate rows instead of replacing existing lines.
- **Steps to Reproduce:**
  1. Edit entry #2.
  2. Modify debit amount and click Save.
- **Expected Result:** Line items replaced cleanly within a single transaction.
- **Actual Result:** Old line items remained in DB alongside new lines.
- **Severity:** High
- **Assigned To:** Cabase, Wincel James A.
- **Status:** **Fixed & Verified** (Updated `JournalEntryDAO.updateJournalEntry()` to execute `DELETE FROM journal_entry_lines WHERE journal_entry_id = ?` before inserting new lines within the same atomic connection transaction).

### BUG-008
- **Bug ID:** BUG-008
- **Module:** Security (`AuthFilter`)
- **Summary:** Direct requests to `/pages/dashboard.jsp` bypassed `AuthFilter` if not mapped to `.jsp` extension.
- **Steps to Reproduce:**
  1. Enter direct URL `http://localhost:8080/accounting/pages/dashboard.jsp` in private window.
- **Expected Result:** Redirect to `/login`.
- **Actual Result:** Partial JSP rendered with empty session attributes.
- **Severity:** Critical
- **Assigned To:** Gabutan, Ivan G.
- **Status:** **Fixed & Verified** (Configured `AuthFilter` URL patterns in `web.xml` and programmatic session checks on all JSP resources).

### BUG-009
- **Bug ID:** BUG-009
- **Module:** UI / Theme Styling (`css/geist.css` & JSPs)
- **Summary:** System color-scheme dark mode media queries automatically forced dark styling on users with OS dark mode enabled.
- **Steps to Reproduce:**
  1. Launch web browser in Windows 11 with system dark mode enabled.
  2. Open `http://localhost:8080/accounting/login`.
- **Expected Result:** High-contrast, clean Vercel Light UI as mandated by client requirements.
- **Actual Result:** Page rendered with dark backgrounds and uncoordinated dark tokens.
- **Severity:** Medium
- **Assigned To:** Melgar, Ashley Rosse
- **Status:** **Fixed & Verified** (Purged all `[data-theme="dark"]` and `@media (prefers-color-scheme: dark)` rules from `geist.css`; locked `data-theme="light"` across all JSPs).

### BUG-010
- **Bug ID:** BUG-010
- **Module:** Desktop GUI (`AccountingDesktopApp`, `GuiUtils`)
- **Summary:** Default Java Duke mascot icon displayed in Windows taskbar; button hover effect produced muddy dark slate rectangles.
- **Steps to Reproduce:**
  1. Launch Desktop GUI via `run.bat`.
  2. Inspect Windows taskbar dock and hover over `[Refresh]` and `[+ New Journal Entry]` buttons.
- **Expected Result:** Custom executive Vercel branding icon in taskbar; crisp Vercel monochrome button hover transitions.
- **Actual Result:** Windows taskbar showed Duke mascot icon; buttons darkened with FlatLaf double-overlay.
- **Severity:** Medium
- **Assigned To:** Melgar, Ashley Rosse & Cabase, Wincel James A.
- **Status:** **Fixed & Verified** (Generated multi-resolution `app_icon.ico` and implemented `java.awt.Taskbar.setIconImage`; rebuilt button paintComponent with authentic Vercel `#000000` -> `#262626` hover).

---

## 6. QA Sign-Off & Recommendation

| Role | Name | Signature / Sign-Off Date |
|---|---|---|
| **Lead QA Tester** | Angelique P. Mamites | **APPROVED** — October 27, 2026 |
| **Project Manager** | Hannah Bea C. Aranez | **APPROVED** — October 27, 2026 |
| **Lead Developer** | Wincel James A. Cabase | **APPROVED** — October 27, 2026 |
| **Database Lead** | Joshua Niño A. Villorejo | **APPROVED** — October 27, 2026 |

### Recommendation
The accounting software has successfully satisfied **100% of functional requirements (FR-01 through FR-17)**, all non-functional stability and security metrics, and verified accounting mathematical equilibrium across all modules.

**The system is certified ready for student final defense, demonstration, and production deployment.**
