package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.CurrencyDto;
import exception.UniqueException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import entity.Currency;


@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        List<CurrencyDto> result = currencyService.findAll();
        try {
            resp.getWriter().write(new ObjectMapper().writeValueAsString(result));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            resp.sendError(500, "Error");

        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");

        String name = req.getParameter("name");
        String code= req.getParameter("code");
        String sign = req.getParameter("sign");

        if (name == null || code == null || sign == null){
            resp.sendError(400, "A required form field is missing");
            return;
        }
        try {
            Currency res = currencyService.save(Currency
                    .builder()
                    .code(code)
                    .sign(sign)
                    .fullName(name)
                    .build());
            resp.getWriter().write(new ObjectMapper().writeValueAsString(res));
            resp.setStatus(HttpServletResponse.SC_OK);
        }

        catch (UniqueException e){
            resp.sendError(409, "A currency with this code already exists.");
        }
        catch (Exception e) {
            resp.sendError(500, "Error");
        }
    }
}
