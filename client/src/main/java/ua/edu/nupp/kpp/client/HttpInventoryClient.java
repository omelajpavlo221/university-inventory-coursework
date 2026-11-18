package ua.edu.nupp.kpp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpInventoryClient {
    private static final String BASE = "http://localhost:8080/university-inventory-server";
    private final HttpClient http = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // Asset CRUD
    public List<Asset> getAssets() {
        return Arrays.asList(read(get("/assets"), Asset[].class));
    }

    public void addAsset(Asset a) {
        post("/assets", assetForm("add", a));
    }

    public void updateAsset(Asset a) {
        post("/assets", assetForm("update", a) + "&id=" + a.getId());
    }

    public void deleteAsset(long id) {
        post("/assets", "action=delete&id=" + id);
    }

    // AppUser CRUD
    public List<AppUser> getUsers() {
        return Arrays.asList(read(get("/users"), AppUser[].class));
    }

    public void addUser(AppUser u) {
        post("/users", userForm("add", u));
    }

    public void updateUser(AppUser u) {
        post("/users", userForm("update", u) + "&id=" + u.getId());
    }

    public void deleteUser(long id) {
        post("/users", "action=delete&id=" + id);
    }

    // IssueRequest CRUD & Lifecycle
    public List<IssueRequest> getRequests() {
        return Arrays.asList(read(get("/requests"), IssueRequest[].class));
    }

    public void addRequest(long assetId, long userId, int qty, double urgency) {
        post("/requests", "action=add&assetId=" + assetId + "&userId=" + userId + "&requestedQty=" + qty + "&urgencyScore=" + urgency);
    }

    public void updateRequest(long id, long assetId, long userId, int qty, double urgency) {
        post("/requests", "action=update&id=" + id + "&assetId=" + assetId + "&userId=" + userId + "&requestedQty=" + qty + "&urgencyScore=" + urgency);
    }

    public void changeRequestState(long id, RequestState state) {
        if (state == null) throw new IllegalArgumentException("Оберіть новий стан зі списку");
        post("/requests", "action=transition&id=" + id + "&state=" + enc(state.name()));
    }

    public void deleteRequest(long id) {
        post("/requests", "action=delete&id=" + id);
    }

    // TransferRecord CRUD
    public List<TransferRecord> getTransfers() {
        return Arrays.asList(read(get("/transfers"), TransferRecord[].class));
    }

    public void addTransfer(TransferRecord tr) {
        post("/transfers", transferForm("add", tr));
    }

    public void updateTransfer(TransferRecord tr) {
        post("/transfers", transferForm("update", tr) + "&id=" + tr.getId());
    }

    public void deleteTransfer(long id) {
        post("/transfers", "action=delete&id=" + id);
    }

    private String assetForm(String action, Asset a) {
        return "action=" + action + "&invNumber=" + enc(a.getInvNumber()) + "&name=" + enc(a.getName()) +
               "&category=" + enc(a.getCategory()) + "&availableQty=" + a.getAvailableQty() +
               "&criticalityScore=" + a.getCriticalityScore();
    }

    private String userForm(String action, AppUser u) {
        return "action=" + action + "&fullName=" + enc(u.getFullName()) + "&department=" + enc(u.getDepartment()) +
               "&role=" + enc(u.getRole()) + "&email=" + enc(u.getEmail());
    }

    private String transferForm(String action, TransferRecord tr) {
        return "action=" + action + "&requestId=" + tr.getRequestId() + "&actionType=" + enc(tr.getActionType()) +
               "&actionDate=" + enc(tr.getActionDate()) + "&conditionNote=" + enc(tr.getConditionNote());
    }

    private String get(String path) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path)).GET().build();
        return send(req);
    }

    private String post(String path, String form) {
        HttpRequest req = HttpRequest.newBuilder(URI.create(BASE + path))
            .header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(form))
            .build();
        return send(req);
    }

    private String send(HttpRequest req) {
        try {
            HttpResponse<String> r = http.send(req, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (r.statusCode() >= 400) {
                String msg = extractError(r.body());
                throw new RuntimeException(msg);
            }
            return r.body();
        } catch (RuntimeException re) {
            throw re;
        } catch (Exception e) {
            throw new RuntimeException("Помилка з'єднання із сервером: " + e.getMessage(), e);
        }
    }

    private String extractError(String body) {
        try {
            JsonNode node = mapper.readTree(body);
            if (node.has("error")) return node.get("error").asText();
        } catch (Exception ignored) {}
        return body;
    }

    private <T> T read(String json, Class<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new RuntimeException("Помилка розбору JSON-відповіді", e);
        }
    }

    private String enc(String s) {
        return URLEncoder.encode(s == null ? "" : s, StandardCharsets.UTF_8);
    }
}
