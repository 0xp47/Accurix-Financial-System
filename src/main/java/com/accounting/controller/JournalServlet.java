package com.accounting.controller;

import com.accounting.dao.AccountDAO;
import com.accounting.dao.JournalEntryDAO;
import com.accounting.model.Account;
import com.accounting.model.JournalEntry;
import com.accounting.model.JournalEntryLine;
import com.accounting.model.User;
import com.accounting.service.JournalValidationService;
import com.accounting.service.JournalValidationService.ValidationResult;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller handling Journal Entry CRUD operations and date filtering.
 */
@WebServlet(name = "JournalServlet", urlPatterns = {"/journals"})
public class JournalServlet extends HttpServlet {

    private final JournalEntryDAO journalEntryDAO = new JournalEntryDAO();
    private final AccountDAO accountDAO = new AccountDAO();
    private final JournalValidationService validationService = new JournalValidationService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "new":
                showNewForm(req, resp);
                break;
            case "edit":
                showEditForm(req, resp);
                break;
            case "view":
                showView(req, resp);
                break;
            case "list":
            default:
                showList(req, resp);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("delete".equals(action)) {
            handleDelete(req, resp);
        } else if ("save".equals(action) || "update".equals(action)) {
            handleSaveOrUpdate(req, resp);
        } else {
            resp.sendRedirect(req.getContextPath() + "/journals");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        String search = req.getParameter("search");

        List<JournalEntry> entries = journalEntryDAO.findAll(startDate, endDate, search);

        BigDecimal grandTotalDebit = BigDecimal.ZERO;
        BigDecimal grandTotalCredit = BigDecimal.ZERO;

        for (JournalEntry entry : entries) {
            grandTotalDebit = grandTotalDebit.add(entry.getTotalDebit());
            grandTotalCredit = grandTotalCredit.add(entry.getTotalCredit());
        }

        req.setAttribute("entries", entries);
        req.setAttribute("grandTotalDebit", grandTotalDebit);
        req.setAttribute("grandTotalCredit", grandTotalCredit);
        req.setAttribute("isTotalsBalanced", grandTotalDebit.compareTo(grandTotalCredit) == 0);
        req.setAttribute("startDate", startDate);
        req.setAttribute("endDate", endDate);
        req.setAttribute("search", search);

        req.getRequestDispatcher("/pages/journals/list.jsp").forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Account> accounts = accountDAO.findAllActive();
        req.setAttribute("accounts", accounts);

        JournalEntry entry = new JournalEntry();
        entry.setEntryDate(new Date(System.currentTimeMillis()));

        // Initial 2 lines for user convenience
        entry.addLine(new JournalEntryLine());
        entry.addLine(new JournalEntryLine());

        req.setAttribute("entry", entry);
        req.setAttribute("isEdit", false);
        req.getRequestDispatcher("/pages/journals/form.jsp").forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/journals");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            JournalEntry entry = journalEntryDAO.findById(id);
            if (entry == null) {
                req.getSession().setAttribute("flashError", "Journal entry not found.");
                resp.sendRedirect(req.getContextPath() + "/journals");
                return;
            }

            List<Account> accounts = accountDAO.findAllActive();
            req.setAttribute("accounts", accounts);
            req.setAttribute("entry", entry);
            req.setAttribute("isEdit", true);
            req.getRequestDispatcher("/pages/journals/form.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/journals");
        }
    }

    private void showView(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idParam = req.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/journals");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            JournalEntry entry = journalEntryDAO.findById(id);
            if (entry == null) {
                req.getSession().setAttribute("flashError", "Journal entry not found.");
                resp.sendRedirect(req.getContextPath() + "/journals");
                return;
            }

            req.setAttribute("entry", entry);
            req.getRequestDispatcher("/pages/journals/view.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/journals");
        }
    }

    private void handleSaveOrUpdate(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        String idParam = req.getParameter("id");
        boolean isEdit = (idParam != null && !idParam.trim().isEmpty() && !idParam.equals("0"));

        String dateStr = req.getParameter("entryDate");
        String particulars = req.getParameter("particulars");
        String referenceNumber = req.getParameter("referenceNumber");

        String[] accountIds = req.getParameterValues("accountId[]");
        String[] debits = req.getParameterValues("debit[]");
        String[] credits = req.getParameterValues("credit[]");

        JournalEntry entry = new JournalEntry();
        if (isEdit) {
            entry.setId(Integer.parseInt(idParam));
        }
        if (dateStr != null && !dateStr.trim().isEmpty()) {
            try {
                entry.setEntryDate(Date.valueOf(dateStr.trim()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        entry.setParticulars(particulars != null ? particulars.trim() : "");
        entry.setReferenceNumber(referenceNumber != null ? referenceNumber.trim() : "");
        if (user != null) {
            entry.setCreatedBy(user.getId());
        }

        List<JournalEntryLine> lines = new ArrayList<>();
        if (accountIds != null) {
            for (int i = 0; i < accountIds.length; i++) {
                String accIdStr = accountIds[i];
                if (accIdStr == null || accIdStr.trim().isEmpty()) {
                    continue;
                }

                try {
                    int accId = Integer.parseInt(accIdStr);
                    BigDecimal debit = parseAmount(debits != null && i < debits.length ? debits[i] : "0");
                    BigDecimal credit = parseAmount(credits != null && i < credits.length ? credits[i] : "0");

                    // Only include lines with some entry
                    if (accId > 0 && (debit.compareTo(BigDecimal.ZERO) > 0 || credit.compareTo(BigDecimal.ZERO) > 0)) {
                        JournalEntryLine line = new JournalEntryLine(accId, debit, credit);
                        line.setLineOrder(i + 1);
                        lines.add(line);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        }
        entry.setLines(lines);

        // Enforce Validation Rules
        ValidationResult result = validationService.validate(entry);

        if (!result.isValid()) {
            req.setAttribute("error", result.getMessage());
            req.setAttribute("entry", entry);
            req.setAttribute("isEdit", isEdit);
            req.setAttribute("accounts", accountDAO.findAllActive());
            req.getRequestDispatcher("/pages/journals/form.jsp").forward(req, resp);
            return;
        }

        boolean success;
        if (isEdit) {
            success = journalEntryDAO.updateEntry(entry);
        } else {
            int newId = journalEntryDAO.createEntry(entry);
            success = (newId > 0);
        }

        if (success) {
            resp.sendRedirect(req.getContextPath() + "/journals?success=" + (isEdit ? "updated" : "created"));
        } else {
            req.setAttribute("error", "Database operation failed. Unable to save journal entry.");
            req.setAttribute("entry", entry);
            req.setAttribute("isEdit", isEdit);
            req.setAttribute("accounts", accountDAO.findAllActive());
            req.getRequestDispatcher("/pages/journals/form.jsp").forward(req, resp);
        }
    }

    private void handleDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String idParam = req.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                int id = Integer.parseInt(idParam);
                journalEntryDAO.deleteEntry(id);
            } catch (NumberFormatException ignored) {
            }
        }
        resp.sendRedirect(req.getContextPath() + "/journals?success=deleted");
    }

    private BigDecimal parseAmount(String val) {
        if (val == null || val.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(val.trim().replace(",", ""));
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }
}
