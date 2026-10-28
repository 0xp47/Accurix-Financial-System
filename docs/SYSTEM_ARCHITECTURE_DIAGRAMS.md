# Accounting System — System Architecture & Design Diagrams

This document contains visual diagrams for the team's final documentation deliverables:
1. Entity Relationship Diagram (ERD)
2. Use Case Diagram
3. Data Flow Diagram (DFD Level 0 & Level 1)
4. System Flowchart (End-to-End Transaction Flow)
5. Clean Architecture Component Diagram

---

## 1. Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    USERS ||--o{ JOURNAL_ENTRIES : "creates"
    JOURNAL_ENTRIES ||--|{ JOURNAL_ENTRY_LINES : "contains"
    CHART_OF_ACCOUNTS ||--o{ JOURNAL_ENTRY_LINES : "categorizes"

    USERS {
        int id PK
        string username UK
        string password_hash
        string full_name
        string role
        timestamp created_at
        timestamp updated_at
    }

    CHART_OF_ACCOUNTS {
        int id PK
        string account_code UK
        string account_name
        enum account_type
        string description
        boolean is_active
        timestamp created_at
        timestamp updated_at
    }

    JOURNAL_ENTRIES {
        int id PK
        date entry_date
        text particulars
        string reference_number
        int created_by FK
        timestamp created_at
        timestamp updated_at
    }

    JOURNAL_ENTRY_LINES {
        int id PK
        int journal_entry_id FK
        int account_id FK
        decimal debit
        decimal credit
        int line_order
    }
```

---

## 2. Use Case Diagram

```mermaid
flowchart LR
    subgraph Actors
        User["Authenticated User / Accountant"]
        Admin["Administrator"]
    end

    subgraph "Accounting System Boundary"
        UC1["Login & Authenticate Session"]
        UC2["Manage Account Profile & Password"]
        UC3["Create Balanced Journal Entry"]
        UC4["View / Search Journal Entries"]
        UC5["Edit / Delete Journal Entry"]
        UC6["Inspect General Ledger & Running Balances"]
        UC7["Generate Trial Balance"]
        UC8["Generate Income Statement"]
        UC9["Generate Balance Sheet"]
        UC10["Manage Chart of Accounts"]
        UC11["Print Financial Reports"]
        UC12["Sign Out Safely"]
    end

    User --> UC1
    User --> UC2
    User --> UC3
    User --> UC4
    User --> UC5
    User --> UC6
    User --> UC7
    User --> UC8
    User --> UC9
    User --> UC11
    User --> UC12

    Admin --> UC1
    Admin --> UC2
    Admin --> UC3
    Admin --> UC10
    Admin --> UC11
    Admin --> UC12
```

---

## 3. Data Flow Diagram (DFD Level 0 Context & Level 1)

### 3.1 DFD Level 0 — Context Diagram

```mermaid
flowchart TD
    Accountant["Accountant / User"]
    System["Web-Based Accounting System"]
    DB[("MySQL Database: accounting_db")]

    Accountant -- "1. Login Credentials" --> System
    Accountant -- "2. Journal Entry Vouchers" --> System
    Accountant -- "3. Date Filter & Report Parameters" --> System

    System -- "Authentication Status & Session Token" --> Accountant
    System -- "Ledger Statements & Financial Reports" --> Accountant

    System -- "Prepared SQL CRUD & Queries" --> DB
    DB -- "Account Data, Transactions & Balances" --> System
```

### 3.2 DFD Level 1 — Detailed Process Breakdown

```mermaid
flowchart TD
    User["User"]

    subgraph Processes
        P1["1.0 Authenticate User"]
        P2["2.0 Validate & Record Journal Entry"]
        P3["3.0 Post to General Ledger"]
        P4["4.0 Compile Trial Balance"]
        P5["5.0 Calculate Financial Statements"]
        P6["6.0 Manage Chart of Accounts"]
    end

    subgraph Data Stores
        D1[("Users Store")]
        D2[("Chart of Accounts")]
        D3[("Journal Entries (Headers)")]
        D4[("Journal Entry Lines")]
    end

    User -->|Username & Password| P1
    P1 <-->|Verify BCrypt Hash| D1
    P1 -->|Active Session| User

    User -->|New Voucher| P2
    P2 <-->|Fetch Active Codes| D2
    P2 -->|Save Valid Header| D3
    P2 -->|Save Balanced Lines| D4

    D3 & D4 --> P3
    P3 -->|Running Balance Postings| User

    D2 & D4 --> P4
    P4 -->|Trial Balance Equality Report| User

    D2 & D4 --> P5
    P5 -->|Income Statement & Balance Sheet| User

    User -->|Add New Code| P6
    P6 -->|Insert Account| D2
```

---

## 4. System Flowchart (End-to-End Processing Flow)

```mermaid
flowchart TD
    Start(["Start"]) --> OpenApp["Access Application URL"]
    OpenApp --> AuthCheck{"Authenticated Session?"}

    AuthCheck -- "No" --> LoginForm["Render Vercel Login Page"]
    LoginForm --> SubmitCreds["Submit Username & Password"]
    SubmitCreds --> VerifyBCrypt{"BCrypt Match in MySQL?"}
    VerifyBCrypt -- "No" --> LoginError["Display Error Flash"]
    LoginError --> LoginForm
    VerifyBCrypt -- "Yes" --> CreateSession["Create HTTP Session & Set Attributes"]

    AuthCheck -- "Yes" --> Dashboard["Display Dashboard Overview"]
    CreateSession --> Dashboard

    Dashboard --> UserAction{"Select Menu Action"}

    UserAction -- "New Journal" --> FormJE["Open Journal Entry Form"]
    FormJE --> InputLines["Input Particulars, Accounts, Debits, Credits"]
    InputLines --> BalanceCheck{"Total Debit == Total Credit?"}
    BalanceCheck -- "No" --> UnbalancedAlert["Block Save & Display Difference Badge"]
    UnbalancedAlert --> InputLines
    BalanceCheck -- "Yes" --> SaveJE["Commit Transaction to MySQL Header & Lines"]
    SaveJE --> ShowList["Redirect to Journal List"]

    UserAction -- "General Ledger" --> SelectAccount["Select Account & Date Range"]
    SelectAccount --> CalcRunning["Compute Prior Carryover & Running Balances"]
    CalcRunning --> ShowLedger["Display Ledger Table with Normal Balance Rules"]

    UserAction -- "Reports" --> ChooseReport{"Choose Report"}
    ChooseReport -- "Trial Balance" --> GenTB["Aggregate Account Balances & Check Invariant"]
    ChooseReport -- "Income Statement" --> GenIS["Compute Net Income: Revenues - Expenses"]
    ChooseReport -- "Balance Sheet" --> GenBS["Verify Assets = Liabilities + Equity"]

    ShowLedger --> PrintOpt{"Print View?"}
    GenTB --> PrintOpt
    GenIS --> PrintOpt
    GenBS --> PrintOpt

    PrintOpt -- "Yes" --> PrintMedia["Trigger @media print Clean View"]
    PrintOpt -- "No" --> UserAction

    UserAction -- "Sign Out" --> Logout["Invalidate Session & Redirect to Login"]
    Logout --> End(["End"])
```

---

## 5. Dual-Tier Clean Architecture Component Breakdown

```mermaid
flowchart TD
    subgraph Presentation ["Dual Presentation Tiers (Vercel Geist Light Design)"]
        GUI["100% Native Desktop Java GUI: Swing + FlatLaf Light + Java2D Vector Icons + Custom Taskbar Icon"]
        WebUI["Web Application: JSP Views + JSTL 1.2 + Vercel Geist Light CSS + Print Rules"]
    end

    subgraph EntryPoints ["Execution Entry Points"]
        Launcher["run.bat Master Launcher / Accounting System.lnk"]
        TomcatServer["Apache Tomcat 9/10 (Port 8080)"]
    end

    subgraph Controllers ["Controllers & GUI Panels"]
        AuthFilter["AuthFilter (Session & Route Guard)"]
        Servlets["Servlets: Login, Journal, Ledger, Report, Account, Settings"]
        GuiPanels["GUI Panels: MainFrame, Dashboard, Journals, Ledger, Reports, Accounts, Settings"]
    end

    subgraph ServiceLayer ["Business Logic & Validation Layer (Shared)"]
        Validation["JournalValidationService (Double-Entry Invariant: Debits = Credits)"]
        Rules["AccountType (Normal Balance & Net Income Calculations)"]
        Security["PasswordUtil (BCrypt 10-Round Hashing)"]
        GuiUtils["GuiUtils (Philippine Peso ₱ Formatting & Vector Graphics)"]
    end

    subgraph DAOLayer ["Persistence & DAO Layer (Shared)"]
        UserDAO["UserDAO"]
        AccountDAO["AccountDAO"]
        JournalDAO["JournalEntryDAO (ACID Transactions & Cascading Deletion)"]
        LedgerDAO["LedgerDAO (Chronological Postings)"]
        ReportDAO["ReportDAO (Aggregations & Balance Sheet Equation)"]
        DBConn["DBConnection (Thread-Safe JDBC Connection Manager)"]
    end

    subgraph Database ["MySQL 8.0 Storage"]
        MySQL[("accounting_db")]
    end

    Launcher --> GUI
    Launcher --> TomcatServer
    TomcatServer --> WebUI
    GUI --> GuiPanels
    WebUI --> Servlets
    WebUI --> AuthFilter
    GuiPanels --> ServiceLayer
    GuiPanels --> DAOLayer
    Servlets --> ServiceLayer
    Servlets --> DAOLayer
    ServiceLayer --> DAOLayer
    DAOLayer --> DBConn
    DBConn --> MySQL
```
