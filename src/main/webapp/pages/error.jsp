<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    String code = request.getParameter("code");
    if (code == null || code.isEmpty()) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        code = (statusCode != null) ? String.valueOf(statusCode) : "500";
    }
    String message = "404".equals(code) 
            ? "The page or resource you requested could not be found." 
            : "An unexpected error occurred while processing your request.";
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Error <%= code %> — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
    <script>
        document.documentElement.setAttribute('data-theme', 'light');
        try { localStorage.setItem('theme', 'light'); } catch(e) {}
    </script>
</head>
<body>
<div class="auth-wrapper">
    <div class="auth-card" style="text-align: center;">
        <div style="font-family: var(--font-mono); font-size: 48px; font-weight: 700; letter-spacing: -0.05em; color: var(--text-primary);">
            <%= code %>
        </div>
        <h1 style="font-size: 16px; font-weight: 600; margin-top: 8px;">
            <%= "404".equals(code) ? "Page Not Found" : "System Error" %>
        </h1>
        <p style="font-size: 13px; color: var(--text-secondary); margin-top: 8px; line-height: 1.5;">
            <%= message %>
        </p>

        <div style="margin-top: 24px;">
            <a href="${pageContext.request.contextPath}/dashboard" class="btn btn-primary" style="width: 100%;">
                Return to Dashboard
            </a>
        </div>
    </div>
</div>
</body>
</html>
