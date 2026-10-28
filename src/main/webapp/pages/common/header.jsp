<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<header class="top-bar no-print">
    <div class="page-title">
        <span class="page-breadcrumb">Accurix /</span>
        <span>${pageTitle != null ? pageTitle : 'Dashboard'}</span>
    </div>

    <div style="display: flex; align-items: center; gap: 12px;">
        <c:if test="${isReportPage == true}">
            <button onclick="window.print()" class="btn btn-secondary btn-sm">
                <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <polyline points="6 9 6 2 18 2 18 9"></polyline>
                    <path d="M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"></path>
                    <rect x="6" y="14" width="12" height="8"></rect>
                </svg>
                <span>Print Report</span>
            </button>
        </c:if>

        <span class="pill pill-neutral">
            <span style="display: inline-block; width: 6px; height: 6px; border-radius: 50%; background: #10b981; margin-right: 4px;"></span>
            ${sessionScope.username} (${sessionScope.userRole})
        </span>
    </div>
</header>
