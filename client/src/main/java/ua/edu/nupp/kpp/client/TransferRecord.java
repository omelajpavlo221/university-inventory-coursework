package ua.edu.nupp.kpp.client;

public class TransferRecord {
    private Long id;
    private long requestId;
    private String actionType;
    private String actionDate;
    private String conditionNote;

    public TransferRecord() {}

    public TransferRecord(Long id, long requestId, String actionType, String actionDate, String conditionNote) {
        this.id = id;
        this.requestId = requestId;
        this.actionType = actionType;
        this.actionDate = actionDate;
        this.conditionNote = conditionNote;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getActionDate() { return actionDate; }
    public void setActionDate(String actionDate) { this.actionDate = actionDate; }
    public String getConditionNote() { return conditionNote; }
    public void setConditionNote(String conditionNote) { this.conditionNote = conditionNote; }
}
