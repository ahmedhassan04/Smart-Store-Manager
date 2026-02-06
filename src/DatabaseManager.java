// DatabaseManager.java - Handles all database operations (Simplified)
import java.sql.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class DatabaseManager {
    private String url;
    private String username;
    private String password;
    private Connection connection;

    public DatabaseManager(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
        this.connection = null;
    }

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(url, username, password);
            connection.setAutoCommit(true);
        }
    }

    public void close() {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // --- Admin Operations ---
    public Admin validateAdmin(String username, String password) throws SQLException {
        String sql = "SELECT admin_id, username, password FROM admins WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Admin(
                            rs.getInt("admin_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    // --- Customer Operations ---
    public int insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (name, email, phone_number, address) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, customer.getAddress());
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, name, email, phone_number, address FROM customers";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone_number"),
                        rs.getString("address")
                ));
            }
        }
        return customers;
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT customer_id, name, email, phone_number, address FROM customers WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone_number"),
                            rs.getString("address")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET name = ?, email = ?, phone_number = ?, address = ? WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, customer.getAddress());
            pstmt.setInt(5, customer.getCustomerId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "DELETE FROM customers WHERE customer_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // --- Customer Account Operations ---
    public int registerCustomerAccount(CustomerAccount customerAccount) throws SQLException {
        String sql = "INSERT INTO customer_accounts (customer_id, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, customerAccount.getCustomerId());
            pstmt.setString(2, customerAccount.getUsername());
            pstmt.setString(3, customerAccount.getPassword());
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM customer_accounts WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public CustomerAccount validateCustomerAccount(String username, String password) throws SQLException {
        String sql = "SELECT account_id, customer_id, username, password FROM customer_accounts WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new CustomerAccount(
                            rs.getInt("account_id"),
                            rs.getInt("customer_id"),
                            rs.getString("username"),
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    // --- Product Operations ---
    public int insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (name, description, price, stock_quantity, image_url) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());
            pstmt.setString(5, product.getImageUrl());
            if (pstmt.executeUpdate() > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }
        return -1;
    }

    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_id, name, description, price, stock_quantity, image_url FROM products";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getBigDecimal("price"),
                        rs.getInt("stock_quantity"),
                        rs.getString("image_url")
                ));
            }
        }
        return products;
    }

    public Product getProductById(int productId) throws SQLException {
        String sql = "SELECT product_id, name, description, price, stock_quantity, image_url FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("product_id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock_quantity"),
                            rs.getString("image_url")
                    );
                }
            }
        }
        return null;
    }

    public boolean updateProduct(Product product) throws SQLException {
        String sql = "UPDATE products SET name = ?, description = ?, price = ?, stock_quantity = ?, image_url = ? WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, product.getName());
            pstmt.setString(2, product.getDescription());
            pstmt.setBigDecimal(3, product.getPrice());
            pstmt.setInt(4, product.getStockQuantity());
            pstmt.setString(5, product.getImageUrl());
            pstmt.setInt(6, product.getProductId());
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean deleteProduct(int productId) throws SQLException {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;
        }
    }

    // --- Order Operations ---
    public int createOrder(int customerId, Map<Integer, Integer> cartItems, BigDecimal totalAmount, String paymentMethod) throws SQLException {
        connection.setAutoCommit(false);
        int orderId = -1;
        try {
            String orderSql = "INSERT INTO orders (customer_id, total_amount, status, payment_method) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                pstmt.setInt(1, customerId);
                pstmt.setBigDecimal(2, totalAmount);
                pstmt.setString(3, "Pending");
                pstmt.setString(4, paymentMethod);
                pstmt.executeUpdate();
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        orderId = rs.getInt(1);
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            String itemSql = "INSERT INTO order_items (order_id, product_id, quantity, price_at_purchase) VALUES (?, ?, ?, ?)";
            String updateStockSql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";

            for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                Product product = getProductById(entry.getKey());
                if (product.getStockQuantity() < entry.getValue()) {
                    throw new SQLException("Insufficient stock for " + product.getName());
                }
                try (PreparedStatement itemPstmt = connection.prepareStatement(itemSql)) {
                    itemPstmt.setInt(1, orderId);
                    itemPstmt.setInt(2, entry.getKey());
                    itemPstmt.setInt(3, entry.getValue());
                    itemPstmt.setBigDecimal(4, product.getPrice());
                    itemPstmt.executeUpdate();
                }
                try (PreparedStatement stockPstmt = connection.prepareStatement(updateStockSql)) {
                    stockPstmt.setInt(1, entry.getValue());
                    stockPstmt.setInt(2, entry.getKey());
                    stockPstmt.setInt(3, entry.getValue());
                    if (stockPstmt.executeUpdate() == 0) {
                        throw new SQLException("Failed to update stock for " + product.getName());
                    }
                }
            }
            connection.commit();
            return orderId;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.customer_id, o.order_date, o.total_amount, o.status, o.payment_method, c.name AS customer_name " +
                "FROM orders o JOIN customers c ON o.customer_id = c.customer_id ORDER BY o.order_date DESC";
        String itemSql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.price_at_purchase, p.name AS product_name " +
                "FROM order_items oi JOIN products p ON oi.product_id = p.product_id WHERE oi.order_id = ?";

        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                Order order = new Order(orderId, rs.getInt("customer_id"), rs.getTimestamp("order_date"), rs.getBigDecimal("total_amount"), rs.getString("status"), rs.getString("payment_method"));
                try (PreparedStatement itemPstmt = connection.prepareStatement(itemSql)) {
                    itemPstmt.setInt(1, orderId);
                    try (ResultSet itemRs = itemPstmt.executeQuery()) {
                        while (itemRs.next()) {
                            order.addOrderItem(new OrderItem(itemRs.getInt("order_item_id"), itemRs.getInt("order_id"), itemRs.getInt("product_id"), itemRs.getString("product_name"), itemRs.getInt("quantity"), itemRs.getBigDecimal("price_at_purchase")));
                        }
                    }
                }
                orders.add(order);
            }
        }
        return orders;
    }

    public List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String orderSql = "SELECT o.order_id, o.customer_id, o.order_date, o.total_amount, o.status, o.payment_method " +
                "FROM orders o WHERE o.customer_id = ? ORDER BY o.order_date DESC";

        String itemSql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.price_at_purchase, p.name AS product_name " +
                "FROM order_items oi JOIN products p ON oi.product_id = p.product_id WHERE oi.order_id = ?";

        try (PreparedStatement orderPstmt = connection.prepareStatement(orderSql)) {
            orderPstmt.setInt(1, customerId);
            try (ResultSet rs = orderPstmt.executeQuery()) {
                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    Order order = new Order(
                            orderId,
                            rs.getInt("customer_id"),
                            rs.getTimestamp("order_date"),
                            rs.getBigDecimal("total_amount"),
                            rs.getString("status"),
                            rs.getString("payment_method")
                    );

                    try (PreparedStatement itemPstmt = connection.prepareStatement(itemSql)) {
                        itemPstmt.setInt(1, orderId);
                        try (ResultSet itemRs = itemPstmt.executeQuery()) {
                            while (itemRs.next()) {
                                order.addOrderItem(new OrderItem(
                                        itemRs.getInt("order_item_id"),
                                        itemRs.getInt("order_id"),
                                        itemRs.getInt("product_id"),
                                        itemRs.getString("product_name"),
                                        itemRs.getInt("quantity"),
                                        itemRs.getBigDecimal("price_at_purchase")
                                ));
                            }
                        }
                    }
                    orders.add(order);
                }
            }
        }
        return orders;
    }


    public Order getOrderById(int orderId) throws SQLException {
        Order order = null;
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    order = new Order(rs.getInt("order_id"), rs.getInt("customer_id"), rs.getTimestamp("order_date"), rs.getBigDecimal("total_amount"), rs.getString("status"), rs.getString("payment_method"));
                    String itemSql = "SELECT oi.order_item_id, oi.order_id, oi.product_id, oi.quantity, oi.price_at_purchase, p.name AS product_name " +
                            "FROM order_items oi JOIN products p ON oi.product_id = p.product_id WHERE oi.order_id = ?";
                    try (PreparedStatement itemPstmt = connection.prepareStatement(itemSql)) {
                        itemPstmt.setInt(1, orderId);
                        try (ResultSet itemRs = itemPstmt.executeQuery()) {
                            while (itemRs.next()) {
                                order.addOrderItem(new OrderItem(itemRs.getInt("order_item_id"), itemRs.getInt("order_id"), itemRs.getInt("product_id"), itemRs.getString("product_name"), itemRs.getInt("quantity"), itemRs.getBigDecimal("price_at_purchase")));
                            }
                        }
                    }
                }
            }
        }
        return order;
    }

    public boolean updateOrderStatus(int orderId, String newStatus) throws SQLException {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, orderId);
            return pstmt.executeUpdate() > 0;
        }
    }
}
