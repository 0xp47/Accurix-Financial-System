<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="balance_sheet" scope="request"/>
<c:set var="pageTitle" value="Balance Sheet" scope="request"/>
<c:set var="isReportPage" value="true" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Balance Sheet — Accurix Financial</title>
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
                    <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">Balance Sheet</h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Statement of Financial Position: Assets = Liabilities + Equity
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
                <h1 style="font-size: 22px; font-weight: 700;">BALANCE SHEET (STATEMENT OF FINANCIAL POSITION)</h1>
                <p style="font-size: 13px; color: #555; margin-top: 4px;">
                    As of: ${not empty asOfDate ? asOfDate : 'Current Cumulative'}
                </p>
            </div>

            <!-- Filter Toolbar (hidden when printing) -->
            <div class="geist-card no-print" style="margin-bottom: 20px;">
                <div class="geist-card-body" style="padding: 16px 20px;">
                    <form action="${pageContext.request.contextPath}/reports" method="get" 
                          style="display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end;">
                        <input type="hidden" name="type" value="balance_sheet">

                        <div style="flex: 1; min-width: 200px;">
                            <label class="form-label" style="font-size: 11px;">Reporting Date (As Of)</label>
                            <input type="date" name="asOfDate" value="${asOfDate}" class="form-input">
                        </div>

                        <div style="display: flex; gap: 8px;">
                            <button type="submit" class="btn btn-secondary">Apply Date</button>
                            <a href="${pageContext.request.contextPath}/reports?type=balance_sheet" class="btn btn-secondary">Current</a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Accounting Equation Status Banner -->
            <div style="margin-bottom: 20px;">
                <c:choose>
                    <c:when test="${sheet.balanced}">
                        <div class="alert alert-success" style="margin-bottom: 0;">
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                                <polyline points="22 4 12 14.01 9 11.01"></polyline>
                            </svg>
                            <span>
                                <strong>Accounting Equation Balanced:</strong>
                                Total Assets (₱<fmt:formatNumber value="${sheet.totalAssets}" type="currency" currencySymbol=""/>) = 
                                Liabilities (₱<fmt:formatNumber value="${sheet.totalLiabilities}" type="currency" currencySymbol=""/>) + 
                                Equity (₱<fmt:formatNumber value="${sheet.totalEquity.add(sheet.currentNetIncome)}" type="currency" currencySymbol=""/>).
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
                                <strong>Warning: Accounting Equation Out of Balance.</strong>
                                Discrepancy is ₱<fmt:formatNumber value="${sheet.difference}" type="currency" currencySymbol=""/>.
                            </span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(450px, 1fr)); gap: 24px;">
                <!-- 1. Assets Column Card -->
                <div class="geist-card">
                    <div class="geist-card-header">
                        <div class="geist-card-title">Assets</div>
                        <span class="pill pill-asset">Economic Resources</span>
                    </div>

                    <table class="geist-table">
                        <thead>
                            <tr>
                                <th style="width: 100px;">Code</th>
                                <th>Account Title</th>
                                <th style="width: 150px; text-align: right;">Amount (₱)</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty sheet.assets}">
                                    <tr>
                                        <td colspan="3" style="text-align: center; color: var(--text-tertiary); padding: 24px;">
                                            No asset balances recorded.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="item" items="${sheet.assets}">
                                        <tr>
                                            <td style="font-family: var(--font-mono); font-size: 12px;">${item.accountCode}</td>
                                            <td>${item.accountName}</td>
                                            <td class="num-cell">
                                                <fmt:formatNumber value="${item.amount}" type="currency" currencySymbol=""/>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                        <tfoot>
                            <tr style="background-color: var(--bg-subtle); font-weight: 700; border-top: 2px solid var(--border-strong);">
                                <td colspan="2" style="text-align: right; padding-right: 16px;">
                                    TOTAL ASSETS:
                                </td>
                                <td class="num-cell" style="font-size: 15px; border-bottom: 3px double var(--text-primary);">
                                    ₱<fmt:formatNumber value="${sheet.totalAssets}" type="currency" currencySymbol=""/>
                                </td>
                            </tr>
                        </tfoot>
                    </table>
                </div>

                <!-- 2. Liabilities & Equity Column Card -->
                <div style="display: flex; flex-direction: column; gap: 24px;">
                    <!-- Liabilities Card -->
                    <div class="geist-card">
                        <div class="geist-card-header">
                            <div class="geist-card-title">Liabilities</div>
                            <span class="pill pill-liability">Obligations & Debts</span>
                        </div>
                        <table class="geist-table">
                            <thead>
                                <tr>
                                    <th style="width: 100px;">Code</th>
                                    <th>Account Title</th>
                                    <th style="width: 150px; text-align: right;">Amount (₱)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${empty sheet.liabilities}">
                                        <tr>
                                            <td colspan="3" style="text-align: center; color: var(--text-tertiary); padding: 20px;">
                                                No liability obligations recorded.
                                            </td>
                                        </tr>
                                    </c:when>
                                    <c:otherwise>
                                        <c:forEach var="item" items="${sheet.liabilities}">
                                            <tr>
                                                <td style="font-family: var(--font-mono); font-size: 12px;">${item.accountCode}</td>
                                                <td>${item.accountName}</td>
                                                <td class="num-cell">
                                                    <fmt:formatNumber value="${item.amount}" type="currency" currencySymbol=""/>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                            <tfoot>
                                <tr style="background-color: var(--bg-subtle); font-weight: 600;">
                                    <td colspan="2" style="text-align: right; padding-right: 16px;">
                                        Total Liabilities:
                                    </td>
                                    <td class="num-cell" style="font-size: 14px;">
                                        ₱<fmt:formatNumber value="${sheet.totalLiabilities}" type="currency" currencySymbol=""/>
                                    </td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>

                    <!-- Equity Card -->
                    <div class="geist-card">
                        <div class="geist-card-header">
                            <div class="geist-card-title">Owner's Equity</div>
                            <span class="pill pill-equity">Residual Interest</span>
                        </div>
                        <table class="geist-table">
                            <thead>
                                <tr>
                                    <th style="width: 100px;">Code</th>
                                    <th>Account Title</th>
                                    <th style="width: 150px; text-align: right;">Amount (₱)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${sheet.equityItems}">
                                    <tr>
                                        <td style="font-family: var(--font-mono); font-size: 12px;">${item.accountCode}</td>
                                        <td>${item.accountName}</td>
                                        <td class="num-cell">
                                            <fmt:formatNumber value="${item.amount}" type="currency" currencySymbol=""/>
                                        </td>
                                    </tr>
                                </c:forEach>

                                <!-- Current Net Income Integration -->
                                <tr style="background-color: rgba(59, 130, 246, 0.04);">
                                    <td style="font-family: var(--font-mono); font-size: 12px; color: var(--text-tertiary);">RE-CURR</td>
                                    <td>
                                        <span style="font-weight: 500;">Current Period Net Income (Retained Earnings)</span>
                                    </td>
                                    <td class="num-cell" style="font-weight: 600; color: ${sheet.currentNetIncomeNonNegative ? '#059669' : '#dc2626'};">
                                        <fmt:formatNumber value="${sheet.currentNetIncome}" type="currency" currencySymbol=""/>
                                    </td>
                                </tr>
                            </tbody>
                            <tfoot>
                                <tr style="background-color: var(--bg-subtle); font-weight: 600;">
                                    <td colspan="2" style="text-align: right; padding-right: 16px;">
                                        Total Equity:
                                    </td>
                                    <td class="num-cell" style="font-size: 14px;">
                                        ₱<fmt:formatNumber value="${sheet.totalEquity.add(sheet.currentNetIncome)}" type="currency" currencySymbol=""/>
                                    </td>
                                </tr>
                                <tr style="background-color: var(--bg-subtle); font-weight: 700; border-top: 2px solid var(--border-strong);">
                                    <td colspan="2" style="text-align: right; padding-right: 16px;">
                                        TOTAL LIABILITIES & EQUITY:
                                    </td>
                                    <td class="num-cell" style="font-size: 15px; border-bottom: 3px double var(--text-primary);">
                                        ₱<fmt:formatNumber value="${sheet.totalLiabilitiesAndEquity}" type="currency" currencySymbol=""/>
                                    </td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
