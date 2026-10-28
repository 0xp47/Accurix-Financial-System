-- =====================================================================
-- ACCOUNTING SYSTEM DATABASE SCHEMA
-- Target Database: MySQL 8.0+
-- Prepared for: Student Final Project / Accounting Team
-- Architecture: Clean relational schema with Header + Lines journal model
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `accounting_db` 
    DEFAULT CHARACTER SET utf8mb4 
    COLLATE utf8mb4_unicode_ci;

USE `accounting_db`;

-- Drop existing tables in correct order of foreign key dependencies
DROP TABLE IF EXISTS `journal_entry_lines`;
DROP TABLE IF EXISTS `journal_entries`;
DROP TABLE IF EXISTS `chart_of_accounts`;
DROP TABLE IF EXISTS `users`;

-- ---------------------------------------------------------------------
-- 1. USERS TABLE
-- ---------------------------------------------------------------------
CREATE TABLE `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password_hash` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL,
    `role` VARCHAR(20) NOT NULL DEFAULT 'ACCOUNTANT',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 2. CHART OF ACCOUNTS TABLE
-- Supports: ASSET, LIABILITY, EQUITY, REVENUE, EXPENSE
-- ---------------------------------------------------------------------
CREATE TABLE `chart_of_accounts` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `account_code` VARCHAR(20) NOT NULL UNIQUE,
    `account_name` VARCHAR(100) NOT NULL,
    `account_type` ENUM('ASSET', 'LIABILITY', 'EQUITY', 'REVENUE', 'EXPENSE') NOT NULL,
    `description` VARCHAR(255) NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT TRUE,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_account_code` (`account_code`),
    INDEX `idx_account_type` (`account_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. JOURNAL ENTRIES (Header Table)
-- ---------------------------------------------------------------------
CREATE TABLE `journal_entries` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `entry_date` DATE NOT NULL,
    `particulars` TEXT NOT NULL,
    `reference_number` VARCHAR(50) NULL,
    `created_by` INT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `fk_journal_entries_created_by`
        FOREIGN KEY (`created_by`) REFERENCES `users` (`id`)
        ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX `idx_entry_date` (`entry_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. JOURNAL ENTRY LINES (Individual Debit / Credit Line Items)
-- ---------------------------------------------------------------------
CREATE TABLE `journal_entry_lines` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `journal_entry_id` INT NOT NULL,
    `account_id` INT NOT NULL,
    `debit` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `credit` DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    `line_order` INT NOT NULL DEFAULT 0,
    CONSTRAINT `fk_lines_journal_entry`
        FOREIGN KEY (`journal_entry_id`) REFERENCES `journal_entries` (`id`)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_lines_account`
        FOREIGN KEY (`account_id`) REFERENCES `chart_of_accounts` (`id`)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `chk_positive_amounts` 
        CHECK (`debit` >= 0 AND `credit` >= 0),
    INDEX `idx_lines_journal_entry` (`journal_entry_id`),
    INDEX `idx_lines_account` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================================
-- SEED DATA
-- =====================================================================

-- 1. Insert Sample Users
-- Passwords:
-- 'admin'       -> 'admin123'
-- 'accountant'  -> 'password123'
-- 'igabutan'    -> 'password123'
INSERT INTO `users` (`id`, `username`, `password_hash`, `full_name`, `role`) VALUES
(1, 'admin', '$2a$10$eEb1kdfA976Hdk6PEA3Qh.NvQr0Lwu8Gw722slfqscuJ/UiHeF3/2', 'Administrator', 'ADMIN'),
(2, 'accountant', '$2a$10$eQ3ekID568jC/vppifHc1eVXfVy.y6rYYlEKNK.NRDOhS1Gx2K2Em', 'Senior Accountant', 'ACCOUNTANT'),
(3, 'igabutan', '$2a$10$eQ3ekID568jC/vppifHc1eVXfVy.y6rYYlEKNK.NRDOhS1Gx2K2Em', 'Ivan G. Gabutan', 'ACCOUNTANT');

-- 2. Insert Chart of Accounts
-- Assets (1000s)
INSERT INTO `chart_of_accounts` (`id`, `account_code`, `account_name`, `account_type`, `description`) VALUES
(1, '1010', 'Cash on Hand & Bank', 'ASSET', 'Liquid funds and operating checking accounts'),
(2, '1020', 'Accounts Receivable', 'ASSET', 'Amounts due from credit clients'),
(3, '1030', 'Prepaid Rent', 'ASSET', 'Advance rent payments for office space'),
(4, '1040', 'Office Supplies', 'ASSET', 'Stationery, paper, and general supplies inventory'),
(5, '1050', 'Office Equipment', 'ASSET', 'Computers, printers, and office machinery');

-- Liabilities (2000s)
INSERT INTO `chart_of_accounts` (`id`, `account_code`, `account_name`, `account_type`, `description`) VALUES
(6, '2010', 'Accounts Payable', 'LIABILITY', 'Short-term obligations to suppliers and vendors'),
(7, '2020', 'Notes Payable', 'LIABILITY', 'Promissory notes and bank loans'),
(8, '2030', 'Unearned Revenue', 'LIABILITY', 'Customer advance payments for services pending');

-- Equity (3000s)
INSERT INTO `chart_of_accounts` (`id`, `account_code`, `account_name`, `account_type`, `description`) VALUES
(9, '3010', 'Owner\'s Capital', 'EQUITY', 'Initial and ongoing capital contributions'),
(10, '3020', 'Owner\'s Drawings', 'EQUITY', 'Owner withdrawals for personal use');

-- Revenue (4000s)
INSERT INTO `chart_of_accounts` (`id`, `account_code`, `account_name`, `account_type`, `description`) VALUES
(11, '4010', 'Accounting Service Fees', 'REVENUE', 'Income from professional accounting services'),
(12, '4020', 'Consulting Revenue', 'REVENUE', 'Income from advisory and tax consulting');

-- Expenses (5000s)
INSERT INTO `chart_of_accounts` (`id`, `account_code`, `account_name`, `account_type`, `description`) VALUES
(13, '5010', 'Rent Expense', 'EXPENSE', 'Monthly facility rental fee'),
(14, '5020', 'Salaries & Wages Expense', 'EXPENSE', 'Staff payroll and remuneration'),
(15, '5030', 'Utilities Expense', 'EXPENSE', 'Electricity, water, and internet bills'),
(16, '5040', 'Advertising & Marketing', 'EXPENSE', 'Online marketing, promotional costs, and campaigns');

-- 3. Insert Balanced Sample Journal Entries & Lines
-- Entry 1: 2026-10-01 - Initial capital investment of 150,000 cash
INSERT INTO `journal_entries` (`id`, `entry_date`, `particulars`, `reference_number`, `created_by`) VALUES
(1, '2026-10-01', 'Initial capital contribution by owner', 'JE-2026-001', 1);

INSERT INTO `journal_entry_lines` (`journal_entry_id`, `account_id`, `debit`, `credit`, `line_order`) VALUES
(1, 1, 150000.00, 0.00, 1), -- Debit: Cash (1010)
(1, 9, 0.00, 150000.00, 2); -- Credit: Owner's Capital (3010)

-- Entry 2: 2026-10-02 - Purchased office equipment on account from supplier
INSERT INTO `journal_entries` (`id`, `entry_date`, `particulars`, `reference_number`, `created_by`) VALUES
(2, '2026-10-02', 'Acquired computer workstations and printers on credit', 'JE-2026-002', 2);

INSERT INTO `journal_entry_lines` (`journal_entry_id`, `account_id`, `debit`, `credit`, `line_order`) VALUES
(2, 5, 35000.00, 0.00, 1), -- Debit: Office Equipment (1050)
(2, 6, 0.00, 35000.00, 2); -- Credit: Accounts Payable (2010)

-- Entry 3: 2026-10-05 - Paid advance rent for 2 months
INSERT INTO `journal_entries` (`id`, `entry_date`, `particulars`, `reference_number`, `created_by`) VALUES
(3, '2026-10-05', 'Paid 2 months advance office space rental', 'JE-2026-003', 2);

INSERT INTO `journal_entry_lines` (`journal_entry_id`, `account_id`, `debit`, `credit`, `line_order`) VALUES
(3, 3, 20000.00, 0.00, 1), -- Debit: Prepaid Rent (1030)
(3, 1, 0.00, 20000.00, 2); -- Credit: Cash (1010)

-- Entry 4: 2026-10-10 - Rendered accounting & consulting services: 30,000 in cash, 20,000 on account
INSERT INTO `journal_entries` (`id`, `entry_date`, `particulars`, `reference_number`, `created_by`) VALUES
(4, '2026-10-10', 'Rendered comprehensive bookkeeping and tax consulting services', 'JE-2026-004', 2);

INSERT INTO `journal_entry_lines` (`journal_entry_id`, `account_id`, `debit`, `credit`, `line_order`) VALUES
(4, 1, 30000.00, 0.00, 1),  -- Debit: Cash (1010)
(4, 2, 20000.00, 0.00, 2),  -- Debit: Accounts Receivable (1020)
(4, 11, 0.00, 35000.00, 3), -- Credit: Accounting Service Fees (4010)
(4, 12, 0.00, 15000.00, 4); -- Credit: Consulting Revenue (4020)

-- Entry 5: 2026-10-15 - Paid staff salaries in cash
INSERT INTO `journal_entries` (`id`, `entry_date`, `particulars`, `reference_number`, `created_by`) VALUES
(5, '2026-10-15', 'Paid mid-month staff payroll', 'JE-2026-005', 2);

INSERT INTO `journal_entry_lines` (`journal_entry_id`, `account_id`, `debit`, `credit`, `line_order`) VALUES
(5, 14, 15000.00, 0.00, 1), -- Debit: Salaries Expense (5020)
(5, 1, 0.00, 15000.00, 2);  -- Credit: Cash (1010)

-- Entry 6: 2026-10-20 - Paid partial accounts payable to vendor
INSERT INTO `journal_entries` (`id`, `entry_date`, `particulars`, `reference_number`, `created_by`) VALUES
(6, '2026-10-20', 'Settled partial payment for office equipment', 'JE-2026-006', 2);

INSERT INTO `journal_entry_lines` (`journal_entry_id`, `account_id`, `debit`, `credit`, `line_order`) VALUES
(6, 6, 15000.00, 0.00, 1),  -- Debit: Accounts Payable (2010)
(6, 1, 0.00, 15000.00, 2);  -- Credit: Cash (1010)

-- Entry 7: 2026-10-25 - Paid utilities bill
INSERT INTO `journal_entries` (`id`, `entry_date`, `particulars`, `reference_number`, `created_by`) VALUES
(7, '2026-10-25', 'Paid monthly electric and high-speed fiber internet bill', 'JE-2026-007', 2);

INSERT INTO `journal_entry_lines` (`journal_entry_id`, `account_id`, `debit`, `credit`, `line_order`) VALUES
(7, 15, 4500.00, 0.00, 1),  -- Debit: Utilities Expense (5030)
(7, 1, 0.00, 4500.00, 2);   -- Credit: Cash (1010)
