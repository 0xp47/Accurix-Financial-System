<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="ledger" scope="request"/>
<c:set var="pageTitle" value="General Ledger" scope="request"/>
<c:set var="isReportPage" value="true" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>General Ledger — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="/pages/common/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/pages/common/header.jsp"/>

        <main class="content-body">
            <!-- Header (hidden on print) -->
            <div class="no-print" style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">General Ledger</h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Individual account histories, debit and credit postings, and running balances.
                    </p>
                </div>
                <div>
                    <button onclick="window.print()" class="btn btn-secondary">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6 9 6 2 18 2 18 9"></polyline>
                            <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                            <rect x="6" y="14" width="12" height="8"></rect>
                        </svg>
                        <span>Print Ledger</span>
                    </button>
                </div>
            </div>

            <!-- Print Header -->
            <div class="print-header" style="display: none;">
                <h1 style="font-size: 20px; font-weight: 700;">GENERAL LEDGER ACCOUNT STATEMENT</h1>
                <p style="font-size: 13px; color: #555; margin-top: 4px;">
                    Account: ${ledger.account.accountCode} - ${ledger.account.accountName} (${ledger.account.accountType.displayName})
                </p>
                <c:if test="${not empty startDate || not empty endDate}">
                    <p style="font-size: 11px; color: #777;">
                        Period: ${startDate != null ? startDate : 'Beginning'} to ${endDate != null ? endDate : 'Present'}
                    </p>
                </c:if>
            </div>

            <!-- Account Filter Toolbar (hidden on print) -->
            <div class="geist-card no-print" style="margin-bottom: 20px;">
                <div class="geist-card-body" style="padding: 16px 20px;">
                    <form action="${pageContext.request.contextPath}/ledger" method="get" 
                          style="display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end;">
                        
                        <div style="flex: 2; min-width: 250px;">
                            <label class="form-label" style="font-size: 11px;">Select Account *</label>
                            <select name="accountId" class="form-select" onchange="this.form.submit()">
                                <c:forEach var="acc" items="${accounts}">
                                    <option value="${acc.id}" ${selectedAccountId == acc.id ? 'selected' : ''}>
                                        ${acc.accountCode} — ${acc.accountName} (${acc.accountType.displayName})
                                    </option>
                                </c:forEach>
                            </select>
                        </div>

                        <div style="flex: 1; min-width: 140px;">
                            <label class="form-label" style="font-size: 11px;">From Date</label>
                            <input type="date" name="startDate" value="${startDate}" class="form-input">
                        </div>

                        <div style="flex: 1; min-width: 140px;">
                            <label class="form-label" style="font-size: 11px;">To Date</label>
                            <input type="date" name="endDate" value="${endDate}" class="form-input">
                        </div>

                        <div style="display: flex; gap: 8px;">
                            <button type="submit" class="btn btn-secondary">Filter</button>
                            <a href="${pageContext.request.contextPath}/ledger?accountId=${selectedAccountId}" class="btn btn-secondary">Reset</a>
                        </div>
                    </form>
                </div>
            </div>

            <c:choose>
                <c:when test="${not empty ledger}">
                    <!-- Account Metric Overview Cards -->
                    <div class="grid-stats">
                        <div class="stat-card">
                            <div class="stat-label">Account Code & Name</div>
                            <div style="font-size: 16px; font-weight: 600; margin-top: 8px;">
                                ${ledger.account.accountCode} - ${ledger.account.accountName}
                            </div>
                            <div style="margin-top: 6px;">
                                <c:choose>
                                    <c:when test="${ledger.account.accountType == 'ASSET'}">
                                        <span class="pill pill-asset">Asset (Normal Debit)</span>
                                    </c:when>
                                    <c:when test="${ledger.account.accountType == 'LIABILITY'}">
                                        <span class="pill pill-liability">Liability (Normal Credit)</span>
                                    </c:when>
                                    <c:when test="${ledger.account.accountType == 'EQUITY'}">
                                        <span class="pill pill-equity">Equity (Normal Credit)</span>
                                    </c:when>
                                    <c:when test="${ledger.account.accountType == 'REVENUE'}">
                                        <span class="pill pill-revenue">Revenue (Normal Credit)</span>
                                    </c:when>
                                    <c:when test="${ledger.account.accountType == 'EXPENSE'}">
                                        <span class="pill pill-expense">Expense (Normal Debit)</span>
                                    </c:when>
                                </c:choose>
                            </div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-label">Beginning Balance</div>
                            <div class="stat-value num-cell" style="font-size: 22px; text-align: left;">
                                ₱<fmt:formatNumber value="${ledger.beginningBalance}" type="currency" currencySymbol=""/>
                            </div>
                            <div class="stat-subtext">Prior period carryover</div>
                        </div>

                        <div class="stat-card">
                            <div class="stat-label">Period Activity</div>
                            <div style="font-size: 13px; margin-top: 8px; font-family: var(--font-mono);">
                                <div>Debits: <span style="font-weight: 600;">₱<fmt:formatNumber value="${ledger.totalDebit}" type="currency" currencySymbol=""/></span></div>
                                <div style="margin-top: 2px;">Credits: <span style="font-weight: 600;">₱<fmt:formatNumber value="${ledger.totalCredit}" type="currency" currencySymbol=""/></span></div>
                            </div>
                        </div>

                        <div class="stat-card" style="border-left: 3px solid var(--text-primary);">
                            <div class="stat-label">Ending Balance</div>
                            <div class="stat-value num-cell" style="font-size: 24px; text-align: left; color: var(--text-primary);">
                                ₱<fmt:formatNumber value="${ledger.endingBalance}" type="currency" currencySymbol=""/>
                            </div>
                            <div class="stat-subtext">${ledger.account.accountType.displayName} Net Position</div>
                        </div>
                    </div>

                    <!-- Postings Ledger Table -->
                    <div class="geist-table-container">
                        <table class="geist-table">
                            <thead>
                                <tr>
                                    <th style="width: 100px;">Date</th>
                                    <th style="width: 120px;">Reference</th>
                                    <th>Particulars</th>
                                    <th style="width: 140px; text-align: right;">Debit (₱)</th>
                                    <th style="width: 140px; text-align: right;">Credit (₱)</th>
                                    <th style="width: 150px; text-align: right;">Running Balance (₱)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <!-- Beginning Balance Row -->
                                <tr style="background-color: var(--bg-subtle); font-style: italic; color: var(--text-secondary);">
                                    <td style="font-family: var(--font-mono); font-size: 12px;">
                                        ${startDate != null && !startDate.isEmpty() ? startDate : 'Beginning'}
                                    </td>
                                    <td>—</td>
                                    <td>Beginning Balance Brought Forward</td>
                                    <td class="num-cell">—</td>
                                    <td class="num-cell">—</td>
                                    <td class="num-cell" style="font-weight: 600; color: var(--text-primary);">
                                        <fmt:formatNumber value="${ledger.beginningBalance}" type="currency" currencySymbol=""/>
                                    </td>
                                </tr>

                                <c:choose>
                                    <c:when test="${empty ledger.entries}">
                                        <tr>
                                            <td colspan="6" style="text-align: center; padding: 32px 16px; color: var(--text-tertiary);">
                                                No transactions posted to this account during the selected period.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="entry" items="${ledger.entries}">
                                            <tr>
                                                <td style="font-family: var(--font-mono); font-size: 12px;">
                                                    <fmt:formatDate value="${entry.entryDate}" pattern="yyyy-MM-dd"/>
                                                </td>
                                                <td style="font-family: var(--font-mono); font-size: 12px; font-weight: 500;">
                                                    <a href="${pageContext.request.contextPath}/journals?action=view&id=${entry.journalEntryId}" 
                                                       style="color: inherit; text-decoration: underline; text-decoration-color: var(--border-color);">
                                                        ${entry.referenceNumber != null && !entry.referenceNumber.isEmpty() ? entry.referenceNumber : ('JE-'.concat(entry.journalEntryId))}
                                                    </a>
                                                </td>
                                                <td>
                                                    <span style="font-weight: 500;">${entry.particulars}</span>
                                                </td>
                                                <td class="num-cell">
                                                    <c:if test="${entry.debitPositive}">
                                                        <fmt:formatNumber value="${entry.debit}" type="currency" currencySymbol=""/>
                                                    </c:if>
                                                </td>
                                                <td class="num-cell">
                                                    <c:if test="${entry.creditPositive}">
                                                        <fmt:formatNumber value="${entry.credit}" type="currency" currencySymbol=""/>
                                                    </c:if>
                                                </td>
                                                <td class="num-cell" style="font-weight: 600;">
                                                    <fmt:formatNumber value="${entry.runningBalance}" type="currency" currencySymbol=""/>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                            <tfoot>
                                <tr style="background-color: var(--bg-subtle); font-weight: 600;">
                                    <td colspan="3" style="text-align: right; padding-right: 16px;">
                                        Period Totals & Ending Balance:
                                    </td>
                                    <td class="num-cell">
                                        <fmt:formatNumber value="${ledger.totalDebit}" type="currency" currencySymbol=""/>
                                    </td>
                                    <td class="num-cell">
                                        <fmt:formatNumber value="${ledger.totalCredit}" type="currency" currencySymbol=""/>
                                    </td>
                                    <td class="num-cell" style="font-size: 14px; color: var(--text-primary);">
                                        <fmt:formatNumber value="${ledger.endingBalance}" type="currency" currencySymbol=""/>
                                    </td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="geist-card" style="text-align: center; padding: 48px 24px;">
                        <p style="color: var(--text-secondary);">Please select an account from the dropdown above to inspect its General Ledger ledger.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </main>
    </div>
</div>
</body>
</html>
