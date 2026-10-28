package com.accounting.controller;

import com.accounting.dao.AccountDAO;
import com.accounting.dao.LedgerDAO;
import com.accounting.model.Account;
import com.accounting.model.AccountLedger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Controller for General Ledger account views, balance calculations, and date filters.
 */
@WebServlet(name = "LedgerServlet", urlPatterns = {"/ledger"})
public class LedgerServlet extends HttpServlet {

    private final LedgerDAO ledgerDAO = new LedgerDAO();
    private final AccountDAO accountDAO = new AccountDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accountIdParam = req.getParameter("accountId");
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        String search = req.getParameter("search");

        List<Account> accounts;
        if (search != null && !search.trim().isEmpty()) {
            accounts = accountDAO.search(search.trim(), null);
        } else {
            accounts = accountDAO.findAllActive();
        }

        req.setAttribute("accounts", accounts);
        req.setAttribute("startDate", startDate);
        req.setAttribute("endDate", endDate);
        req.setAttribute("search", search);

        int selectedAccountId = -1;
        if (accountIdParam != null && !accountIdParam.trim().isEmpty()) {
            try {
                selectedAccountId = Integer.parseInt(accountIdParam.trim());
            } catch (NumberFormatException ignored) {
            }
        } else if (!accounts.isEmpty()) {
            // Default to first account (e.g. Cash)
            selectedAccountId = accounts.get(0).getId();
        }

        if (selectedAccountId > 0) {
            AccountLedger ledger = ledgerDAO.getAccountLedger(selectedAccountId, startDate, endDate);
            req.setAttribute("ledger", ledger);
            req.setAttribute("selectedAccountId", selectedAccountId);
        }

        req.getRequestDispatcher("/pages/ledger/index.jsp").forward(req, resp);
    }
}
