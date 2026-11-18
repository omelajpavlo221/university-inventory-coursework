package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AssetDAO {
    public void add(Asset a) throws SQLException {
        String sql = "INSERT INTO asset(inv_number, name, category, available_qty, criticality_score) VALUES(?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, a);
            p.executeUpdate();
        }
    }

    public List<Asset> getAll() throws SQLException {
        List<Asset> list = new ArrayList<>();
        String sql = "SELECT * FROM asset ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public Asset getById(long id) throws SQLException {
        String sql = "SELECT * FROM asset WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Asset a) throws SQLException {
        String sql = "UPDATE asset SET inv_number=?, name=?, category=?, available_qty=?, criticality_score=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, a);
            p.setLong(6, a.getId());
            p.executeUpdate();
        }
    }

    public void updateAvailableQty(long id, int newQty) throws SQLException {
        String sql = "UPDATE asset SET available_qty=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setInt(1, newQty);
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM asset WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, Asset a) throws SQLException {
        p.setString(1, a.getInvNumber());
        p.setString(2, a.getName());
        p.setString(3, a.getCategory());
        p.setInt(4, a.getAvailableQty());
        p.setDouble(5, a.getCriticalityScore());
    }

    private Asset map(ResultSet r) throws SQLException {
        return new Asset(
            r.getLong("id"),
            r.getString("inv_number"),
            r.getString("name"),
            r.getString("category"),
            r.getInt("available_qty"),
            r.getDouble("criticality_score")
        );
    }
}
