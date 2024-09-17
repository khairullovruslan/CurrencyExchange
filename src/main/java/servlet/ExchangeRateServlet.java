package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRatesDto;
import exception.NotFoundException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.ExchangeRatesService;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet  extends HttpServlet {
    private final ExchangeRatesService exchangeRatesService = ExchangeRatesService.getInstance();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (req.getMethod().equals("PATCH"))
            doPatch(req, resp);
        else
            super.service(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                String exCode = pathInfo.substring(1);
                ExchangeRatesDto currency = exchangeRatesService.findByIdCodes(exCode);
                if (currency == null){
                    resp.sendError(404, "Exchange rate for the pair not found");
                }
                else {
                    resp.getWriter().write(new ObjectMapper().writeValueAsString(currency));
                    resp.setStatus(200);
                }

            }
            else {
                resp.sendError(400, "Pair currency codes are missing in the address");
            }
        }
        catch (Exception e){
            resp.sendError(500, "error");
        }
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)   throws IOException {
        String pathInfo = req.getPathInfo();
        String rate = req.getParameter("rate");
        if (pathInfo == null || pathInfo.length() <= 1 || rate == null){
            resp.sendError(400, "The required form field is present");
            return;
        }

        String exCode = pathInfo.substring(1);
        try {
            ExchangeRatesDto exchangeRatesDto = exchangeRatesService.changeRate(exCode, Double.valueOf(rate));
            resp.getWriter().write(new ObjectMapper().writeValueAsString(exchangeRatesDto));
            resp.setStatus(200);
        }
        catch (NotFoundException e){
            resp.sendError(404, "The currency pair is missing from the database");
        }



    }



}
