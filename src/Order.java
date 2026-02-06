// Order.java - POJO for Order entity
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * POJO (Plain Old Java Object) representing an order.
 * Includes a list of OrderItem objects associated with this order.
 */
class Order {
    private int orderId;
    private int customerId;
    private Timestamp orderDate;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod; // NEW: To store the payment method
    private List<OrderItem> orderItems; // List of items in this order

    public Order(int orderId, int customerId, Timestamp orderDate, BigDecimal totalAmount, String status, String paymentMethod) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod; // NEW
        this.orderItems = new ArrayList<>(); // Initialize the list to prevent NullPointerExceptions
    }

    // Getters
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public Timestamp getOrderDate() { return orderDate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public String getPaymentMethod() { return paymentMethod; } // NEW
    public List<OrderItem> getOrderItems() { return orderItems; }

    // Setters (useful for updating order properties, e.g., status)
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setStatus(String status) { this.status = status; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    // Helper method to add an item to the order's list of items
    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' + // NEW
                ", items count=" + orderItems.size() + // Show number of items for quick info
                '}';
    }
}