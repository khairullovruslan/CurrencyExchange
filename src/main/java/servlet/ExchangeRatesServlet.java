package servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRatesDto;
import exception.NotFoundException;
import exception.UniqueException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRatesService;

import java.io.IOException;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            resp.setContentType("application/json");
            resp.getWriter().write(new ObjectMapper().writeValueAsString(exchangeRatesService.findAll()));
            resp.setStatus(200);
        } catch (Exception e) {
            resp.sendError(500, "Error");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        if (baseCurrencyCode == null || targetCurrencyCode == null || rate == null) {
            resp.sendError(400, "A required form field is missing");
            return;
        }
        try {
            ExchangeRatesDto exchangeRatesDto = exchangeRatesService.saveNewExchange(baseCurrencyCode, targetCurrencyCode, Double.valueOf(rate));

            resp.getWriter().write(new ObjectMapper().writeValueAsString(exchangeRatesDto));
            resp.setStatus(201);

        } catch (UniqueException e) {
            resp.sendError(409, "A currency pair with this code already exists");
        } catch (NotFoundException e) {
            resp.sendError(404, "One (or both) currencies from the currency pair do not exist in the database");
        } catch (NumberFormatException e) {
            resp.sendError(400, "A required form field is missing");
        } catch (Exception e) {
            resp.sendError(500, "Error");
        }

    }
}
