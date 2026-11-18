package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

public class TransferRecordService {
    private static final Logger LOG = Logger.getLogger(TransferRecordService.class.getName());
    private final TransferRecordDAO dao = new TransferRecordDAO();
    private final IssueRequestDAO requestDAO = new IssueRequestDAO();

    public List<TransferRecord> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(TransferRecord tr) throws SQLException {
        validate(tr);
        requireRequest(tr.getRequestId());
        dao.add(tr);
        LOG.info("Додано запис передачі для заявки ID=" + tr.getRequestId());
    }

    public void update(TransferRecord tr) throws SQLException {
        validate(tr);
        requireRequest(tr.getRequestId());
        if (tr.getId() == null || dao.getById(tr.getId()) == null) {
            throw new IllegalArgumentException("Запис передачі не знайдено");
        }
        dao.update(tr);
        LOG.info("Оновлено запис передачі ID=" + tr.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено запис передачі ID=" + id);
    }

    static void validate(TransferRecord tr) {
        if (tr == null || tr.getRequestId() <= 0 || blank(tr.getActionType()) || blank(tr.getActionDate()) || blank(tr.getConditionNote())) {
            throw new IllegalArgumentException("Усі поля запису передачі є обов'язковими");
        }
        try {
            LocalDate.parse(tr.getActionDate());
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Дата має відповідати формату YYYY-MM-DD і бути існуючою датою");
        }
    }

    private void requireRequest(long requestId) throws SQLException {
        if (requestDAO.getById(requestId) == null) {
            throw new IllegalArgumentException("Заявку із зазначеним ID не знайдено");
        }
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
