<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="journals" scope="request"/>
<c:set var="pageTitle" value="Journal Entries" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Journal Entries — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="/pages/common/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/pages/common/header.jsp"/>

        <main class="content-body">
            <!-- Header & Action Row -->
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">Journal Entries</h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Manage transaction vouchers and double-entry general journal records.
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/journals?action=new" class="btn btn-primary">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"></line>
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                        </svg>
                        <span>New Entry</span>
                    </a>
                </div>
            </div>

            <!-- Flash Success / Error messages -->
            <c:if test="${param.success == 'created'}">
                <div class="alert alert-success">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"></path></svg>
                    <span>Journal entry recorded successfully.</span>
                </div>
            </c:if>
            <c:if test="${param.success == 'updated'}">
                <div class="alert alert-success">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"></path></svg>
                    <span>Journal entry updated successfully.</span>
                </div>
            </c:if>
            <c:if test="${param.success == 'deleted'}">
                <div class="alert alert-success">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"></path></svg>
                    <span>Journal entry deleted successfully.</span>
                </div>
            </c:if>

            <!-- Filter Toolbar -->
            <div class="geist-card" style="margin-bottom: 20px;">
                <div class="geist-card-body" style="padding: 16px 20px;">
                    <form action="${pageContext.request.contextPath}/journals" method="get" 
                          style="display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end;">
                        <input type="hidden" name="action" value="list">
                        
                        <div style="flex: 1; min-width: 150px;">
                            <label class="form-label" style="font-size: 11px;">Start Date</label>
                            <input type="date" name="startDate" value="${startDate}" class="form-input">
                        </div>

                        <div style="flex: 1; min-width: 150px;">
                            <label class="form-label" style="font-size: 11px;">End Date</label>
                            <input type="date" name="endDate" value="${endDate}" class="form-input">
                        </div>

                        <div style="flex: 2; min-width: 200px;">
                            <label class="form-label" style="font-size: 11px;">Search Particulars / Ref</label>
                            <input type="text" name="search" value="${search}" class="form-input" placeholder="Keyword or reference...">
                        </div>

                        <div style="display: flex; gap: 8px;">
                            <button type="submit" class="btn btn-secondary">
                                <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                                    <circle cx="11" cy="11" r="8"></circle>
                                    <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                                </svg>
                                <span>Filter</span>
                            </button>
                            <a href="${pageContext.request.contextPath}/journals" class="btn btn-secondary" title="Reset Filters">
                                Reset
                            </a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Entries Table -->
            <div class="geist-table-container">
                <table class="geist-table">
                    <thead>
                        <tr>
                            <th style="width: 90px;">Date</th>
                            <th style="width: 120px;">Ref #</th>
                            <th>Particulars</th>
                            <th style="width: 130px; text-align: right;">Debit (₱)</th>
                            <th style="width: 130px; text-align: right;">Credit (₱)</th>
                            <th style="width: 120px;">Created By</th>
                            <th style="width: 140px; text-align: right;">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty entries}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 48px 16px; color: var(--text-tertiary);">
                                        No journal entries found matching criteria.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="entry" items="${entries}">
                                    <tr>
                                        <td style="font-family: var(--font-mono); font-size: 12px;">
                                            <fmt:formatDate value="${entry.entryDate}" pattern="yyyy-MM-dd"/>
                                        </td>
                                        <td style="font-family: var(--font-mono); font-size: 12px; font-weight: 500;">
                                            <a href="${pageContext.request.contextPath}/journals?action=view&id=${entry.id}" style="color: inherit; text-decoration: underline; text-decoration-color: var(--border-color);">
                                                ${entry.referenceNumber != null && !entry.referenceNumber.isEmpty() ? entry.referenceNumber : ('JE-'.concat(entry.id))}
                                            </a>
                                        </td>
                                        <td>
                                            <div style="font-weight: 500;">${entry.particulars}</div>
                                        </td>
                                        <td class="num-cell">
                                            <fmt:formatNumber value="${entry.totalDebit}" type="currency" currencySymbol=""/>
                                        </td>
                                        <td class="num-cell">
                                            <fmt:formatNumber value="${entry.totalCredit}" type="currency" currencySymbol=""/>
                                        </td>
                                        <td style="color: var(--text-secondary); font-size: 12px;">
                                            ${entry.creatorName != null ? entry.creatorName : 'System'}
                                        </td>
                                        <td style="text-align: right;">
                                            <div style="display: inline-flex; gap: 4px;">
                                                <a href="${pageContext.request.contextPath}/journals?action=view&id=${entry.id}" 
                                                   class="btn btn-secondary btn-sm" title="View Voucher">
                                                    View
                                                </a>
                                                <a href="${pageContext.request.contextPath}/journals?action=edit&id=${entry.id}" 
                                                   class="btn btn-secondary btn-sm" title="Edit Entry">
                                                    Edit
                                                </a>
                                                <form action="${pageContext.request.contextPath}/journals" method="post" 
                                                      style="display: inline;" 
                                                      onsubmit="return confirm('Are you sure you want to delete this journal entry? This will permanently remove its debit and credit postings.');">
                                                    <input type="hidden" name="action" value="delete">
                                                    <input type="hidden" name="id" value="${entry.id}">
                                                    <button type="submit" class="btn btn-danger btn-sm" title="Delete">
                                                        Del
                                                    </button>
                                                </form>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                    <c:if test="${not empty entries}">
                        <tfoot>
                            <tr style="background-color: var(--bg-subtle); font-weight: 600;">
                                <td colspan="3" style="text-align: right; padding-right: 16px;">
                                    Grand Totals (${entries.size()} entries):
                                </td>
                                <td class="num-cell">
                                    <fmt:formatNumber value="${grandTotalDebit}" type="currency" currencySymbol=""/>
                                </td>
                                <td class="num-cell">
                                    <fmt:formatNumber value="${grandTotalCredit}" type="currency" currencySymbol=""/>
                                </td>
                                <td colspan="2">
                                    <c:choose>
                                        <c:when test="${isTotalsBalanced}">
                                            <span class="pill pill-revenue" style="font-size: 10px;">Balanced</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="pill pill-expense" style="font-size: 10px;">Unbalanced</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </tfoot>
                    </c:if>
                </table>
            </div>
        </main>
    </div>
</div>
</body>
</html>
