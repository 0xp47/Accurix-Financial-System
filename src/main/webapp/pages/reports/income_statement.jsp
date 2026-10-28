<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="income_statement" scope="request"/>
<c:set var="pageTitle" value="Income Statement" scope="request"/>
<c:set var="isReportPage" value="true" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Income Statement — Accurix Financial</title>
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
                    <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">Income Statement</h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Statement of Profit or Loss: Operating revenues, expenses, and net profit.
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
                <h1 style="font-size: 22px; font-weight: 700;">INCOME STATEMENT</h1>
                <p style="font-size: 13px; color: #555; margin-top: 4px;">
                    Reporting Period: ${not empty startDate ? startDate : 'Beginning'} to ${not empty endDate ? endDate : 'Present'}
                </p>
            </div>

            <!-- Filter Toolbar (hidden when printing) -->
            <div class="geist-card no-print" style="margin-bottom: 20px;">
                <div class="geist-card-body" style="padding: 16px 20px;">
                    <form action="${pageContext.request.contextPath}/reports" method="get" 
                          style="display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end;">
                        <input type="hidden" name="type" value="income_statement">

                        <div style="flex: 1; min-width: 160px;">
                            <label class="form-label" style="font-size: 11px;">Period Start Date</label>
                            <input type="date" name="startDate" value="${startDate}" class="form-input">
                        </div>

                        <div style="flex: 1; min-width: 160px;">
                            <label class="form-label" style="font-size: 11px;">Period End Date</label>
                            <input type="date" name="endDate" value="${endDate}" class="form-input">
                        </div>

                        <div style="display: flex; gap: 8px;">
                            <button type="submit" class="btn btn-secondary">Apply Period</button>
                            <a href="${pageContext.request.contextPath}/reports?type=income_statement" class="btn btn-secondary">All Time</a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Net Income Summary KPI Card -->
            <div class="grid-stats">
                <div class="stat-card">
                    <div class="stat-label">Total Revenues</div>
                    <div class="stat-value num-cell" style="color: #059669; text-align: left;">
                        ₱<fmt:formatNumber value="${statement.totalRevenue}" type="currency" currencySymbol=""/>
                    </div>
                    <div class="stat-subtext">Gross billings & fees</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Total Expenses</div>
                    <div class="stat-value num-cell" style="color: #dc2626; text-align: left;">
                        ₱<fmt:formatNumber value="${statement.totalExpense}" type="currency" currencySymbol=""/>
                    </div>
                    <div class="stat-subtext">Operating expenditures</div>
                </div>

                <div class="stat-card" style="border-left: 3px solid ${statement.netProfit ? '#059669' : '#dc2626'};">
                    <div class="stat-label">Net Income (Net Profit / Loss)</div>
                    <div class="stat-value num-cell" style="text-align: left; color: ${statement.netProfit ? '#059669' : '#dc2626'};">
                        ₱<fmt:formatNumber value="${statement.netIncome}" type="currency" currencySymbol=""/>
                    </div>
                    <div class="stat-subtext">
                        <c:choose>
                            <c:when test="${statement.netProfit}">
                                <span class="pill pill-revenue" style="font-size: 10px;">Net Operating Profit</span>
                            </c:when>
                            <c:otherwise>
                                <span class="pill pill-expense" style="font-size: 10px;">Net Operating Loss</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <!-- Detailed Income Statement Structure -->
            <div class="geist-card">
                <div class="geist-card-header">
                    <div class="geist-card-title">Statement Breakdown</div>
                </div>

                <table class="geist-table">
                    <!-- 1. Revenue Section -->
                    <thead>
                        <tr style="background-color: var(--bg-subtle);">
                            <th colspan="2" style="font-size: 13px; font-weight: 600; text-transform: none; color: var(--text-primary);">
                                Operating Revenues
                            </th>
                            <th style="width: 200px; text-align: right;">Amount (₱)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty statement.revenues}">
                                <tr>
                                    <td colspan="3" style="font-style: italic; color: var(--text-tertiary); padding-left: 32px;">
                                        No revenue transactions recorded in this period.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="item" items="${statement.revenues}">
                                    <tr>
                                        <td style="width: 120px; font-family: var(--font-mono); font-size: 12px; padding-left: 32px;">
                                            ${item.accountCode}
                                        </td>
                                        <td>${item.accountName}</td>
                                        <td class="num-cell">
                                            <fmt:formatNumber value="${item.amount}" type="currency" currencySymbol=""/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        <tr style="font-weight: 600; background-color: rgba(16, 185, 129, 0.05);">
                            <td colspan="2" style="text-align: right; padding-right: 16px;">
                                Total Operating Revenue:
                            </td>
                            <td class="num-cell" style="font-size: 14px; color: #059669;">
                                ₱<fmt:formatNumber value="${statement.totalRevenue}" type="currency" currencySymbol=""/>
                            </td>
                        </tr>
                    </tbody>

                    <!-- 2. Expense Section -->
                    <thead>
                        <tr style="background-color: var(--bg-subtle); border-top: 2px solid var(--border-color);">
                            <th colspan="2" style="font-size: 13px; font-weight: 600; text-transform: none; color: var(--text-primary);">
                                Operating Expenses
                            </th>
                            <th style="width: 200px; text-align: right;">Amount (₱)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty statement.expenses}">
                                <tr>
                                    <td colspan="3" style="font-style: italic; color: var(--text-tertiary); padding-left: 32px;">
                                        No expense transactions recorded in this period.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="item" items="${statement.expenses}">
                                    <tr>
                                        <td style="width: 120px; font-family: var(--font-mono); font-size: 12px; padding-left: 32px;">
                                            ${item.accountCode}
                                        </td>
                                        <td>${item.accountName}</td>
                                        <td class="num-cell">
                                            <fmt:formatNumber value="${item.amount}" type="currency" currencySymbol=""/>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                        <tr style="font-weight: 600; background-color: rgba(239, 68, 68, 0.05);">
                            <td colspan="2" style="text-align: right; padding-right: 16px;">
                                Total Operating Expenses:
                            </td>
                            <td class="num-cell" style="font-size: 14px; color: #dc2626;">
                                ₱<fmt:formatNumber value="${statement.totalExpense}" type="currency" currencySymbol=""/>
                            </td>
                        </tr>
                    </tbody>

                    <!-- 3. Bottom Net Income Result -->
                    <tfoot>
                        <tr style="background-color: var(--bg-subtle); font-weight: 700; border-top: 2px solid var(--border-strong);">
                            <td colspan="2" style="text-align: right; padding-right: 16px; font-size: 14px;">
                                NET INCOME (Total Revenue - Total Expenses):
                            </td>
                            <td class="num-cell" style="font-size: 16px; border-bottom: 3px double var(--text-primary); color: ${statement.netProfit ? '#059669' : '#dc2626'};">
                                ₱<fmt:formatNumber value="${statement.netIncome}" type="currency" currencySymbol=""/>
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
