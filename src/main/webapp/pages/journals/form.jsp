<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="journals" scope="request"/>
<c:set var="pageTitle" value="${isEdit ? 'Edit Journal Entry' : 'New Journal Entry'}" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${isEdit ? 'Edit Journal Entry' : 'New Journal Entry'} — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
    <style>
        .line-row:hover { background-color: var(--bg-subtle); }
        .diff-balanced { color: var(--success-text); font-weight: 600; }
        .diff-unbalanced { color: var(--error-text); font-weight: 600; }
    </style>
</head>
<body>
<div class="app-layout">
    <jsp:include page="/pages/common/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/pages/common/header.jsp"/>

        <main class="content-body">
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">
                        ${isEdit ? 'Edit Journal Entry #'.concat(entry.id) : 'Create Journal Entry'}
                    </h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Record financial transaction with double-entry debit and credit equality.
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/journals" class="btn btn-secondary">
                        &larr; Back to Journal List
                    </a>
                </div>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <circle cx="12" cy="12" r="10"></circle>
                        <line x1="12" y1="8" x2="12" y2="12"></line>
                        <line x1="12" y1="16" x2="12.01" y2="16"></line>
                    </svg>
                    <span>${error}</span>
                </div>
            </c:if>

            <form id="journalForm" action="${pageContext.request.contextPath}/journals" method="post">
                <input type="hidden" name="action" value="${isEdit ? 'update' : 'save'}">
                <c:if test="${isEdit}">
                    <input type="hidden" name="id" value="${entry.id}">
                </c:if>

                <!-- Transaction Header Details -->
                <div class="geist-card">
                    <div class="geist-card-header">
                        <div class="geist-card-title">Transaction Header</div>
                    </div>
                    <div class="geist-card-body">
                        <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 16px;">
                            <div class="form-group" style="margin-bottom: 0;">
                                <label for="entryDate" class="form-label">Transaction Date *</label>
                                <input type="date" id="entryDate" name="entryDate" class="form-input" 
                                       value="${entry.entryDate != null ? entry.entryDate : ''}" required>
                            </div>

                            <div class="form-group" style="margin-bottom: 0;">
                                <label for="referenceNumber" class="form-label">Reference / Voucher #</label>
                                <input type="text" id="referenceNumber" name="referenceNumber" class="form-input" 
                                       placeholder="e.g. JE-2026-008 or INV-1042" 
                                       value="${entry.referenceNumber}">
                            </div>
                        </div>

                        <div class="form-group" style="margin-top: 16px; margin-bottom: 0;">
                            <label for="particulars" class="form-label">Particulars / Explanation *</label>
                            <input type="text" id="particulars" name="particulars" class="form-input" 
                                   placeholder="Describe transaction purpose (e.g. Purchased office supplies on cash)" 
                                   value="${entry.particulars}" required>
                        </div>
                    </div>
                </div>

                <!-- Multi-Line Accounting Entries -->
                <div class="geist-card">
                    <div class="geist-card-header">
                        <div>
                            <div class="geist-card-title">Journal Lines (Debits & Credits)</div>
                            <div class="geist-card-subtitle">Minimum 2 lines required. Total Debit must equal Total Credit.</div>
                        </div>
                        <button type="button" id="btnAddLine" class="btn btn-secondary btn-sm">
                            + Add Line
                        </button>
                    </div>

                    <div style="overflow-x: auto;">
                        <table class="geist-table" id="linesTable">
                            <thead>
                                <tr>
                                    <th style="width: 45%;">Account *</th>
                                    <th style="width: 25%; text-align: right;">Debit Amount (₱)</th>
                                    <th style="width: 25%; text-align: right;">Credit Amount (₱)</th>
                                    <th style="width: 5%; text-align: center;"></th>
                                </tr>
                            </thead>
                            <tbody id="linesContainer">
                                <c:forEach var="line" items="${entry.lines}" varStatus="status">
                                    <tr class="line-row">
                                        <td>
                                            <select name="accountId[]" class="form-select account-select" required>
                                                <option value="">-- Select Account --</option>
                                                <c:forEach var="acc" items="${accounts}">
                                                    <option value="${acc.id}" ${line.accountId == acc.id ? 'selected' : ''}>
                                                        ${acc.accountCode} - ${acc.accountName} (${acc.accountType.displayName})
                                                    </option>
                                                </c:forEach>
                                            </select>
                                        </td>
                                        <td>
                                            <input type="number" step="0.01" min="0" name="debit[]" 
                                                   class="form-input debit-input num-cell" 
                                                   placeholder="0.00" 
                                                   value="${line.debitPositive ? line.debit : ''}">
                                        </td>
                                        <td>
                                            <input type="number" step="0.01" min="0" name="credit[]" 
                                                   class="form-input credit-input num-cell" 
                                                   placeholder="0.00" 
                                                   value="${line.creditPositive ? line.credit : ''}">
                                        </td>
                                        <td style="text-align: center;">
                                            <button type="button" class="btn btn-secondary btn-sm btn-remove-line" title="Remove Line">
                                                &times;
                                            </button>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                            <tfoot>
                                <tr style="background-color: var(--bg-subtle); font-weight: 600;">
                                    <td style="text-align: right; padding-right: 16px;">
                                        Totals:
                                    </td>
                                    <td class="num-cell" id="totalDebitDisplay">0.00</td>
                                    <td class="num-cell" id="totalCreditDisplay">0.00</td>
                                    <td></td>
                                </tr>
                                <tr style="background-color: var(--bg-surface);">
                                    <td style="text-align: right; padding-right: 16px;">
                                        Balance Status:
                                    </td>
                                    <td colspan="3" id="balanceStatusCell">
                                        <span class="diff-unbalanced">Difference: ₱0.00</span>
                                    </td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>

                    <div class="geist-card-footer">
                        <a href="${pageContext.request.contextPath}/journals" class="btn btn-secondary">Cancel</a>
                        <button type="submit" id="btnSubmit" class="btn btn-primary">
                            ${isEdit ? 'Update Journal Entry' : 'Save Journal Entry'}
                        </button>
                    </div>
                </div>
            </form>
        </main>
    </div>
</div>

<!-- Account template for dynamic row generation -->
<template id="rowTemplate">
    <tr class="line-row">
        <td>
            <select name="accountId[]" class="form-select account-select" required>
                <option value="">-- Select Account --</option>
                <c:forEach var="acc" items="${accounts}">
                    <option value="${acc.id}">
                        ${acc.accountCode} - ${acc.accountName} (${acc.accountType.displayName})
                    </option>
                </c:forEach>
            </select>
        </td>
        <td>
            <input type="number" step="0.01" min="0" name="debit[]" 
                   class="form-input debit-input num-cell" placeholder="0.00">
        </td>
        <td>
            <input type="number" step="0.01" min="0" name="credit[]" 
                   class="form-input credit-input num-cell" placeholder="0.00">
        </td>
        <td style="text-align: center;">
            <button type="button" class="btn btn-secondary btn-sm btn-remove-line" title="Remove Line">
                &times;
            </button>
        </td>
    </tr>
</template>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        const linesContainer = document.getElementById('linesContainer');
        const btnAddLine = document.getElementById('btnAddLine');
        const rowTemplate = document.getElementById('rowTemplate');
        const totalDebitDisplay = document.getElementById('totalDebitDisplay');
        const totalCreditDisplay = document.getElementById('totalCreditDisplay');
        const balanceStatusCell = document.getElementById('balanceStatusCell');
        const btnSubmit = document.getElementById('btnSubmit');
        const journalForm = document.getElementById('journalForm');

        function updateTotals() {
            let totalDebit = 0;
            let totalCredit = 0;

            const debitInputs = linesContainer.querySelectorAll('.debit-input');
            const creditInputs = linesContainer.querySelectorAll('.credit-input');

            debitInputs.forEach(input => {
                const val = parseFloat(input.value) || 0;
                totalDebit += val;
            });

            creditInputs.forEach(input => {
                const val = parseFloat(input.value) || 0;
                totalCredit += val;
            });

            totalDebitDisplay.textContent = totalDebit.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
            totalCreditDisplay.textContent = totalCredit.toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 });

            const diff = Math.abs(totalDebit - totalCredit);
            const isBalanced = (diff < 0.001) && (totalDebit > 0);

            if (isBalanced) {
                balanceStatusCell.innerHTML = `
                    <span class="pill pill-revenue" style="font-size: 12px; padding: 4px 10px;">
                        ✓ Balanced: Total Debit = Total Credit (₱` + totalDebit.toLocaleString('en-US', { minimumFractionDigits: 2 }) + `)
                    </span>
                `;
                btnSubmit.removeAttribute('disabled');
            } else {
                balanceStatusCell.innerHTML = `
                    <span class="pill pill-expense" style="font-size: 12px; padding: 4px 10px;">
                        ⚠ Unbalanced: Difference is ₱` + diff.toLocaleString('en-US', { minimumFractionDigits: 2 }) + `
                    </span>
                `;
            }
        }

        // Mutual exclusivity on row: typing in debit clears credit and vice versa
        linesContainer.addEventListener('input', function (e) {
            const target = e.target;
            const row = target.closest('.line-row');
            if (!row) return;

            if (target.classList.contains('debit-input')) {
                if (parseFloat(target.value) > 0) {
                    row.querySelector('.credit-input').value = '';
                }
            } else if (target.classList.contains('credit-input')) {
                if (parseFloat(target.value) > 0) {
                    row.querySelector('.debit-input').value = '';
                }
            }
            updateTotals();
        });

        // Add Line Button
        btnAddLine.addEventListener('click', function () {
            const clone = rowTemplate.content.cloneNode(true);
            linesContainer.appendChild(clone);
            updateTotals();
        });

        // Remove Line Button
        linesContainer.addEventListener('click', function (e) {
            if (e.target.closest('.btn-remove-line')) {
                const rows = linesContainer.querySelectorAll('.line-row');
                if (rows.length <= 2) {
                    alert('A journal entry must contain at least 2 lines.');
                    return;
                }
                e.target.closest('.line-row').remove();
                updateTotals();
            }
        });

        // Form Submit Interception to prevent saving unbalanced entries
        journalForm.addEventListener('submit', function (e) {
            let totalDebit = 0;
            let totalCredit = 0;

            linesContainer.querySelectorAll('.debit-input').forEach(i => totalDebit += parseFloat(i.value) || 0);
            linesContainer.querySelectorAll('.credit-input').forEach(i => totalCredit += parseFloat(i.value) || 0);

            const diff = Math.abs(totalDebit - totalCredit);
            if (diff >= 0.001 || totalDebit <= 0) {
                e.preventDefault();
                alert('Cannot save unbalanced journal entry. Total Debit must equal Total Credit and be greater than 0.');
            }
        });

        // Initialize calculation on load
        updateTotals();
    });
</script>
</body>
</html>
