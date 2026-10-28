<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Sign In — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
    <script>
        document.documentElement.setAttribute('data-theme', 'light');
        try { localStorage.setItem('theme', 'light'); } catch(e) {}
    </script>
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-card">
        <div class="auth-header">
            <!-- Refined Executive Scales Emblem -->
            <div class="brand-badge-lg">
                <svg width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <path d="M12 4v16"></path>
                    <path d="M7 20h10"></path>
                    <path d="M4 7h16"></path>
                    <circle cx="12" cy="4" r="1.5" fill="currentColor"></circle>
                    <path d="M6 7l-2 5"></path>
                    <path d="M6 7l2 5"></path>
                    <path d="M3.5 12c0 2 5 2 5 0z" fill="currentColor"></path>
                    <path d="M18 7l-2 5"></path>
                    <path d="M18 7l2 5"></path>
                    <path d="M15.5 12c0 2 5 2 5 0z" fill="currentColor"></path>
                </svg>
            </div>
            <h1 class="auth-title">Sign in to Accurix Financial</h1>
            <p class="auth-subtitle">Enterprise Double-Entry Bookkeeping & Ledger Suite</p>
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

        <c:if test="${param.logged_out == 'true'}">
            <div class="alert alert-success">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                    <path d="M20 6L9 17l-5-5"></path>
                </svg>
                <span>You have been signed out safely.</span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" autocomplete="off">
            <div class="form-group">
                <label for="username" class="form-label">Username</label>
                <input type="text" id="username" name="username" class="form-input" 
                       placeholder="e.g. admin or accountant" 
                       value="${enteredUsername}" required autofocus>
            </div>

            <div class="form-group">
                <label for="password" class="form-label">Password</label>
                <input type="password" id="password" name="password" class="form-input" 
                       placeholder="••••••••••••" required>
            </div>

            <div style="margin-top: 24px;">
                <button type="submit" class="btn btn-primary" style="width: 100%; padding: 10px;">
                    Sign In
                </button>
            </div>
        </form>
    </div>
</div>
</body>
</html>
