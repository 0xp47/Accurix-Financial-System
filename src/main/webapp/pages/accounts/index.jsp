<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="activePage" value="accounts" scope="request"/>
<c:set var="pageTitle" value="Chart of Accounts" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chart of Accounts — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
    <style>
        .modal-overlay {
            position: fixed; top: 0; left: 0; right: 0; bottom: 0;
            background: rgba(0, 0, 0, 0.4);
            backdrop-filter: blur(2px);
            display: none; align-items: center; justify-content: center;
            z-index: 50;
        }
        .modal-card {
            background: var(--bg-surface);
            width: 100%; max-width: 480px;
            border-radius: var(--radius-lg);
            border: 1px solid var(--border-color);
            box-shadow: var(--shadow-modal);
            overflow: hidden;
        }
    </style>
</head>
<body>
<div class="app-layout">
    <jsp:include page="/pages/common/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/pages/common/header.jsp"/>

        <main class="content-body">
            <!-- Header Row -->
            <div style="display: flex; align-items: center; justify-content: space-between; margin-bottom: 24px;">
                <div>
                    <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">Chart of Accounts</h1>
                    <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                        Master directory of operational accounts, financial classifications, and normal balance rules.
                    </p>
                </div>
                <div>
                    <button type="button" onclick="openModal()" class="btn btn-primary">
                        <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                            <line x1="12" y1="5" x2="12" y2="19"></line>
                            <line x1="5" y1="12" x2="19" y2="12"></line>
                        </svg>
                        <span>Add Account</span>
                    </button>
                </div>
            </div>

            <!-- Alerts -->
            <c:if test="${not empty error}">
                <div class="alert alert-error">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="12" cy="12" r="10"></circle><line x1="12" y1="8" x2="12" y2="12"></line><line x1="12" y1="16" x2="12.01" y2="16"></line></svg>
                    <span>${error}</span>
                </div>
            </c:if>
            <c:if test="${param.success == 'created'}">
                <div class="alert alert-success">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"></path></svg>
                    <span>Account created successfully.</span>
                </div>
            </c:if>
            <c:if test="${param.success == 'updated'}">
                <div class="alert alert-success">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M20 6L9 17l-5-5"></path></svg>
                    <span>Account updated successfully.</span>
                </div>
            </c:if>

            <!-- Search and Filter Toolbar -->
            <div class="geist-card" style="margin-bottom: 20px;">
                <div class="geist-card-body" style="padding: 16px 20px;">
                    <form action="${pageContext.request.contextPath}/accounts" method="get" 
                          style="display: flex; flex-wrap: wrap; gap: 12px; align-items: flex-end;">

                        <div style="flex: 2; min-width: 220px;">
                            <label class="form-label" style="font-size: 11px;">Search by Code or Name</label>
                            <input type="text" name="search" value="${search}" class="form-input" placeholder="e.g. 1010 or Cash...">
                        </div>

                        <div style="flex: 1; min-width: 180px;">
                            <label class="form-label" style="font-size: 11px;">Classification</label>
                            <select name="type" class="form-select">
                                <option value="ALL">All Classifications</option>
                                <option value="ASSET" ${typeFilter == 'ASSET' ? 'selected' : ''}>Assets (1000s)</option>
                                <option value="LIABILITY" ${typeFilter == 'LIABILITY' ? 'selected' : ''}>Liabilities (2000s)</option>
                                <option value="EQUITY" ${typeFilter == 'EQUITY' ? 'selected' : ''}>Equity (3000s)</option>
                                <option value="REVENUE" ${typeFilter == 'REVENUE' ? 'selected' : ''}>Revenue (4000s)</option>
                                <option value="EXPENSE" ${typeFilter == 'EXPENSE' ? 'selected' : ''}>Expenses (5000s)</option>
                            </select>
                        </div>

                        <div style="display: flex; gap: 8px;">
                            <button type="submit" class="btn btn-secondary">Filter</button>
                            <a href="${pageContext.request.contextPath}/accounts" class="btn btn-secondary">Reset</a>
                        </div>
                    </form>
                </div>
            </div>

            <!-- Accounts Table -->
            <div class="geist-table-container">
                <table class="geist-table">
                    <thead>
                        <tr>
                            <th style="width: 120px;">Account Code</th>
                            <th>Account Name</th>
                            <th style="width: 140px;">Category</th>
                            <th style="width: 160px;">Normal Balance</th>
                            <th style="width: 100px;">Status</th>
                            <th>Description</th>
                            <th style="width: 100px; text-align: right;">Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:choose>
                            <c:when test="${empty accounts}">
                                <tr>
                                    <td colspan="7" style="text-align: center; padding: 48px; color: var(--text-tertiary);">
                                        No accounts found matching search criteria.
                                    </td>
                                </tr>
                            </c:when>
                            <c:otherwise>
                                <c:forEach var="acc" items="${accounts}">
                                    <tr>
                                        <td style="font-family: var(--font-mono); font-weight: 600; font-size: 13px;">
                                            ${acc.accountCode}
                                        </td>
                                        <td style="font-weight: 500;">
                                            <a href="${pageContext.request.contextPath}/ledger?accountId=${acc.id}" 
                                               style="color: inherit; text-decoration: underline; text-decoration-color: var(--border-color);" 
                                               title="View in General Ledger">
                                                ${acc.accountName}
                                            </a>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${acc.accountType == 'ASSET'}"><span class="pill pill-asset">Asset</span></c:when>
                                                <c:when test="${acc.accountType == 'LIABILITY'}"><span class="pill pill-liability">Liability</span></c:when>
                                                <c:when test="${acc.accountType == 'EQUITY'}"><span class="pill pill-equity">Equity</span></c:when>
                                                <c:when test="${acc.accountType == 'REVENUE'}"><span class="pill pill-revenue">Revenue</span></c:when>
                                                <c:when test="${acc.accountType == 'EXPENSE'}"><span class="pill pill-expense">Expense</span></c:when>
                                            </c:choose>
                                        </td>
                                        <td style="font-size: 12px; color: var(--text-secondary);">
                                            <c:choose>
                                                <c:when test="${acc.accountType.normalDebit}">Debit (+ / -)</c:when>
                                                <c:otherwise>Credit (- / +)</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${acc.active}">
                                                    <span class="pill pill-revenue" style="font-size: 10px;">Active</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="pill pill-neutral" style="font-size: 10px;">Inactive</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td style="color: var(--text-secondary); font-size: 12px;">
                                            ${acc.description}
                                        </td>
                                        <td style="text-align: right;">
                                            <a href="${pageContext.request.contextPath}/ledger?accountId=${acc.id}" 
                                               class="btn btn-secondary btn-sm" title="View Ledger">
                                                Ledger
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:otherwise>
                        </c:choose>
                    </tbody>
                </table>
            </div>
        </main>
    </div>
</div>

<!-- Create Account Modal Dialog -->
<div id="accountModal" class="modal-overlay">
    <div class="modal-card">
        <div class="geist-card-header">
            <div class="geist-card-title">Create New Account</div>
            <button type="button" onclick="closeModal()" class="btn btn-secondary btn-sm">&times;</button>
        </div>
        <form action="${pageContext.request.contextPath}/accounts" method="post">
            <input type="hidden" name="action" value="create">
            <div class="geist-card-body">
                <div class="form-group">
                    <label for="modalAccountCode" class="form-label">Account Code *</label>
                    <input type="text" id="modalAccountCode" name="accountCode" class="form-input" 
                           placeholder="e.g. 1060 or 5050" required maxlength="20">
                    <div class="form-hint">Standard convention: 1000s Assets, 2000s Liabilities, 3000s Equity, 4000s Revenue, 5000s Expenses.</div>
                </div>

                <div class="form-group">
                    <label for="modalAccountName" class="form-label">Account Name *</label>
                    <input type="text" id="modalAccountName" name="accountName" class="form-input" 
                           placeholder="e.g. Inventory or Travel Expense" required maxlength="100">
                </div>

                <div class="form-group">
                    <label for="modalAccountType" class="form-label">Account Classification *</label>
                    <select id="modalAccountType" name="accountType" class="form-select" required>
                        <option value="">-- Select Type --</option>
                        <option value="ASSET">Asset (Normal Debit)</option>
                        <option value="LIABILITY">Liability (Normal Credit)</option>
                        <option value="EQUITY">Equity (Normal Credit)</option>
                        <option value="REVENUE">Revenue (Normal Credit)</option>
                        <option value="EXPENSE">Expense (Normal Debit)</option>
                    </select>
                </div>

                <div class="form-group" style="margin-bottom: 0;">
                    <label for="modalDescription" class="form-label">Description / Accounting Notes</label>
                    <input type="text" id="modalDescription" name="description" class="form-input" 
                           placeholder="Brief description of transactions recorded here">
                </div>
            </div>
            <div class="geist-card-footer">
                <button type="button" onclick="closeModal()" class="btn btn-secondary">Cancel</button>
                <button type="submit" class="btn btn-primary">Save Account</button>
            </div>
        </form>
    </div>
</div>

<script>
    function openModal() {
        document.getElementById('accountModal').style.display = 'flex';
    }
    function closeModal() {
        document.getElementById('accountModal').style.display = 'none';
    }
    window.onclick = function(event) {
        const modal = document.getElementById('accountModal');
        if (event.target === modal) {
            closeModal();
        }
    };
</script>
</body>
</html>
