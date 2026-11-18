package ua.edu.nupp.kpp.server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IssueRequestDAO {
    public void add(IssueRequest req) throws SQLException {
        String sql = "INSERT INTO issue_request(asset_id, user_id, requested_qty, urgency_score, state, f1, f2, f3, priority_index) VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, req);
            p.executeUpdate();
        }
    }

    public List<IssueRequest> getAll() throws SQLException {
        List<IssueRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM issue_request ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public IssueRequest getById(long id) throws SQLException {
        String sql = "SELECT * FROM issue_request WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(IssueRequest req) throws SQLException {
        String sql = "UPDATE issue_request SET asset_id=?, user_id=?, requested_qty=?, urgency_score=?, state=?, f1=?, f2=?, f3=?, priority_index=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, req);
            p.setLong(10, req.getId());
            p.executeUpdate();
        }
    }

    public void updateState(long id, RequestState state) throws SQLException {
        String sql = "UPDATE issue_request SET state=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, state.name());
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM issue_request WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, IssueRequest req) throws SQLException {
        p.setLong(1, req.getAssetId());
        p.setLong(2, req.getUserId());
        p.setInt(3, req.getRequestedQty());
        p.setDouble(4, req.getUrgencyScore());
        p.setString(5, req.getState().name());
        p.setDouble(6, req.getF1());
        p.setDouble(7, req.getF2());
        p.setDouble(8, req.getF3());
        p.setDouble(9, req.getPriorityIndex());
    }

    private IssueRequest map(ResultSet r) throws SQLException {
        return new IssueRequest(
            r.getLong("id"),
            r.getLong("asset_id"),
            r.getLong("user_id"),
            r.getInt("requested_qty"),
            r.getDouble("urgency_score"),
            RequestState.valueOf(r.getString("state")),
            r.getDouble("f1"),
            r.getDouble("f2"),
            r.getDouble("f3"),
            r.getDouble("priority_index")
        );
    }
}
