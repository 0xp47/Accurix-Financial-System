<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="activePage" value="settings" scope="request"/>
<c:set var="pageTitle" value="Account Settings" scope="request"/>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Account Settings — Accurix Financial</title>
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/favicon.png">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/geist.css">
</head>
<body>
<div class="app-layout">
    <jsp:include page="/pages/common/sidebar.jsp"/>

    <div class="main-wrapper">
        <jsp:include page="/pages/common/header.jsp"/>

        <main class="content-body">
            <div style="margin-bottom: 24px;">
                <h1 style="font-size: 20px; font-weight: 600; letter-spacing: -0.02em;">Account Settings</h1>
                <p style="font-size: 13px; color: var(--text-secondary); margin-top: 4px;">
                    Manage your account profile details and authentication credentials.
                </p>
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

            <c:if test="${not empty success}">
                <div class="alert alert-success">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                        <path d="M20 6L9 17l-5-5"></path>
                    </svg>
                    <span>${success}</span>
                </div>
            </c:if>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 24px;">
                <!-- Profile Information Card -->
                <div class="geist-card">
                    <div class="geist-card-header">
                        <div>
                            <div class="geist-card-title">User Profile</div>
                            <div class="geist-card-subtitle">Your personal system display details</div>
                        </div>
                    </div>
                    <form action="${pageContext.request.contextPath}/settings" method="post">
                        <input type="hidden" name="action" value="updateProfile">
                        <div class="geist-card-body">
                            <div class="form-group">
                                <label class="form-label">Username</label>
                                <input type="text" class="form-input" value="${userProfile.username}" readonly>
                                <div class="form-hint">Username cannot be changed.</div>
                            </div>

                            <div class="form-group">
                                <label class="form-label">System Role</label>
                                <div>
                                    <span class="pill pill-neutral">${userProfile.role}</span>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="fullName" class="form-label">Full Name</label>
                                <input type="text" id="fullName" name="fullName" class="form-input" 
                                       value="${userProfile.fullName}" required>
                            </div>
                        </div>
                        <div class="geist-card-footer">
                            <button type="submit" class="btn btn-primary">Save Profile</button>
                        </div>
                    </form>
                </div>

                <!-- Password Change Card -->
                <div class="geist-card">
                    <div class="geist-card-header">
                        <div>
                            <div class="geist-card-title">Change Password</div>
                            <div class="geist-card-subtitle">Update your password securely using BCrypt hashing</div>
                        </div>
                    </div>
                    <form action="${pageContext.request.contextPath}/settings" method="post">
                        <input type="hidden" name="action" value="changePassword">
                        <div class="geist-card-body">
                            <div class="form-group">
                                <label for="currentPassword" class="form-label">Current Password</label>
                                <input type="password" id="currentPassword" name="currentPassword" 
                                       class="form-input" required autocomplete="current-password">
                            </div>

                            <div class="form-group">
                                <label for="newPassword" class="form-label">New Password</label>
                                <input type="password" id="newPassword" name="newPassword" 
                                       class="form-input" required minlength="6" autocomplete="new-password">
                                <div class="form-hint">Minimum 6 characters.</div>
                            </div>

                            <div class="form-group">
                                <label for="confirmPassword" class="form-label">Confirm New Password</label>
                                <input type="password" id="confirmPassword" name="confirmPassword" 
                                       class="form-input" required minlength="6" autocomplete="new-password">
                            </div>
                        </div>
                        <div class="geist-card-footer">
                            <button type="submit" class="btn btn-secondary">Update Password</button>
                        </div>
                    </form>
                </div>
            </div>
        </main>
    </div>
</div>
</body>
</html>
