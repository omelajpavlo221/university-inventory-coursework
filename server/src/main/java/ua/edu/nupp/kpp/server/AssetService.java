package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class AssetService {
    private static final Logger LOG = Logger.getLogger(AssetService.class.getName());
    private final AssetDAO dao = new AssetDAO();

    public List<Asset> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(Asset a) throws SQLException {
        validate(a);
        dao.add(a);
        LOG.info("Додано одиницю майна: " + a.getInvNumber());
    }

    public void update(Asset a) throws SQLException {
        validate(a);
        if (a.getId() == null || dao.getById(a.getId()) == null) {
            throw new IllegalArgumentException("Майно не знайдено");
        }
        dao.update(a);
        LOG.info("Оновлено майно ID=" + a.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено майно ID=" + id);
    }

    static void validate(Asset a) {
        if (a == null || blank(a.getInvNumber()) || blank(a.getName()) || blank(a.getCategory())) {
            throw new IllegalArgumentException("Інвентарний номер, назва та категорія майна є обов'язковими");
        }
        if (a.getAvailableQty() < 0) {
            throw new IllegalArgumentException("Доступна кількість не може бути від'ємною");
        }
        if (a.getCriticalityScore() < 0 || a.getCriticalityScore() > 100) {
            throw new IllegalArgumentException("Критичність майна (F2) має бути в діапазоні 0..100");
        }
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
