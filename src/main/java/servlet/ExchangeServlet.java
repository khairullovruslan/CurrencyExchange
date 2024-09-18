package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeDto;
import exception.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRatesService;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurCode = req.getParameter("from");
        String targetCurCode = req.getParameter("to");
        String amount = req.getParameter("amount");
        if (baseCurCode == null || targetCurCode == null || amount == null) {
            resp.sendError(400, "Currency codes of the pair are missing in the address");
            return;
        }
        try {
            ExchangeDto exchangeDto = exchangeRatesService.exchange(baseCurCode, targetCurCode, Double.valueOf(amount));
            resp.getWriter().write(new ObjectMapper().writeValueAsString(exchangeDto));
            resp.setStatus(200);
        } catch (NotFoundException e) {
            resp.sendError(404, "The currency pair is missing from the database");
        } catch (Exception e) {
            resp.sendError(500, "Error");
        }


    }
}

