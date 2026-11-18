package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class AppUserService {
    private static final Logger LOG = Logger.getLogger(AppUserService.class.getName());
    private final AppUserDAO dao = new AppUserDAO();

    public List<AppUser> getAll() throws SQLException {
        return dao.getAll();
    }

    public void add(AppUser u) throws SQLException {
        validate(u);
        dao.add(u);
        LOG.info("Додано користувача: " + u.getFullName());
    }

    public void update(AppUser u) throws SQLException {
        validate(u);
        if (u.getId() == null || dao.getById(u.getId()) == null) {
            throw new IllegalArgumentException("Користувача не знайдено");
        }
        dao.update(u);
        LOG.info("Оновлено користувача ID=" + u.getId());
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено користувача ID=" + id);
    }

    static void validate(AppUser u) {
        if (u == null || blank(u.getFullName()) || blank(u.getDepartment()) || blank(u.getRole()) || blank(u.getEmail())) {
            throw new IllegalArgumentException("Усі поля користувача є обов'язковими");
        }
        if (!u.getEmail().contains("@")) {
            throw new IllegalArgumentException("Некоректний формат електронної пошти");
        }
    }

    private static boolean blank(String s) {
        return s == null || s.isBlank();
    }
}
