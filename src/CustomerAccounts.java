// CustomerAccount.java - POJO for Customer Account entity (Simplified)

class CustomerAccount {
    private int accountId;
    private int customerId;
    private String username;
    private String password;

    /**
     * Constructor for a new customer account.
     */
    public CustomerAccount(int customerId, String username, String password) {
        this.customerId = customerId;
        this.username = username;
        this.password = password;
    }

    /**
     * Full constructor for retrieving data from the database.
     */
    public CustomerAccount(int accountId, int customerId, String username, String password) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.username = username;
        this.password = password;
    }

    // Getters
    public int getAccountId() { return accountId; }
    public int getCustomerId() { return customerId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    // Setters
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    @Override
    public String toString() {
        return "CustomerAccount{" +
                "accountId=" + accountId +
                ", customerId=" + customerId +
                ", username='" + username + '\'' +
                '}';
    }
}
