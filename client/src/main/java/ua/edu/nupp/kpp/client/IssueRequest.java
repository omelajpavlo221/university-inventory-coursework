package ua.edu.nupp.kpp.client;

public class IssueRequest {
    private Long id;
    private long assetId;
    private long userId;
    private int requestedQty;
    private double urgencyScore;
    private RequestState state;
    private double f1;
    private double f2;
    private double f3;
    private double priorityIndex;

    public IssueRequest() {}

    public IssueRequest(Long id, long assetId, long userId, int requestedQty, double urgencyScore,
                        RequestState state, double f1, double f2, double f3, double priorityIndex) {
        this.id = id;
        this.assetId = assetId;
        this.userId = userId;
        this.requestedQty = requestedQty;
        this.urgencyScore = urgencyScore;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.priorityIndex = priorityIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getAssetId() { return assetId; }
    public void setAssetId(long assetId) { this.assetId = assetId; }
    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }
    public int getRequestedQty() { return requestedQty; }
    public void setRequestedQty(int requestedQty) { this.requestedQty = requestedQty; }
    public double getUrgencyScore() { return urgencyScore; }
    public void setUrgencyScore(double urgencyScore) { this.urgencyScore = urgencyScore; }
    public RequestState getState() { return state; }
    public void setState(RequestState state) { this.state = state; }
    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }
    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }
    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }
    public double getPriorityIndex() { return priorityIndex; }
    public void setPriorityIndex(double priorityIndex) { this.priorityIndex = priorityIndex; }
}
