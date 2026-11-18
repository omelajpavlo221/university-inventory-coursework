package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

public class IssueRequestServlet extends HttpServlet {
    private final IssueRequestService service = new IssueRequestService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action)) {
                service.add(
                    Long.parseLong(req.getParameter("assetId")),
                    Long.parseLong(req.getParameter("userId")),
                    Integer.parseInt(req.getParameter("requestedQty")),
                    Double.parseDouble(req.getParameter("urgencyScore"))
                );
            } else if ("update".equals(action)) {
                service.update(
                    Long.parseLong(req.getParameter("id")),
                    Long.parseLong(req.getParameter("assetId")),
                    Long.parseLong(req.getParameter("userId")),
                    Integer.parseInt(req.getParameter("requestedQty")),
                    Double.parseDouble(req.getParameter("urgencyScore"))
                );
            } else if ("transition".equals(action)) {
                String stateParam = req.getParameter("state");
                if (stateParam == null || stateParam.isBlank()) {
                    throw new IllegalArgumentException("Не задано новий стан заявки");
                }
                RequestState next;
                try {
                    next = RequestState.valueOf(stateParam);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Невідомий стан заявки: " + stateParam);
                }
                service.changeState(Long.parseLong(req.getParameter("id")), next);
            } else if ("delete".equals(action)) {
                service.delete(Long.parseLong(req.getParameter("id")));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private void writeJson(HttpServletResponse resp, Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        int status;
        String msg;
        if (e instanceof NumberFormatException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            msg = "Некоректне або відсутнє числове значення параметра запиту";
        } else if (e instanceof IllegalArgumentException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            msg = e.getMessage() != null ? e.getMessage() : "Некоректний запит";
        } else if (e instanceof SQLIntegrityConstraintViolationException) {
            status = HttpServletResponse.SC_BAD_REQUEST;
            msg = "Операцію відхилено обмеженням цілісності БД: значення вже існує або запис має пов’язані дані";
        } else {
            status = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            msg = "Внутрішня помилка сервера";
        }
        resp.setStatus(status);
        writeJson(resp, Map.of("error", msg));
    }
}
