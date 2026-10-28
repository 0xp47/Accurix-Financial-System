package com.accounting.controller;

import com.accounting.dao.AccountDAO;
import com.accounting.model.Account;
import com.accounting.model.AccountType;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for Chart of Accounts listing, filtering, and account creation.
 */
@WebServlet(name = "AccountServlet", urlPatterns = {"/accounts"})
public class AccountServlet extends HttpServlet {

    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String search = req.getParameter("search");
        String typeFilter = req.getParameter("type");

        List<Account> accounts = accountDAO.search(search, typeFilter);

        req.setAttribute("accounts", accounts);
        req.setAttribute("search", search);
        req.setAttribute("typeFilter", typeFilter);
        req.setAttribute("accountTypes", AccountType.values());

        req.getRequestDispatcher("/pages/accounts/index.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("create".equals(action)) {
            String accountCode = req.getParameter("accountCode");
            String accountName = req.getParameter("accountName");
            String accountTypeStr = req.getParameter("accountType");
            String description = req.getParameter("description");

            if (accountCode == null || accountCode.trim().isEmpty() ||
                accountName == null || accountName.trim().isEmpty() ||
                accountTypeStr == null || accountTypeStr.trim().isEmpty()) {
                req.setAttribute("error", "Account Code, Account Name, and Type are required.");
                doGet(req, resp);
                return;
            }

            // Check for duplicate code
            if (accountDAO.findByCode(accountCode.trim()) != null) {
                req.setAttribute("error", "Account code '" + accountCode.trim() + "' already exists.");
                doGet(req, resp);
                return;
            }

            Account account = new Account();
            account.setAccountCode(accountCode.trim());
            account.setAccountName(accountName.trim());
            account.setAccountType(AccountType.fromString(accountTypeStr.trim()));
            account.setDescription(description != null ? description.trim() : "");
            account.setActive(true);

            int id = accountDAO.createAccount(account);
            if (id > 0) {
                resp.sendRedirect(req.getContextPath() + "/accounts?success=created");
            } else {
                req.setAttribute("error", "Failed to create account. Database error.");
                doGet(req, resp);
            }
        } else if ("update".equals(action)) {
            String idParam = req.getParameter("id");
            String accountName = req.getParameter("accountName");
            String accountTypeStr = req.getParameter("accountType");
            String description = req.getParameter("description");
            boolean isActive = "true".equalsIgnoreCase(req.getParameter("isActive"));

            if (idParam != null && !idParam.isEmpty()) {
                try {
                    int id = Integer.parseInt(idParam);
                    Account account = accountDAO.findById(id);
                    if (account != null) {
                        account.setAccountName(accountName.trim());
                        account.setAccountType(AccountType.fromString(accountTypeStr));
                        account.setDescription(description != null ? description.trim() : "");
                        account.setActive(isActive);
                        accountDAO.updateAccount(account);
                        resp.sendRedirect(req.getContextPath() + "/accounts?success=updated");
                        return;
                    }
                } catch (Exception ignored) {
                }
            }
            resp.sendRedirect(req.getContextPath() + "/accounts");
        } else {
            resp.sendRedirect(req.getContextPath() + "/accounts");
        }
    }
}
