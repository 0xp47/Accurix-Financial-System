package com.accounting.controller;

import com.accounting.dao.ReportDAO;
import com.accounting.model.BalanceSheet;
import com.accounting.model.IncomeStatement;
import com.accounting.model.TrialBalance;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Controller handling Trial Balance, Income Statement, and Balance Sheet generation.
 */
@WebServlet(name = "ReportServlet", urlPatterns = {"/reports"})
public class ReportServlet extends HttpServlet {

    private final ReportDAO reportDAO = new ReportDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String type = req.getParameter("type");
        if (type == null || type.trim().isEmpty()) {
            type = "trial_balance";
        }

        String startDate = req.getParameter("startDate");
        String endDate = req.getParameter("endDate");
        String asOfDate = req.getParameter("asOfDate");

        req.setAttribute("reportType", type);
        req.setAttribute("startDate", startDate);
        req.setAttribute("endDate", endDate);
        req.setAttribute("asOfDate", asOfDate);

        switch (type) {
            case "income_statement":
                IncomeStatement is = reportDAO.generateIncomeStatement(startDate, endDate);
                req.setAttribute("statement", is);
                req.getRequestDispatcher("/pages/reports/income_statement.jsp").forward(req, resp);
                break;

            case "balance_sheet":
                BalanceSheet bs = reportDAO.generateBalanceSheet(asOfDate);
                req.setAttribute("sheet", bs);
                req.getRequestDispatcher("/pages/reports/balance_sheet.jsp").forward(req, resp);
                break;

            case "trial_balance":
            default:
                TrialBalance tb = reportDAO.generateTrialBalance(asOfDate);
                req.setAttribute("trialBalance", tb);
                req.getRequestDispatcher("/pages/reports/trial_balance.jsp").forward(req, resp);
                break;
        }
    }
}
