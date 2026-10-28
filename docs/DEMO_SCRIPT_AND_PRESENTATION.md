# Accounting System — Presentation Deck & Live Demo Script

**Project:** Web-Based Accounting System  
**Stack:** Java Servlets • JDBC • MySQL • Apache Tomcat • Maven • Vercel Geist Design System  
**Document Leads:** Cortes, Ashlee Nicole B. (Presentation & Documentation) & Aranez, Hannah Bea C. (Project Management)  
**Target Audience:** Project Evaluators, Computer Science Faculty, and Peer Reviewers  
**Target Duration:** 15 – 20 Minutes (including live demonstration and Q&A)

---

## 1. Team Roles & Speaker Distribution

| Presenter | Role & Assignment | Section Covered |
|---|---|---|
| **Aranez, Hannah Bea C.** | Project Manager | Project Objectives, Scope, Team Work Plan & Governance |
| **Baydal, Nica** | System Analyst | System Analysis, Requirements, Use Case, DFD & Flowcharts |
| **Villorejo, Joshua Niño A.** | Database Lead | Database Architecture, Schema, ERD, Constraints & Seed Data |
| **Gabutan, Ivan G.** | Security & Auth | Authentication, Password Hashing (BCrypt), Sessions & Settings |
| **Cabase, Wincel James A.** | Lead Dev (Journals & JDBC) | Double-Entry Engine, ACID Transactions & Live Validation |
| **Pacaña, Kim Y.** | Ledger Lead | General Ledger Postings, Normal Balances & Running Calculations |
| **Gimena, Byn Jumer L.** | Financial Reports Lead | Trial Balance, Income Statement, Balance Sheet & Date Filtering |
| **Melgar, Ashley Rosse** | UI / Frontend Lead | Vercel Geist Minimalist Design System, Responsive Layout & Print Views |
| **Mamites, Angelique P.** | QA / Testing Lead | Test Strategy, Automated JUnit Results, Bug Fixes & QA Sign-Off |
| **Cortes, Ashlee Nicole B.** | Lead Presenter & Docs | Live Demonstration Host, Setup Guide & Concluding Q&A Defense |

---

## 2. Slide-by-Slide Presentation Deck Outline

### Slide 1: Title Slide & Introduction
- **Visual:** High-contrast minimalist title with project logo and team roster.
- **Title:** Web-Based Accounting System: Enterprise Double-Entry Ledger & Financial Reporting Engine.
- **Presenter:** Aranez, Hannah Bea C.
- **Talking Points:**
  - Introduce the system as a robust, compliant double-entry accounting web application built from scratch without bulky third-party enterprise frameworks.
  - Highlight the core mission: bridging computer science principles (ACID transactions, modular separation, security) with international GAAP accounting standards.

### Slide 2: Problem Statement & Objectives
- **Visual:** Side-by-side comparison: "Manual / Spreadsheet Bookkeeping" vs. "Our Web-Based System".
- **Presenter:** Aranez, Hannah Bea C.
- **Talking Points:**
  - Traditional small-business bookkeeping relies on spreadsheets vulnerable to formula errors, out-of-balance entries, and unauthenticated edits.
  - Objectives: Authenticated access, mandatory balanced double-entry validation ($\text{Debit} = \text{Credit}$), automated general ledger postings, instant GAAP financial reports, and audit trails.

### Slide 3: Dual-Mode System Architecture & Clean Separation of Concerns
- **Visual:** Clean Architecture block diagram showing Dual Presentation Tiers (Desktop GUI + Web UI) connecting to shared Service, DAO, and MySQL layers.
- **Presenter:** Baydal, Nica
- **Talking Points:**
  - Adheres strictly to Clean Architecture and the MVC (Model-View-Controller) design pattern.
  - **Dual Presentation Tiers:** Features both a 100% Native Desktop Java GUI (Swing, FlatLaf Light, Java2D vector graphics) and a responsive Web Application (Servlets, JSP, JSTL 1.2).
  - **Unified Business Logic & DAOs:** Both presentation interfaces share the exact same underlying validation services (`JournalValidationService`), math engines, and JDBC DAOs.
  - **Controllers & Servlets:** Handle request dispatching, session validation, and UI event routing.
  - **DAOs:** Centralized thread-safe JDBC queries using `PreparedStatement` to eliminate SQL injection and guarantee ACID transaction rollbacks.

### Slide 4: Database Design & Entity Relationship Diagram (ERD)
- **Visual:** Crow's Foot Mermaid ERD showing `users`, `chart_of_accounts`, `journal_entries`, and `journal_entry_lines`.
- **Presenter:** Villorejo, Joshua Niño A.
- **Talking Points:**
  - Decided on the professional **Header + Lines** normalized structure (`journal_entries` $1:N$ `journal_entry_lines`).
  - Avoids denormalized, inflexible single-table structures.
  - Foreign key constraints with `ON DELETE CASCADE` on lines and `ON DELETE RESTRICT` on accounts to preserve historical accounting integrity.
  - Strict `DECIMAL(15, 2)` monetary columns to eliminate floating-point penny rounding discrepancies.

### Slide 5: Security & Session Management
- **Visual:** Security flow diagram showing BCrypt salt generation and `AuthFilter` interception.
- **Presenter:** Gabutan, Ivan G.
- **Talking Points:**
  - Passwords are never stored in plaintext; salted with 10 rounds of BCrypt (`jBCrypt`).
  - `AuthFilter` protects all internal routes (`/dashboard`, `/journals`, `/ledger`, `/reports`, `/accounts`, `/settings`).
  - Direct browser URL injections to unauthenticated sessions automatically trigger 302 redirects to `/login`.
  - Secure session destruction on logout, including cache-control headers to prevent Back-button history access.

### Slide 6: The Double-Entry Engine & Transaction Integrity
- **Visual:** Code snippet of `JournalEntryDAO` transaction rollback alongside live dynamic UI table.
- **Presenter:** Cabase, Wincel James A.
- **Talking Points:**
  - Core accounting rule: No transaction is saved unless $\sum \text{Debits} = \sum \text{Credits}$.
  - Client-side JavaScript provides instant user feedback, calculating debits, credits, and differences on every keystroke, disabling the Save button if unbalanced.
  - Server-side double validation ensures that forged or corrupted HTTP POST requests are intercepted and rejected.
  - Database persistence uses JDBC transaction blocks (`connection.setAutoCommit(false)`), ensuring that both the header and all child lines either commit together or roll back completely on failure.

### Slide 7: General Ledger & Running Balance Mechanics
- **Visual:** Ledger table showing chronological running balance calculations for Asset vs. Liability.
- **Presenter:** Pacaña, Kim Y.
- **Talking Points:**
  - Real-time ledger posting: Every journal entry immediately influences account balances without requiring batch batch-jobs.
  - Dynamic Normal Balance logic:
    - **Asset / Expense:** $\text{Balance} = \text{Debits} - \text{Credits}$
    - **Liability / Equity / Revenue:** $\text{Balance} = \text{Credits} - \text{Debits}$
  - Historical carryover support: When filtering by date, the system aggregates pre-period transactions into a "Beginning Balance" carryover row.

### Slide 8: Financial Reporting Suite (GAAP Compliant)
- **Visual:** Mockups of Trial Balance, Income Statement, and Balance Sheet.
- **Presenter:** Gimena, Byn Jumer L.
- **Talking Points:**
  - **Trial Balance:** Lists ending debit and credit balances for all accounts to confirm mathematical equilibrium.
  - **Income Statement:** Real-time computation of $\text{Revenues} - \text{Expenses} = \text{Net Income (or Net Loss)}$.
  - **Balance Sheet:** Verifies the fundamental accounting equation:
    $$\text{Assets} = \text{Liabilities} + \text{Equity}$$
  - Dynamic Retained Earnings integration: Current period net income is dynamically mapped into Owner's Equity, ensuring perfect balance sheet reconciliation.

### Slide 9: User Interface & Vercel Geist Light Aesthetic
- **Visual:** Screenshots of Desktop GUI and Web Application showcasing the clean Vercel Light monochrome theme, Java2D vector icons, custom taskbar icon, and refined button hover interactions.
- **Presenter:** Melgar, Ashley Rosse
- **Talking Points:**
  - Inspired by Vercel's design language: crisp typography, subtle 1px border cards, monochrome buttons, and uppercase tracking-wider table headers.
  - **100% Light Mode strictly enforced:** Purged dark mode media queries and locked pure light mode across both desktop and web interfaces.
  - **High-DPI Java2D Vector Icons:** Complete elimination of unicode emojis, replaced with custom vector balance scales, ledgers, and geometric status indicators.
  - **Refined Button Interactions:** Authentic Vercel hover states (primary black `#000000` to `#262626` hover; secondary white with `#000000` hover border).
  - **Print-friendly CSS (`@media print`):** One-click report printing strips navigation and sidebars, rendering formal paper statements ready for executive review.

### Slide 10: Quality Assurance, Automated Testing & Bug Lifecycle
- **Visual:** Test execution summary chart (22 unit tests passing, 10 defects resolved, 100% pass rate).
- **Presenter:** Mamites, Angelique P.
- **Talking Points:**
  - 100% pass rate across 22 automated JUnit unit tests and 28 manual integration test scenarios.
  - Defect tracking followed a formal bug lifecycle: 10 defects identified, assigned, resolved, and verified (BUG-001 through BUG-010).
  - Addressed critical accounting and presentation bugs including floating-point cent precision, liability sign inversion, balance sheet net income absorption, dark mode override purge, and taskbar branding integration.

### Slide 11: Live Demonstration Handover & Unified Launcher
- **Visual:** Overview of the master interactive launcher (`run.bat`) offering options for Desktop GUI, Web App, or concurrent execution.
- **Presenter:** Cortes, Ashlee Nicole B.
- **Talking Points:**
  - Transition to live demonstration using the master launcher `run.bat` (or `Accounting System.lnk`).
  - Highlights dual-mode capabilities: demonstrator can launch the standalone Desktop GUI (`[1]`), the Web Application (`[2]`), or run both simultaneously (`[3]`).
  - Demonstration agenda: Login $\rightarrow$ Dashboard KPI inspection $\rightarrow$ Creating a compound journal entry $\rightarrow$ Ledger inspection $\rightarrow$ Real-time report verification $\rightarrow$ Print preview.

---

## 3. Step-by-Step Live Demonstration Script

### Pre-Demo Checklist (T-minus 5 minutes)
- [ ] MySQL service running on port 3306 (`accounting_db` populated with schema and seeds).
- [ ] Master launcher `run.bat` or `Accounting System.lnk` ready on Desktop / project root.
- [ ] For Web demo: Apache Tomcat running on port 8080 (`http://localhost:8080/accounting`).
- [ ] For Desktop GUI demo: Java 17 runtime verified.
- [ ] Clean browser session opened in Chrome or Edge (for web demonstration).
- [ ] Screen display scaled to 100% or 110% for crisp projector/screen sharing visibility.

---

### Phase 1: Authentication & Protection (2 Minutes)
**Demonstrator:** Ivan G. Gabutan / Ashlee Nicole B. Cortes

1. **Test Direct URL Bypass:**
   - **Action:** Open browser address bar and navigate directly to:
     `http://localhost:8080/accounting/dashboard`
   - **Spoken Commentary:** *"Notice that before we log in, attempting to reach the internal dashboard immediately redirects us back to `/login`. Our custom `AuthFilter` intercepts unauthorized requests at the servlet layer."*
2. **Test Invalid Login:**
   - **Action:** Type `admin` and an incorrect password `wrongpassword123`. Click **Sign in**.
   - **Spoken Commentary:** *"The system refuses access and renders a clear error notification without leaking internal system state."*
3. **Successful Authentication:**
   - **Action:** Type `admin` and password `admin123`. Click **Sign in**.
   - **Spoken Commentary:** *"Upon successful credential verification using BCrypt password hashing, a secure HTTP session is established, and we land on the Executive Dashboard."*

---

### Phase 2: Executive Dashboard & Live Indicators (2 Minutes)
**Demonstrator:** Hannah Bea C. Aranez / Ashley Rosse Melgar

1. **Highlight Live KPI Cards:**
   - **Action:** Point mouse to the 4 primary metric cards: Total Assets, Total Liabilities, Total Equity, and Net Income.
   - **Spoken Commentary:** *"All numbers here are live aggregations computed directly from MySQL. No hardcoded or static figures exist."*
2. **Check Equilibrium Status Badge:**
   - **Action:** Point to the top-right card showing `Trial Balance: IN BALANCE`.
   - **Spoken Commentary:** *"Notice the live equilibrium badge confirming that the general ledger is currently in mathematical balance, with debits exactly matching credits."*

---

### Phase 3: Creating a Double-Entry Journal Entry (4 Minutes)
**Demonstrator:** Wincel James A. Cabase / Angelique P. Mamites

1. **Navigate to Journal Entries:**
   - **Action:** Click **Journal Entries** on the sidebar, then click **+ New Entry**.
2. **Demonstrate Dynamic Imbalance Blocking:**
   - **Action:**
     - Date: Select today's date.
     - Particulars: Type `Purchase of Equipment via Cash and Credit`.
     - Row 1: Select `Office Equipment (1040)`, enter Debit `₱50,000.00`.
     - Row 2: Select `Cash on Hand (1010)`, enter Credit `₱20,000.00`.
   - **Spoken Commentary:** *"Observe the live transaction status bar at the bottom. The Total Debit shows ₱50,000, Total Credit shows ₱20,000, and Difference shows ₱30,000. The Save Entry button is dynamically disabled, preventing the user from committing an unbalanced entry."*
3. **Add Third Line for Compound Entry:**
   - **Action:**
     - Click **+ Add Line**.
     - Row 3: Select `Accounts Payable (2010)`, enter Credit `₱30,000.00`.
   - **Spoken Commentary:** *"Now, Total Debits (₱50,000) exactly equals Total Credits (₱50,000). The difference turns to ₱0.00, the status pill turns green ('Balanced'), and the Save Entry button is enabled."*
4. **Save & Commit:**
   - **Action:** Click **Save Entry**.
   - **Spoken Commentary:** *"The entry is submitted. The servlet validates all business rules server-side, executes an atomic JDBC transaction, and commits both the header and all three lines to MySQL."*

---

### Phase 4: General Ledger & Dynamic Running Balance (3 Minutes)
**Demonstrator:** Kim Y. Pacaña

1. **Navigate to General Ledger:**
   - **Action:** Click **General Ledger** on the sidebar.
2. **Inspect Cash Account (Asset):**
   - **Action:** Select `1010 - Cash on Hand` from the account dropdown.
   - **Spoken Commentary:** *"Notice the credit of ₱20,000 that we just posted. Because Cash is an Asset account with a normal debit balance, this credit properly reduces the running balance."*
3. **Inspect Accounts Payable (Liability):**
   - **Action:** Switch dropdown to `2010 - Accounts Payable`.
   - **Spoken Commentary:** *"Notice the credit of ₱30,000. Because Accounts Payable is a Liability with a normal credit balance, this credit properly increases the payable balance."*
4. **Demonstrate Date Range Filter:**
   - **Action:** Set the Start Date to tomorrow or a narrow window.
   - **Spoken Commentary:** *"When a date range filter is applied, transactions prior to the start date are automatically aggregated into the 'Beginning Balance' carryover row, maintaining complete historical accounting continuity."*

---

### Phase 5: Financial Reporting & Print View (3 Minutes)
**Demonstrator:** Byn Jumer L. Gimena / Ashlee Nicole B. Cortes

1. **View Trial Balance:**
   - **Action:** Click **Financial Reports** $\rightarrow$ **Trial Balance**.
   - **Spoken Commentary:** *"The Trial Balance aggregates ending balances across all active accounts. Notice the bottom summary row: Total Debits and Total Credits match to the exact cent, displaying our green 'BALANCED' pill."*
2. **View Balance Sheet:**
   - **Action:** Click **Balance Sheet** tab.
   - **Spoken Commentary:** *"This is the ultimate test of an accounting system. Our Balance Sheet confirms the fundamental GAAP equation: Total Assets = Total Liabilities + Total Equity. Notice the 'Current Period Net Income' line item under Equity—our system dynamically incorporates operational profit into retained equity without requiring artificial manual closing entries."*
3. **Demonstrate Print-Friendly View:**
   - **Action:** Click the **Print Report** button (or press Ctrl+P).
   - **Spoken Commentary:** *"Through custom `@media print` CSS rules, the navigation sidebar, action buttons, and filters vanish completely, producing a clean, formal corporate financial statement suitable for external auditing."*

---

### Phase 6: Conclusion & Logout (1 Minute)
**Demonstrator:** Ivan G. Gabutan / Hannah Bea C. Aranez

1. **Navigate to Settings & Profile:**
   - **Action:** Click **Settings** in the sidebar. Show update capability.
2. **Execute Secure Sign Out:**
   - **Action:** Click **Sign out**.
   - **Spoken Commentary:** *"Clicking sign out completely destroys the server-side session, clears the security context, and returns us to the login screen. Thank you, and we welcome any questions from the panel."*

---

## 4. Anticipated Panel Q&A & Technical Defense Guide

### Question 1: "How do you guarantee that a network failure or database crash during journal entry creation does not result in partial saves (orphaned header without lines)?"
**Speaker:** Wincel James A. Cabase / Joshua Niño A. Villorejo  
**Answer:**  
*"We implement strict ACID transaction control in `JournalEntryDAO`. When saving, we invoke `connection.setAutoCommit(false)`. We first insert the journal entry header to acquire the generated ID, then execute a batch insert for all child lines. Only if all lines succeed do we call `connection.commit()`. If any SQL error occurs at any point, the catch block immediately calls `DBConnection.rollback(connection)`, ensuring zero partial or corrupted entries are ever saved."*

---

### Question 2: "Why did your team choose the Header + Lines database architecture instead of a simpler single-table journal entry model?"
**Speaker:** Joshua Niño A. Villorejo / Nica Baydal  
**Answer:**  
*"A single-table design forces redundancy and makes multi-line compound transactions (e.g., 1 debit split across 3 credit accounts) nearly impossible to model cleanly. Our normalized Header + Lines structure represents the true real-world accounting model: one voucher header containing date and particulars, linked $1:N$ to discrete debit and credit line items. This allows unlimited compound lines while keeping database indexes clean and queries efficient."*

---

### Question 3: "How does the system ensure the Balance Sheet balances when there have been operational revenues and expenses during the period?"
**Speaker:** Byn Jumer L. Gimena / Kim Y. Pacaña  
**Answer:**  
*"In GAAP accounting, operational net income ($\text{Revenues} - \text{Expenses}$) belongs to the owners and flows into Retained Earnings under Equity. In `ReportDAO.generateBalanceSheet()`, our query computes the cumulative net income up to the selected cutoff date and adds a dynamic 'Current Period Net Income / Retained Earnings' row to the Owner's Equity section. This guarantees that $\text{Assets} = \text{Liabilities} + (\text{Paid-in Capital} + \text{Net Income})$ remains strictly in equilibrium at all times."*

---

### Question 4: "Why choose Vercel Geist styling instead of standard CSS or Bootstrap?"
**Speaker:** Ashley Rosse Melgar / Ashlee Nicole B. Cortes  
**Answer:**  
*"Standard frameworks like Bootstrap or Tailwind add unnecessary complexity and bloated JavaScript runtimes. The Vercel Geist design system is modern, clean, and highly readable, which is ideal for data-dense enterprise applications like accounting. By building our own `geist.css` stylesheet using CSS variables and clean flex/grid rules, we achieved a fast, accessible, lightweight UI that loads instantly, responds smoothly, and prints cleanly."*

---

### Question 5: "What security measures are implemented against common web vulnerabilities?"
**Speaker:** Ivan G. Gabutan  
**Answer:**  
*"We address security on multiple fronts:
1. **SQL Injection:** All database queries exclusively use `PreparedStatement` with parameterized placeholders (`?`), preventing SQL injection.
2. **Credential Theft:** Passwords are never stored in plaintext; we utilize 10 rounds of BCrypt with cryptographic salt.
3. **Unauthorized Access:** `AuthFilter` guards all sensitive servlet endpoints and JSP paths.
4. **Session Fixation:** New sessions are generated upon login, and `session.invalidate()` is strictly called on logout."*
