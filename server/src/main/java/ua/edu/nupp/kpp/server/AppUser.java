package ua.edu.nupp.kpp.server;

public class AppUser {
    private Long id;
    private String fullName;
    private String department;
    private String role;
    private String email;

    public AppUser() {}

    public AppUser(Long id, String fullName, String department, String role, String email) {
        this.id = id;
        this.fullName = fullName;
        this.department = department;
        this.role = role;
        this.email = email;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
