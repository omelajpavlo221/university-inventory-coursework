package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppUserDAO {
    public void add(AppUser u) throws SQLException {
        String sql = "INSERT INTO app_user(full_name, department, role, email) VALUES(?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, u);
            p.executeUpdate();
        }
    }

    public List<AppUser> getAll() throws SQLException {
        List<AppUser> list = new ArrayList<>();
        String sql = "SELECT * FROM app_user ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public AppUser getById(long id) throws SQLException {
        String sql = "SELECT * FROM app_user WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(AppUser u) throws SQLException {
        String sql = "UPDATE app_user SET full_name=?, department=?, role=?, email=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, u);
            p.setLong(5, u.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM app_user WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, AppUser u) throws SQLException {
        p.setString(1, u.getFullName());
        p.setString(2, u.getDepartment());
        p.setString(3, u.getRole());
        p.setString(4, u.getEmail());
    }

    private AppUser map(ResultSet r) throws SQLException {
        return new AppUser(
            r.getLong("id"),
            r.getString("full_name"),
            r.getString("department"),
            r.getString("role"),
            r.getString("email")
        );
    }
}
