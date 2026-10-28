package com.accounting.controller;

import com.accounting.dao.UserDAO;
import com.accounting.model.User;
import com.accounting.util.PasswordUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Handles user profile and account security settings.
 */
@WebServlet(name = "SettingsServlet", urlPatterns = {"/settings"})
public class SettingsServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User sessionUser = (User) session.getAttribute("user");
        User freshUser = userDAO.findById(sessionUser.getId());
        if (freshUser != null) {
            session.setAttribute("user", freshUser);
            req.setAttribute("userProfile", freshUser);
        } else {
            req.setAttribute("userProfile", sessionUser);
        }

        req.getRequestDispatcher("/pages/settings.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect(req.getContextPath() + "/login");
            return;
        }

        User currentUser = (User) session.getAttribute("user");
        String action = req.getParameter("action");

        if ("updateProfile".equals(action)) {
            String fullName = req.getParameter("fullName");
            if (fullName == null || fullName.trim().isEmpty()) {
                req.setAttribute("error", "Full name cannot be blank.");
            } else {
                boolean updated = userDAO.updateProfile(currentUser.getId(), fullName.trim());
                if (updated) {
                    currentUser.setFullName(fullName.trim());
                    session.setAttribute("user", currentUser);
                    session.setAttribute("fullName", currentUser.getFullName());
                    req.setAttribute("success", "Profile updated successfully.");
                } else {
                    req.setAttribute("error", "Failed to update profile. Please try again.");
                }
            }
        } else if ("changePassword".equals(action)) {
            String currentPassword = req.getParameter("currentPassword");
            String newPassword = req.getParameter("newPassword");
            String confirmPassword = req.getParameter("confirmPassword");

            if (currentPassword == null || currentPassword.isEmpty() ||
                newPassword == null || newPassword.isEmpty() ||
                confirmPassword == null || confirmPassword.isEmpty()) {
                req.setAttribute("error", "All password fields are required.");
            } else if (!newPassword.equals(confirmPassword)) {
                req.setAttribute("error", "New password and confirmation password do not match.");
            } else if (newPassword.length() < 6) {
                req.setAttribute("error", "New password must be at least 6 characters long.");
            } else {
                // Verify current password against fresh DB record
                User freshUser = userDAO.findById(currentUser.getId());
                if (freshUser == null || !PasswordUtil.checkPassword(currentPassword, freshUser.getPasswordHash())) {
                    req.setAttribute("error", "Current password is incorrect.");
                } else {
                    String newHash = PasswordUtil.hashPassword(newPassword);
                    boolean updated = userDAO.updatePassword(currentUser.getId(), newHash);
                    if (updated) {
                        currentUser.setPasswordHash(newHash);
                        session.setAttribute("user", currentUser);
                        req.setAttribute("success", "Password changed successfully.");
                    } else {
                        req.setAttribute("error", "Failed to change password. Database error.");
                    }
                }
            }
        }

        doGet(req, resp);
    }
}
