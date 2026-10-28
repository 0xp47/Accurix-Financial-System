<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="dashboard" scope="request"/>
<c:set var="pageTitle" value="Overview" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="/pages/common/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/pages/common/header.jsp"/>

        <main class="content-body">
            <!-- Header Banner -->
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 22px; font-weight: 600; letter-spacing: -0.02em;">
                        Welcome back, ${sessionScope.fullName != null ? sessionScope.fullName : sessionScope.username}
                    </h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Accounting system summary and recent transaction ledger activity.
                    </p>
                </div>
                <div style="display: flex; gap: 8px;">
                    <a href="${pageContext.request.contextPath}/journals?action=new" class="btn btn-primary">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"></line>
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                        </svg>
                        <span>New Entry</span>
                    </a>
                    <a href="${pageContext.request.contextPath}/reports?type=trial_balance" class="btn btn-secondary">
                        Trial Balance
                    </a>
                </div>
            </div>

            <!-- Equilibrium Status Banner -->
            <div style="margin-bottom: 24px;">
                <c:choose>
                    <c:when test="${isBalanced}">
                        <div class="alert alert-success" style="margin-bottom: 0;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                                <polyline points="22 4 12 14.01 9 11.01"></polyline>
                            </svg>
                            <span>
                                <strong>System in Equilibrium:</strong> Total System Debits (₱<fmt:formatNumber value="${totalDebit}" type="currency" currencySymbol=""/>) 
                                perfectly match Total System Credits (₱<fmt:formatNumber value="${totalCredit}" type="currency" currencySymbol=""/>).
                            </span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-error" style="margin-bottom: 0;">
                            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                <circle cx="12" cy="12" r="10"></circle>
                                <line x1="12" y1="8" x2="12" y2="12"></line>
                                <line x1="12" y1="16" x2="12.01" y2="16"></line>
                            </svg>
                            <span><strong>System Imbalance Alert:</strong> Total debits do not equal total credits. Review recent journal entries.</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

            <!-- KPI Metric Grid -->
            <div class="grid-stats">
                <div class="stat-card">
                    <div class="stat-label">Total Journal Entries</div>
                    <div class="stat-value num-cell" style="text-align: left;">
                        ${totalEntries}
                    </div>
                    <div class="stat-subtext">Recorded transaction vouchers</div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Chart of Accounts</div>
                    <div class="stat-value num-cell" style="text-align: left;">
                        ${totalAccounts}
                    </div>
                    <div class="stat-subtext">Active ledger accounts</div>
                </div>

                <div class="stat-card" style="border-left: 3px solid ${isNetProfit ? '#059669' : '#dc2626'};">
                    <div class="stat-label">Net Operating Income</div>
                    <div class="stat-value num-cell" style="text-align: left; color: ${isNetProfit ? '#059669' : '#dc2626'};">
                        ₱<fmt:formatNumber value="${netIncome}" type="currency" currencySymbol=""/>
                    </div>
                    <div class="stat-subtext">
                        <c:choose>
                            <c:when test="${isNetProfit}">
                                <span class="pill pill-revenue" style="font-size: 10px;">Net Profit</span>
                            </c:when>
                            <c:otherwise>
                                <span class="pill pill-expense" style="font-size: 10px;">Net Loss</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="stat-card">
                    <div class="stat-label">Cumulative Volume</div>
                    <div class="stat-value num-cell" style="text-align: left; font-size: 20px;">
                        ₱<fmt:formatNumber value="${totalDebit}" type="currency" currencySymbol=""/>
                    </div>
                    <div class="stat-subtext">Total debits posted</div>
                </div>
            </div>

            <!-- Recent Transactions Card -->
            <div class="geist-card">
                <div class="geist-card-header">
                    <div>
                        <div class="geist-card-title">Recent Journal Entries</div>
                        <div class="geist-card-subtitle">Latest postings added to the accounting ledger</div>
                    </div>
                    <a href="${pageContext.request.contextPath}/journals" class="btn btn-secondary btn-sm">
                        View All Entries &rarr;
                    </a>
                </div>

                <div class="geist-table-container" style="border: none; border-radius: 0;">
                    <table class="geist-table">
                        <thead>
                            <tr>
                                <th style="width: 100px;">Date</th>
                                <th style="width: 120px;">Reference</th>
                                <th>Particulars</th>
                                <th style="width: 140px; text-align: right;">Amount (₱)</th>
                                <th style="width: 120px;">Posted By</th>
                                <th style="width: 100px; text-align: right;">Action</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${empty recentEntries}">
                                    <tr>
                                        <td colspan="6" style="text-align: center; padding: 32px; color: var(--text-tertiary);">
                                            No transactions recorded yet. Click "New Entry" to create one.
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:forEach var="entry" items="${recentEntries}">
                                        <tr>
                                            <td style="font-family: var(--font-mono); font-size: 12px;">
                                                <fmt:formatDate value="${entry.entryDate}" pattern="yyyy-MM-dd"/>
                                            </td>
                                            <td style="font-family: var(--font-mono); font-size: 12px; font-weight: 500;">
                                                ${entry.referenceNumber != null && !entry.referenceNumber.isEmpty() ? entry.referenceNumber : ('JE-'.concat(entry.id))}
                                            </td>
                                            <td>
                                                <div style="font-weight: 500;">${entry.particulars}</div>
                                            </td>
                                            <td class="num-cell">
                                                <fmt:formatNumber value="${entry.totalDebit}" type="currency" currencySymbol=""/>
                                            </td>
                                            <td style="font-size: 12px; color: var(--text-secondary);">
                                                ${entry.creatorName != null ? entry.creatorName : 'System'}
                                            </td>
                                            <td style="text-align: right;">
                                                <a href="${pageContext.request.contextPath}/journals?action=view&id=${entry.id}" 
                                                   class="btn btn-secondary btn-sm">
                                                    View
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>
            </div>

            <!-- Quick Modules Grid -->
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(280px, 1fr)); gap: 16px;">
                <div class="geist-card" style="margin-bottom: 0;">
                    <div class="geist-card-body">
                        <div style="font-size: 14px; font-weight: 600; margin-bottom: 4px;">General Ledger</div>
                        <p style="font-size: 12px; color: var(--text-secondary); margin-bottom: 12px;">
                            Inspect account running balances, posting histories, and date ranges.
                        </p>
                        <a href="${pageContext.request.contextPath}/ledger" class="btn btn-secondary btn-sm" style="width: 100%;">
                            Open Ledger &rarr;
                        </a>
                    </div>
                </div>

                <div class="geist-card" style="margin-bottom: 0;">
                    <div class="geist-card-body">
                        <div style="font-size: 14px; font-weight: 600; margin-bottom: 4px;">Chart of Accounts</div>
                        <p style="font-size: 12px; color: var(--text-secondary); margin-bottom: 12px;">
                            Review account numbers, classifications, and operational codes.
                        </p>
                        <a href="${pageContext.request.contextPath}/accounts" class="btn btn-secondary btn-sm" style="width: 100%;">
                            Manage Accounts &rarr;
                        </a>
                    </div>
                </div>

                <div class="geist-card" style="margin-bottom: 0;">
                    <div class="geist-card-body">
                        <div style="font-size: 14px; font-weight: 600; margin-bottom: 4px;">Balance Sheet</div>
                        <p style="font-size: 12px; color: var(--text-secondary); margin-bottom: 12px;">
                            Evaluate financial position, assets, liabilities, and owner's equity.
                        </p>
                        <a href="${pageContext.request.contextPath}/reports?type=balance_sheet" class="btn btn-secondary btn-sm" style="width: 100%;">
                            View Balance Sheet &rarr;
                        </a>
                    </div>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
