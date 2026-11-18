package ua.edu.nupp.kpp.server;

public class Asset {
    private Long id;
    private String invNumber;
    private String name;
    private String category;
    private int availableQty;
    private double criticalityScore;

    public Asset() {}

    public Asset(Long id, String invNumber, String name, String category, int availableQty, double criticalityScore) {
        this.id = id;
        this.invNumber = invNumber;
        this.name = name;
        this.category = category;
        this.availableQty = availableQty;
        this.criticalityScore = criticalityScore;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInvNumber() { return invNumber; }
    public void setInvNumber(String invNumber) { this.invNumber = invNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public int getAvailableQty() { return availableQty; }
    public void setAvailableQty(int availableQty) { this.availableQty = availableQty; }
    public double getCriticalityScore() { return criticalityScore; }
    public void setCriticalityScore(double criticalityScore) { this.criticalityScore = criticalityScore; }
}
