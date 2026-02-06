// OrderItem.java - POJO for OrderItem entity
import java.math.BigDecimal;

/**
 * POJO (Plain Old Java Object) representing an item within an order.
 */
class OrderItem {
    private int orderItemId;
    private int orderId;
    private int productId;
    private String productName; // Denormalized for convenience, stored in DB and fetched
    private int quantity;
    private BigDecimal priceAtPurchase; // Important for historical accuracy

    public OrderItem(int orderItemId, int orderId, int productId, String productName, int quantity, BigDecimal priceAtPurchase) {
        this.orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    // Getters
    public int getOrderItemId() { return orderItemId; }
    public int getOrderId() { return orderId; }
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderItemId=" + orderItemId +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", priceAtPurchase=" + priceAtPurchase +
                '}';
    }
}
