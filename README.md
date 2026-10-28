# Accurix Financial (Desktop GUI & Web Application)

An enterprise-grade, Philippine GAAP / IFRS-compliant **Double-Entry Accounting & Financial Ledger Suite** developed in Java 17 LTS, JDBC, and MySQL 8. Features both a **100% Native Desktop Java GUI** (FlatLaf with Vercel Geist Light aesthetic, Java2D vector icons, custom taskbar icon) and a responsive **Web Application** (Java Servlets 4.0, JSP, JSTL 1.2, Apache Tomcat). 

Designed and structured according to Clean Architecture and MVC separation of concerns for a final computer science / information technology capstone project. All currency values are strictly formatted in **Philippine Pesos (₱)** with zero foreign currency symbols.

---

## 1. Dual Deployment Modes

The system provides two interchangeable user interfaces backed by the exact same MySQL database layer (`accounting_db`):

| Feature | 100% Desktop GUI Window | Web Browser Application |
|---|---|---|
| **Master Launcher** | `run.bat` (Option `[1]`) or `Accurix Financial.lnk` | `run.bat` (Option `[2]`) |
| **Concurrent Run** | `run.bat` (Option `[3]` launches BOTH GUI and Web concurrently) |
| **Technology** | Java Swing + FlatLaf 3.4.1 (Vercel Geist Light) | Java Servlets 4.0 + JSP 2.3 + JSTL 1.2 |
| **Design System** | Vercel Geist Light (100% Light Mode, monochrome) | Vercel Geist Light (100% Light Mode, monochrome) |
| **Iconography** | Java2D Vector Geometric Icons (Zero emojis) | Clean SVG Vector Icons (Zero emojis) |
| **Branding / Icon** | Multi-res `app_icon.ico` (Taskbar & Titlebar) | Multi-res `favicon.png` & `favicon.ico` |
| **Currency** | Strictly Philippine Pesos (`PHP ₱#,##0.00`) | Strictly Philippine Pesos (`PHP ₱#,##0.00`) |
| **Persistence** | Direct JDBC connection pool (`accounting_db`) | Direct JDBC connection pool (`accounting_db`) |
| **Platform** | Standalone Desktop Application Window | Web Browser (`http://localhost:8080/accounting`) |

---

## 2. Project Team & Module Ownership

| Name | Role | Module Responsibilities |
|---|---|---|
| **Aranez, Hannah Bea C.** | Project Manager | Project Schedule, Task Assignment, Weekly Progress & Final Compilation |
| **Baydal, Nica** | System Analyst | System Description, Objectives, Requirements, Use Case, DFD & Flowcharts |
| **Cabase, Wincel James A.** | Lead Dev (Journals & JDBC) | JDBC Connection Manager, Journal Form, Save/Edit Servlets & Live Validation |
| **Cortes, Ashlee Nicole B.** | Lead Presenter & Docs | Setup Guide, User Manual, Presentation Deck & Live Demo Script |
| **Gabutan, Ivan G.** | Security & Auth | Users Database, BCrypt 10-Round Hashing, Session Management & Settings |
| **Gimena, Byn Jumer L.** | Financial Reports Lead | Trial Balance, Income Statement, Balance Sheet, Date Filtering & Print Views |
| **Mamites, Angelique P.** | QA & Testing Lead | QA Test Plan, Automated JUnit Suites (22/22 Tests), Defect Tracking & Sign-Off |
| **Melgar, Ashley Rosse** | UI / Frontend Lead | Vercel Geist Light Layout, Sidebar, Cards, Buttons & Responsive CSS |
| **Pacaña, Kim Y.** | General Ledger Lead | Chronological Postings, Normal Balance Sign Logic & Running Calculations |
| **Villorejo, Joshua Niño A.** | Database Lead | Schema DDL, ERD, Primary/Foreign Keys, Constraints & Seed Data |

---

## 3. Core Features & Capabilities

- **Authentication & Security:**
  - Salted 10-round BCrypt password hashing (`jBCrypt`).
  - `AuthFilter` URL interception preventing unauthorized direct page access.
  - Session invalidation on sign-out with cache control to prevent browser history caching.
- **Double-Entry Engine:**
  - Strict mathematical equilibrium: $\sum \text{Debits} = \sum \text{Credits}$.
  - Real-time client-side calculation disables save button if an entry is unbalanced.
  - Server-side double validation intercepts forged or malformed requests.
  - Atomic database transactions with connection rollbacks ensuring zero corrupted entries.
- **General Ledger:**
  - Real-time ledger updates for every account.
  - Dynamic Normal Balance calculations:
    - **Asset / Expense:** $\text{Balance} = \text{Debits} - \text{Credits}$
    - **Liability / Equity / Revenue:** $\text{Balance} = \text{Credits} - \text{Debits}$
  - Historical carryovers: Pre-period transactions automatically summarize into a "Beginning Balance" row.
- **GAAP Financial Reporting:**
  - **Trial Balance:** Ending balances across all accounts verifying mathematical equality.
  - **Income Statement:** Real-time computation of operating revenue, expenses, and net profit.
  - **Balance Sheet:** Confirms $\text{Assets} = \text{Liabilities} + \text{Equity}$ with dynamic absorption of current net profit into Owner's Equity.
  - **Print-Friendly:** One-click `@media print` styling strips navigation and renders formal paper financial statements.
- **Chart of Accounts & Live Dashboard:**
  - Full CRUD for Chart of Accounts across 5 core types.
  - Real-time MySQL aggregation cards for Assets, Liabilities, Equity, and Net Income in Philippine Pesos (`₱`).
- **Vercel Geist Light Design:**
  - Monochrome palette: Pitch Black (`#000000`), Dark Charcoal (`#262626`), Crisp White (`#FFFFFF`), Light Border (`#EAEAEA`).
  - Sleek button hover interactions: Secondary buttons feature crisp `#000000` hover borders; Primary buttons feature subtle charcoal hover transitions.
  - High-DPI Java2D geometric vector icons (zero unicode emojis).
  - Custom multi-resolution application icon (`app_icon.ico`) with Windows taskbar dock integration.

---

## 4. Technology Stack

- **Backend:** Java 17 LTS, Java Servlets 4.0, JSTL 1.2
- **Desktop GUI:** Java Swing, FlatLaf 3.4.1 (Vercel Geist Light), Java2D Vector Graphics
- **Persistence:** JDBC, MySQL 8.0+ (InnoDB engine with foreign keys)
- **Security:** jBCrypt 0.4 (10 salt rounds)
- **Build & Packaging:** Apache Maven 3.9.6 (`accounting-system.war`)
- **Server:** Apache Tomcat 9.0+ / 10.1+ (via XAMPP Tomcat on port 8080)
- **Frontend:** HTML5, Modern Vanilla JavaScript, CSS3 (Vercel Geist Light tokens)
- **Testing:** JUnit 4.13.2 (22 unit tests passing, 100% pass rate)

---

## 5. Quick Start Guide (Unified Master Launcher)

The project includes a single master interactive launcher [`run.bat`](run.bat) at the root (and a pre-configured Windows desktop shortcut [`Accurix Financial.lnk`](Accurix%20Financial.lnk)).

```
======================================================================
            ACCURIX FINANCIAL -- DOUBLE-ENTRY LEDGER SUITE
                Philippine GAAP / IFRS Compliant [PHP]
======================================================================

  Select which system you would like to run:

  [1] Native Desktop Java GUI (Accurix Desktop Application)
  [2] Web-Based Application (Accurix Web on Apache Tomcat)
  [3] Launch BOTH Systems Concurrently (Desktop GUI + Web System)
  [4] Stop Web Application (Shut down Apache Tomcat server)
  [5] Exit
======================================================================
```

1. Ensure MySQL is running in your **XAMPP Control Panel** (port 3306).
2. Double-click [`run.bat`](run.bat) or [`Accurix Financial.lnk`](Accurix%20Financial.lnk).
3. Choose an option:
   - Press **`1`**: Launches the **100% Native Desktop Java GUI** window with high-DPI scaling, custom vector icons, and Windows taskbar branding.
   - Press **`2`**: Builds the WAR, synchronizes it to Tomcat, launches Apache Tomcat on port 8080, and opens your browser at `http://localhost:8080/accounting`.
   - Press **`3`**: Launches **BOTH** the Desktop GUI and the Web Application concurrently over the same database.
   - Press **`4`**: Stops Apache Tomcat and frees port 8080.
   - Press **`5`**: Exits the launcher.

> [!NOTE]
> Secondary standalone launch scripts are neatly organized in the [`scripts/`](scripts/) directory:
> - [`scripts/start-accounting-gui.bat`](scripts/start-accounting-gui.bat)
> - [`scripts/start-accounting-system.bat`](scripts/start-accounting-system.bat)
> - [`scripts/stop-accounting-system.bat`](scripts/stop-accounting-system.bat)

### Manual CLI Execution
```bash
# Run unit test suite (22 tests)
mvn test

# Launch Desktop GUI directly from terminal
mvn compile exec:java

# Build WAR package for Tomcat
mvn clean package
```

---

## 6. Default Demonstration Credentials

| Username | Password | Role | Description |
|---|---|---|---|
| `admin` | `admin123` | Administrator | Full administrative and ledger privileges |
| `accountant` | `password123` | Senior Accountant | Standard accounting and reporting access |
| `igabutan` | `password123` | Accountant | Ivan G. Gabutan's demonstration account |

---

## 7. Project Documentation Index

All project deliverables and defense guides are maintained in the [`docs/`](docs/) directory and project root:

- 📄 [**Project Development Specification (.docx)**](Accounting_System_Project_Development_Document.docx): Formal Word specification covering scope, team work plan, architecture, database design, 22 unit tests, and defect logs.
- 📖 [**Setup & User Guide**](docs/SETUP_AND_USER_GUIDE.md): Environment prerequisites, database configuration, deployment instructions, and module-by-module user manual.
- 📐 [**System Architecture & Diagrams**](docs/SYSTEM_ARCHITECTURE_DIAGRAMS.md): Mermaid Entity-Relationship Diagram (ERD), Use Case Diagram, Data Flow Diagrams (Level 0 & Level 1), System Flowchart, and Clean Architecture model.
- 🧪 [**Testing & QA Report**](docs/TESTING_AND_QA_REPORT.md): Quality assurance test strategy, 22 automated JUnit results, 28 manual integration scenarios, defect lifecycle log (BUG-001–008), and formal sign-offs.
- 🎤 [**Presentation Deck & Live Demo Script**](docs/DEMO_SCRIPT_AND_PRESENTATION.md): 11-slide presentation outline with speaker notes, timed live demo walkthrough, and anticipated instructor Q&A technical defense.

---

## 8. Automated Test Execution

To execute the automated regression test suite, run:
```bash
mvn test
```
**Results:** `Tests run: 22, Failures: 0, Errors: 0, Skipped: 0` (100% Pass Rate).
