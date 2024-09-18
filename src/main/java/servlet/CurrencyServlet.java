package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.NotFoundException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyService;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String pathInfo = req.getPathInfo();
        try {
            if (pathInfo != null && pathInfo.length() > 1) {
                String currencyCode = pathInfo.substring(1);
                resp.setContentType("application/json");
                resp.getWriter().write(new ObjectMapper().writeValueAsString(currencyService.findByCode(currencyCode)));

                resp.setStatus(200);

            }
            else {
                resp.sendError(400, "Currency code is missing.");
            }
        }
        catch (NotFoundException e){
            resp.sendError(404, "Currency not found");
        }
        catch (Exception e){
            resp.sendError(500, "Error");
        }


    }
}
