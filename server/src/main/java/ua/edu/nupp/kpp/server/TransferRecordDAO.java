package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransferRecordDAO {
    public void add(TransferRecord tr) throws SQLException {
        String sql = "INSERT INTO transfer_record(request_id, action_type, action_date, condition_note) VALUES(?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, tr);
            p.executeUpdate();
        }
    }

    public List<TransferRecord> getAll() throws SQLException {
        List<TransferRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM transfer_record ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public TransferRecord getById(long id) throws SQLException {
        String sql = "SELECT * FROM transfer_record WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(TransferRecord tr) throws SQLException {
        String sql = "UPDATE transfer_record SET request_id=?, action_type=?, action_date=?, condition_note=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, tr);
            p.setLong(5, tr.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM transfer_record WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, TransferRecord tr) throws SQLException {
        p.setLong(1, tr.getRequestId());
        p.setString(2, tr.getActionType());
        p.setString(3, tr.getActionDate());
        p.setString(4, tr.getConditionNote());
    }

    private TransferRecord map(ResultSet r) throws SQLException {
        return new TransferRecord(
            r.getLong("id"),
            r.getLong("request_id"),
            r.getString("action_type"),
            r.getString("action_date"),
            r.getString("condition_note")
        );
    }
}
