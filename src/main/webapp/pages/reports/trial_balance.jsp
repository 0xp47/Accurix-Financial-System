<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="trial_balance" scope="request"/>
<c:set var="pageTitle" value="Trial Balance" scope="request"/>
<c:set var="isReportPage" value="true" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trial Balance — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="/pages/common/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/pages/common/header.jsp"/>

        <main class="content-body">
            <!-- Header (hidden when printing) -->
            <div class="no-print" style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">Trial Balance</h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Verifies mathematical equality of total debits and total credits across all accounts.
                    </p>
                </div>
                <div style="display: flex; gap: 8px;">
                    <button onclick="window.print()" class="btn btn-secondary">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <polyline points="6 9 6 2 18 2 18 9"></polyline>
                            <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                            <rect x="6" y="14" width="12" height="8"></rect>
                        </svg>
                        <span>Print Report</span>
                    </button>
                </div>
            </div>

            <!-- Print Header -->
            <div class="print-header" style="display: none;">
                <h1 style="font-size: 22px; font-weight: 700;">TRIAL BALANCE REPORT</h1>
                <p style="font-size: 13px; color: #555; margin-top: 4px;">
                    As of: ${not empty asOfDate ? asOfDate : 'Current Cumulative'}
                </p>
            </div>

            <!-- Filter Toolbar (hidden when printing) -->
            <div class="geist-card no-print" style="margin-bottom: 20px;">
                <div class="geist-card-body" style="padding: 16px 20px;">
                    <form action="${pageContext.request.contextPath}/reports" method="get" 
                          style="display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end;">
                        <input type="hidden" name="type" value="trial_balance">

                        <div style="flex: 1; min-width: 200px;">
                            <label class="form-label" style="font-size: 11px;">Cut-Off Date (As Of)</label>
                            <input type="date" name="asOfDate" value="${asOfDate}" class="form-input">
                        </div>

                        <div style="display: flex; gap: 8px;">
                            <button type="submit" class="btn btn-secondary">Apply Date</button>
                            <a href="${pageContext.request.contextPath}/reports?type=trial_balance" class="btn btn-secondary">Current</a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Status Banner -->
            <div style="margin-bottom: 20px;">
                <c:choose>
                    <c:when test="${trialBalance.balanced}">
                        <div class="alert alert-success" style="margin-bottom: 0;">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                                <polyline points="22 4 12 14.01 9 11.01"></polyline>
                            </svg>
                            <span>
                                <strong>Trial Balance is in Perfect Equilibrium.</strong> 
                                Total Debit (₱<fmt:formatNumber value="${trialBalance.totalDebit}" type="currency" currencySymbol=""/>) equals 
                                Total Credit (₱<fmt:formatNumber value="${trialBalance.totalCredit}" type="currency" currencySymbol=""/>).
                            </span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-error" style="margin-bottom: 0;">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="8" x2="12" y2="12"></line>
                                <line x1="12" y1="16" x2="12.01" y2="16"></line>
                            </svg>
                            <span>
                                <strong>Trial Balance Discrepancy Detected.</strong> 
                                Difference is ₱<fmt:formatNumber value="${trialBalance.difference}" type="currency" currencySymbol=""/>.
                            </span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- Trial Balance Table -->
            <div class="geist-table-container">
                <table class="geist-table">
                    <thead>
                        <tr>
                            <th style="width: 120px;">Code</th>
                            <th>Account Title</th>
                            <th style="width: 130px;">Classification</th>
                            <th style="width: 160px; text-align: right;">Debit (₱)</th>
                            <th style="width: 160px; text-align: right;">Credit (₱)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty trialBalance.items}">
                                <tr>
                                    <td colspan="5" style="text-align: center; padding: 48px 16px; color: var(--text-tertiary);">
                                        No account balances to display for this period.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="item" items="${trialBalance.items}">
                                    <tr>
                                        <td style="font-family: var(--font-mono); font-weight: 500;">
                                            ${item.accountCode}
                                        </td>
                                        <td>
                                            <span style="font-weight: 500;">${item.accountName}</span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${item.accountType == 'ASSET'}">
                                                    <span class="pill pill-asset">Asset</span>
                                                </c:when>
                                                <c:when test="${item.accountType == 'LIABILITY'}">
                                                    <span class="pill pill-liability">Liability</span>
                                                </c:when>
                                                <c:when test="${item.accountType == 'EQUITY'}">
                                                    <span class="pill pill-equity">Equity</span>
                                                </c:when>
                                                <c:when test="${item.accountType == 'REVENUE'}">
                                                    <span class="pill pill-revenue">Revenue</span>
                                                </c:when>
                                                <c:when test="${item.accountType == 'EXPENSE'}">
                                                    <span class="pill pill-expense">Expense</span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                        <td class="num-cell">
                                            <c:if test="${item.debitPositive}">
                                                <fmt:formatNumber value="${item.debitBalance}" type="currency" currencySymbol=""/>
                                            </c:if>
                                        </td>
                                        <td class="num-cell">
                                            <c:if test="${item.creditPositive}">
                                                <fmt:formatNumber value="${item.creditBalance}" type="currency" currencySymbol=""/>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                    <tfoot>
                        <tr style="background-color: var(--bg-subtle); font-weight: 700; border-top: 2px solid var(--border-strong);">
                            <td colspan="3" style="text-align: right; padding-right: 16px; font-size: 13px;">
                                Total Debits and Credits:
                            </td>
                            <td class="num-cell" style="font-size: 14px; border-bottom: 3px double var(--text-primary);">
                                ₱<fmt:formatNumber value="${trialBalance.totalDebit}" type="currency" currencySymbol=""/>
                            </td>
                            <td class="num-cell" style="font-size: 14px; border-bottom: 3px double var(--text-primary);">
                                ₱<fmt:formatNumber value="${trialBalance.totalCredit}" type="currency" currencySymbol=""/>
                            </td>
                        </tr>
                    </tfoot>
                </table>
            </div>
        </main>
    </div>
</div>
</body>
</html>
