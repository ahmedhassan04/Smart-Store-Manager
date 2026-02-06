// Admin.java - POJO for Admin entity (Simplified)

class Admin {
    private int adminId;
    private String username;
    private String password;

    public Admin(int adminId, String username, String password) {
        this.adminId = adminId;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getAdminId() { return adminId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return "Admin{" +
                "adminId=" + adminId +
                ", username='" + username + '\'' +
                '}';
    }
}
