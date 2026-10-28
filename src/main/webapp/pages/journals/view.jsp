<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="journals" scope="request"/>
<c:set var="pageTitle" value="Journal Voucher #${entry.id}" scope="request"/>
<c:set var="isReportPage" value="true" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Journal Voucher #${entry.id} — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="/pages/common/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/pages/common/header.jsp"/>

        <main class="content-body">
            <!-- Action Navigation (hidden when printing) -->
            <div class="no-print" style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">
                        Journal Voucher #${entry.id}
                    </h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Transaction record details and double-entry breakdown.
                    </p>
                </div>
                <div style="display: flex; gap: 8px;">
                    <a href="${pageContext.request.contextPath}/journals?action=edit&id=${entry.id}" class="btn btn-secondary">
                        Edit Voucher
                    </a>
                    <button onclick="window.print()" class="btn btn-secondary">
                        Print Voucher
                    </button>
                    <a href="${pageContext.request.contextPath}/journals" class="btn btn-primary">
                        &larr; Journal List
                    </a>
                </div>
            </div>

            <!-- Print Header (shown only when printing) -->
            <div class="print-header" style="display: none;">
                <h1 style="font-size: 20px; font-weight: 700;">GENERAL JOURNAL VOUCHER</h1>
                <p style="font-size: 12px; color: #555;">Accounting System Report</p>
            </div>

            <!-- Transaction Metadata Card -->
            <div class="geist-card">
                <div class="geist-card-header">
                    <div class="geist-card-title">Voucher Overview</div>
                    <span class="pill pill-neutral" style="font-family: var(--font-mono);">
                        ${entry.referenceNumber != null && !entry.referenceNumber.isEmpty() ? entry.referenceNumber : ('JE-'.concat(entry.id))}
                    </span>
                </div>
                <div class="geist-card-body">
                    <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px;">
                        <div>
                            <div class="form-label" style="font-size: 11px; color: var(--text-tertiary);">TRANSACTION DATE</div>
                            <div style="font-family: var(--font-mono); font-size: 14px; font-weight: 500;">
                                <fmt:formatDate value="${entry.entryDate}" pattern="yyyy-MM-dd"/>
                            </div>
                        </div>

                        <div>
                            <div class="form-label" style="font-size: 11px; color: var(--text-tertiary);">RECORDED BY</div>
                            <div style="font-size: 14px; font-weight: 500;">
                                ${entry.creatorName != null ? entry.creatorName : 'System'}
                            </div>
                        </div>

                        <div>
                            <div class="form-label" style="font-size: 11px; color: var(--text-tertiary);">TIMESTAMP</div>
                            <div style="font-family: var(--font-mono); font-size: 12px; color: var(--text-secondary);">
                                <fmt:formatDate value="${entry.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/>
                            </div>
                        </div>
                    </div>

                    <div style="margin-top: 16px; padding-top: 16px; border-top: 1px solid var(--border-color);">
                        <div class="form-label" style="font-size: 11px; color: var(--text-tertiary);">PARTICULARS / DESCRIPTION</div>
                        <div style="font-size: 14px; line-height: 1.5; color: var(--text-primary); font-weight: 500;">
                            ${entry.particulars}
                        </div>
                    </div>
                </div>
            </div>

            <!-- Debit/Credit Breakdown Table -->
            <div class="geist-table-container">
                <table class="geist-table">
                    <thead>
                        <tr>
                            <th style="width: 120px;">Code</th>
                            <th>Account Name</th>
                            <th style="width: 120px;">Category</th>
                            <th style="width: 150px; text-align: right;">Debit (₱)</th>
                            <th style="width: 150px; text-align: right;">Credit (₱)</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="line" items="${entry.lines}">
                            <tr>
                                <td style="font-family: var(--font-mono); font-weight: 500;">
                                    ${line.accountCode}
                                </td>
                                <td>
                                    <span style="${line.creditPositive ? 'padding-left: 24px;' : 'font-weight: 500;'}">
                                        ${line.accountName}
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${line.accountType == 'ASSET'}">
                                            <span class="pill pill-asset">Asset</span>
                                        </c:when>
                                        <c:when test="${line.accountType == 'LIABILITY'}">
                                            <span class="pill pill-liability">Liability</span>
                                        </c:when>
                                        <c:when test="${line.accountType == 'EQUITY'}">
                                            <span class="pill pill-equity">Equity</span>
                                        </c:when>
                                        <c:when test="${line.accountType == 'REVENUE'}">
                                            <span class="pill pill-revenue">Revenue</span>
                                        </c:when>
                                        <c:when test="${line.accountType == 'EXPENSE'}">
                                            <span class="pill pill-expense">Expense</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="pill pill-neutral">${line.accountType}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="num-cell">
                                    <c:if test="${line.debitPositive}">
                                        <fmt:formatNumber value="${line.debit}" type="currency" currencySymbol=""/>
                                    </c:if>
                                </td>
                                <td class="num-cell">
                                    <c:if test="${line.creditPositive}">
                                        <fmt:formatNumber value="${line.credit}" type="currency" currencySymbol=""/>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                    <tfoot>
                        <tr style="background-color: var(--bg-subtle); font-weight: 600;">
                            <td colspan="3" style="text-align: right; padding-right: 16px;">
                                Total:
                            </td>
                            <td class="num-cell">
                                <fmt:formatNumber value="${entry.totalDebit}" type="currency" currencySymbol=""/>
                            </td>
                            <td class="num-cell">
                                <fmt:formatNumber value="${entry.totalCredit}" type="currency" currencySymbol=""/>
                            </td>
                        </tr>
                        <tr style="background-color: var(--bg-surface);">
                            <td colspan="3" style="text-align: right; padding-right: 16px; font-weight: 500;">
                                Double-Entry Verification:
                            </td>
                            <td colspan="2" style="text-align: right;">
                                <c:choose>
                                    <c:when test="${entry.balanced}">
                                        <span class="pill pill-revenue">✓ Balanced (₱${entry.totalDebit})</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="pill pill-expense">⚠ Unbalanced (₱${entry.difference})</span>
                                    </c:otherwise>
                                </c:choose>
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
