// StoreManagementApp.java - The main GUI application with all requested changes
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

public class StoreManagementApp extends JFrame {

    // --- Member Variables ---
    private final DatabaseManager dbManager;
    private final CardLayout cardLayout;
    private final JPanel mainPanel;
    private final Cart currentCart;

    // Session-related variables
    private CustomerAccount loggedInCustomerAccount;
    private Customer loggedInCustomerProfile;
    private List<Order> cachedOrders;

    // UI Panels
    private JPanel welcomePanel;
    private JPanel loginChoicePanel;
    private JPanel adminLoginPanel;
    private JPanel customerLoginPanel;
    private JTabbedPane adminDashboardPanel;
    private JPanel shopPanel;

    // UI Components (grouped by panel for clarity)
    private JTextField adminUsernameField;
    private JPasswordField adminPasswordField;
    private JTextField customerLoginUsernameField;
    private JPasswordField customerLoginPasswordField;
    private JLabel adminLoginStatusLabel, customerLoginStatusLabel;

    private JTextField productIdField, productNameField, productPriceField, productStockField, productImageUrlField;
    private JTextArea productDescriptionArea;
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JLabel productStatusLabel;

    private JTextField customerIdField, customerNameField, customerEmailField, customerPhoneField;
    private JTextArea customerAddressArea;
    private JTable customerTable;
    private DefaultTableModel customerTableModel;
    private JLabel customerStatusLabel;

    private JTable orderTable;
    private DefaultTableModel orderTableModel;
    private JLabel orderStatusLabel;
    private JTextField orderIdField, orderStatusTextField;
    private JTable orderItemsTable;
    private DefaultTableModel orderItemsTableModel;

    private JPanel productDisplayPanel;
    private JTextField customerIdForShopField;
    private JLabel shopStatusLabel;
    private JButton viewMyOrdersButton;
    private JLabel customerIdForShopLabel;

    private JDialog cartDialog, registrationDialog, myOrdersDialog;
    private JTextField cartItemIdField, cartItemQuantityField;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel cartTotalLabel;
    private JTextField regNameField, regEmailField, regPhoneField, regUsernameField;
    private JTextArea regAddressArea;
    private JPasswordField regPasswordField;
    private JLabel regStatusLabel;

    private JTable myOrdersTable;
    private DefaultTableModel myOrdersTableModel;
    private JTable myOrderItemsTable;
    private DefaultTableModel myOrderItemsTableModel;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]+$");


    // --- Constructor ---
    public StoreManagementApp(DatabaseManager dbManager) {
        this.dbManager = dbManager;
        this.currentCart = new Cart();
        this.cachedOrders = new ArrayList<>();

        setTitle("Store Management System");
        // CHANGE: Increased height to prevent scrolling on admin panel
        setSize(1200, 900);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        add(mainPanel);

        initWelcomePanel();
        initLoginChoicePanel();
        initAdminLoginPanel();
        initCustomerLoginPanel();
        initAdminDashboardPanel();
        initShopPanel();
        initMyOrdersDialog();

        mainPanel.add(welcomePanel, "Welcome");
        mainPanel.add(loginChoicePanel, "LoginChoice");
        mainPanel.add(adminLoginPanel, "AdminLogin");
        mainPanel.add(customerLoginPanel, "CustomerLogin");
        mainPanel.add(createAdminWrapperPanel(), "AdminDashboard");
        mainPanel.add(shopPanel, "Shop");

        cardLayout.show(mainPanel, "Welcome");
    }

    // --- Panel Initialization Methods ---

    private void initWelcomePanel() {
        welcomePanel = new JPanel(new GridBagLayout()) {
            private Image backgroundImage;
            {
                try {
                    URL imageUrl = new URL("https://images.stockcake.com/public/d/9/5/d9577658-936a-4fe4-9900-e8f19748e876_large/electronics-retail-shop-stockcake.jpg");
                    backgroundImage = ImageIO.read(imageUrl);
                } catch (IOException e) {
                    setBackground(new Color(230, 240, 255));
                }
            }
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        welcomePanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false);

        JLabel titleLabel = new JLabel("Welcome to the ShelfWare", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 52));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("A Store Management System", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.ITALIC, 24));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleContainer.add(titleLabel);
        titleContainer.add(subtitleLabel);

        welcomePanel.add(titleContainer, gbc);

        JTextArea featuresArea = new JTextArea(
                "What You Can Do:\n\n" +
                        "●  Log in securely as an Admin or Customer.\n" +
                        "●  Sign up easily as a new customer.\n" +
                        "●  Manage all your products and track stock levels.\n" +
                        "●  Add items to a shopping cart with a single click.\n" +
                        "●  Checkout smoothly with multiple payment options.\n" +
                        "●  Automatically create and save PDF receipts for sales.\n" +
                        "●  View and update customer order information."
        );
        featuresArea.setFont(new Font("Segoe UI", Font.BOLD, 16));
        featuresArea.setEditable(false);
        featuresArea.setOpaque(false);
        featuresArea.setForeground(Color.WHITE);
        featuresArea.setWrapStyleWord(true);
        featuresArea.setLineWrap(true);
        featuresArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(255, 255, 255, 150)),
                BorderFactory.createEmptyBorder(10, 15, 10, 10)
        ));
        gbc.ipady = 40;
        gbc.weighty = 0.5;
        welcomePanel.add(featuresArea, gbc);
        gbc.ipady = 0;
        gbc.weighty = 0;

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setOpaque(false);
        JButton goToLoginButton = new JButton("Login or Register");
        styleGenericButton(goToLoginButton, new Color(46, 204, 113), new Color(39, 174, 96));
        goToLoginButton.addActionListener(e -> showLoginChoicePanel());
        buttonPanel.add(goToLoginButton);
        JButton goToShopGuestButton = new JButton("Browse as Guest");
        styleGenericButton(goToShopGuestButton, new Color(52, 152, 219), new Color(41, 128, 185));
        goToShopGuestButton.addActionListener(e -> {
            clearUserSession();
            showShopPanel();
        });
        buttonPanel.add(goToShopGuestButton);
        welcomePanel.add(buttonPanel, gbc);
    }

    private void initLoginChoicePanel() {
        loginChoicePanel = new JPanel(new GridBagLayout());
        loginChoicePanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));
        loginChoicePanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        JLabel titleLabel = new JLabel("How would you like to proceed?", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        gbc.gridy = 0;
        loginChoicePanel.add(titleLabel, gbc);

        JButton adminLoginButton = new JButton("Login as Administrator");
        styleGenericButton(adminLoginButton, new Color(231, 76, 60), new Color(192, 57, 43));
        gbc.gridy = 1; gbc.ipady = 20;
        adminLoginButton.addActionListener(e -> showAdminLoginPanel());
        loginChoicePanel.add(adminLoginButton, gbc);

        JButton customerLoginButton = new JButton("Login or Register as Customer");
        styleGenericButton(customerLoginButton, new Color(52, 152, 219), new Color(41, 128, 185));
        gbc.gridy = 2;
        customerLoginButton.addActionListener(e -> showCustomerLoginPanel());
        loginChoicePanel.add(customerLoginButton, gbc);

        JButton backButton = new JButton("Back to Welcome");
        styleGenericButton(backButton, new Color(149, 165, 166), new Color(127, 140, 141));
        gbc.gridy = 3; gbc.ipady = 0; gbc.insets = new Insets(40, 10, 10, 10);
        backButton.addActionListener(e -> showWelcomePanel());
        loginChoicePanel.add(backButton, gbc);
    }

    private void initAdminLoginPanel() {
        adminLoginPanel = new JPanel(new GridBagLayout());
        adminLoginPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Administrator Login", 0, 0, new Font("Serif", Font.BOLD, 16)));
        adminLoginPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; adminLoginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; adminUsernameField = new JTextField(15); adminLoginPanel.add(adminUsernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; adminLoginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; adminPasswordField = new JPasswordField(15); adminLoginPanel.add(adminPasswordField, gbc);

        JButton loginButton = new JButton("Admin Login");
        styleGenericButton(loginButton, new Color(231, 76, 60), new Color(192, 57, 43));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loginButton.addActionListener(e -> attemptAdminLogin());
        adminPasswordField.addActionListener(e -> attemptAdminLogin());
        adminLoginPanel.add(loginButton, gbc);

        adminLoginStatusLabel = new JLabel("Please enter your admin credentials.", SwingConstants.CENTER);
        gbc.gridy = 3;
        adminLoginPanel.add(adminLoginStatusLabel, gbc);

        JButton backButton = new JButton("Back");
        styleGenericButton(backButton, new Color(149, 165, 166), new Color(127, 140, 141));
        gbc.gridy = 4;
        backButton.addActionListener(e -> showLoginChoicePanel());
        adminLoginPanel.add(backButton, gbc);
    }

    private void initCustomerLoginPanel() {
        customerLoginPanel = new JPanel(new GridBagLayout());
        customerLoginPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Customer Login & Registration", 0, 0, new Font("Serif", Font.BOLD, 16)));
        customerLoginPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; customerLoginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1; customerLoginUsernameField = new JTextField(15); customerLoginPanel.add(customerLoginUsernameField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; customerLoginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1; customerLoginPasswordField = new JPasswordField(15); customerLoginPanel.add(customerLoginPasswordField, gbc);

        JButton loginButton = new JButton("Customer Login");
        styleGenericButton(loginButton, new Color(52, 152, 219), new Color(41, 128, 185));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loginButton.addActionListener(e -> attemptCustomerLogin());
        customerLoginPasswordField.addActionListener(e -> attemptCustomerLogin());
        customerLoginPanel.add(loginButton, gbc);

        JLabel orLabel = new JLabel("--- OR ---", SwingConstants.CENTER);
        gbc.gridy = 3; customerLoginPanel.add(orLabel, gbc);

        JButton registerButton = new JButton("Register New Customer");
        styleGenericButton(registerButton, new Color(46, 204, 113), new Color(39, 174, 96));
        gbc.gridy = 4;
        registerButton.addActionListener(e -> showCustomerRegistrationDialog());
        customerLoginPanel.add(registerButton, gbc);

        customerLoginStatusLabel = new JLabel("Please log in or register a new account.", SwingConstants.CENTER);
        gbc.gridy = 5;
        customerLoginPanel.add(customerLoginStatusLabel, gbc);

        JButton backButton = new JButton("Back");
        styleGenericButton(backButton, new Color(149, 165, 166), new Color(127, 140, 141));
        gbc.gridy = 6;
        backButton.addActionListener(e -> showLoginChoicePanel());
        customerLoginPanel.add(backButton, gbc);
    }

    private void initAdminDashboardPanel() {
        adminDashboardPanel = new JTabbedPane();
        adminDashboardPanel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JPanel productMgmtPanel = new JPanel(new BorderLayout(10, 10));
        productMgmtPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel productInputPanel = new JPanel(new GridBagLayout());
        productInputPanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        GridBagConstraints gbcProd = new GridBagConstraints();
        gbcProd.insets = new Insets(5, 5, 5, 5); gbcProd.fill = GridBagConstraints.HORIZONTAL; gbcProd.weightx = 1.0;
        gbcProd.gridx = 0; gbcProd.gridy = 0; productInputPanel.add(new JLabel("Product ID (for Update/Delete):"), gbcProd);
        gbcProd.gridx = 1; gbcProd.gridy = 0; productIdField = new JTextField(15); productInputPanel.add(productIdField, gbcProd);
        gbcProd.gridx = 0; gbcProd.gridy = 1; productInputPanel.add(new JLabel("Name:"), gbcProd);
        gbcProd.gridx = 1; gbcProd.gridy = 1; productNameField = new JTextField(15); productInputPanel.add(productNameField, gbcProd);
        gbcProd.gridx = 0; gbcProd.gridy = 2; productInputPanel.add(new JLabel("Description:"), gbcProd);
        gbcProd.gridx = 1; gbcProd.gridy = 2;
        productDescriptionArea = new JTextArea(3, 15); productDescriptionArea.setLineWrap(true); productDescriptionArea.setWrapStyleWord(true);
        productInputPanel.add(new JScrollPane(productDescriptionArea), gbcProd);
        gbcProd.gridx = 0; gbcProd.gridy = 3; productInputPanel.add(new JLabel("Price:"), gbcProd);
        gbcProd.gridx = 1; gbcProd.gridy = 3; productPriceField = new JTextField(15); productInputPanel.add(productPriceField, gbcProd);
        gbcProd.gridx = 0; gbcProd.gridy = 4; productInputPanel.add(new JLabel("Stock Quantity:"), gbcProd);
        gbcProd.gridx = 1; gbcProd.gridy = 4; productStockField = new JTextField(15); productInputPanel.add(productStockField, gbcProd);
        gbcProd.gridx = 0; gbcProd.gridy = 5; productInputPanel.add(new JLabel("Image URL:"), gbcProd);
        gbcProd.gridx = 1; gbcProd.gridy = 5; productImageUrlField = new JTextField(15); productInputPanel.add(productImageUrlField, gbcProd);

        JPanel productButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton productAddButton = new JButton("Add Product"); styleGenericButton(productAddButton, new Color(46, 204, 113), new Color(39, 174, 96));
        JButton productUpdateButton = new JButton("Update Product"); styleGenericButton(productUpdateButton, new Color(52, 152, 219), new Color(41, 128, 185));
        JButton productDeleteButton = new JButton("Delete Product"); styleGenericButton(productDeleteButton, new Color(231, 76, 60), new Color(192, 57, 43));
        JButton productClearButton = new JButton("Clear Fields"); styleGenericButton(productClearButton, new Color(149, 165, 166), new Color(127, 140, 141));
        productAddButton.addActionListener(e -> addProduct()); productUpdateButton.addActionListener(e -> updateProduct());
        productDeleteButton.addActionListener(e -> deleteProduct()); productClearButton.addActionListener(e -> clearProductFields());
        productButtonsPanel.add(productAddButton); productButtonsPanel.add(productUpdateButton);
        productButtonsPanel.add(productDeleteButton); productButtonsPanel.add(productClearButton);

        JPanel productTopPanel = new JPanel(new BorderLayout());
        productTopPanel.add(productInputPanel, BorderLayout.CENTER); productTopPanel.add(productButtonsPanel, BorderLayout.SOUTH);
        productMgmtPanel.add(productTopPanel, BorderLayout.NORTH);

        String[] productColumnNames = {"ID", "Name", "Description", "Price", "Stock", "Image URL"};
        productTableModel = new DefaultTableModel(productColumnNames, 0) { public boolean isCellEditable(int r, int c){ return false; }};
        productTable = new JTable(productTableModel); productTable.setFillsViewportHeight(true);
        productMgmtPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);
        productStatusLabel = new JLabel("Ready.", SwingConstants.CENTER);
        productMgmtPanel.add(productStatusLabel, BorderLayout.SOUTH);
        productTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = productTable.getSelectedRow();
                if (row != -1) {
                    productIdField.setText(productTable.getValueAt(row, 0).toString());
                    productNameField.setText(productTable.getValueAt(row, 1).toString());
                    productDescriptionArea.setText(productTable.getValueAt(row, 2).toString());
                    productPriceField.setText(productTable.getValueAt(row, 3).toString());
                    productStockField.setText(productTable.getValueAt(row, 4).toString());
                    Object imgUrl = productTable.getValueAt(row, 5);
                    productImageUrlField.setText(imgUrl != null ? imgUrl.toString() : "");
                }
            }
        });
        adminDashboardPanel.addTab("Product Management", productMgmtPanel);

        JPanel customerMgmtPanel = new JPanel(new BorderLayout(10, 10));
        customerMgmtPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel customerInputPanel = new JPanel(new GridBagLayout());
        customerInputPanel.setBorder(BorderFactory.createTitledBorder("Customer Details"));
        GridBagConstraints gbcCust = new GridBagConstraints();
        gbcCust.insets = new Insets(5,5,5,5); gbcCust.fill = GridBagConstraints.HORIZONTAL; gbcCust.weightx = 1.0;
        gbcCust.gridx = 0; gbcCust.gridy = 0; customerInputPanel.add(new JLabel("Customer ID Add/Delete):"), gbcCust);
        gbcCust.gridx = 1; gbcCust.gridy = 0; customerIdField = new JTextField(15); customerInputPanel.add(customerIdField, gbcCust);
        gbcCust.gridx = 0; gbcCust.gridy = 1; customerInputPanel.add(new JLabel("Name:"), gbcCust);
        gbcCust.gridx = 1; gbcCust.gridy = 1; customerNameField = new JTextField(15); customerInputPanel.add(customerNameField, gbcCust);
        gbcCust.gridx = 0; gbcCust.gridy = 2; customerInputPanel.add(new JLabel("Email:"), gbcCust);
        gbcCust.gridx = 1; gbcCust.gridy = 2; customerEmailField = new JTextField(15); customerInputPanel.add(customerEmailField, gbcCust);
        gbcCust.gridx = 0; gbcCust.gridy = 3; customerInputPanel.add(new JLabel("Phone:"), gbcCust);
        gbcCust.gridx = 1; gbcCust.gridy = 3; customerPhoneField = new JTextField(15); customerInputPanel.add(customerPhoneField, gbcCust);
        gbcCust.gridx = 0; gbcCust.gridy = 4; customerInputPanel.add(new JLabel("Address:"), gbcCust);
        gbcCust.gridx = 1; gbcCust.gridy = 4;
        customerAddressArea = new JTextArea(3, 15); customerAddressArea.setLineWrap(true);
        customerInputPanel.add(new JScrollPane(customerAddressArea), gbcCust);

        JPanel customerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton customerAddButton = new JButton("Add Customer"); styleGenericButton(customerAddButton, new Color(46, 204, 113), new Color(39, 174, 96));
        JButton customerDeleteButton = new JButton("Delete Customer"); styleGenericButton(customerDeleteButton, new Color(231, 76, 60), new Color(192, 57, 43));
        JButton customerClearButton = new JButton("Clear Fields"); styleGenericButton(customerClearButton, new Color(149, 165, 166), new Color(127, 140, 141));
        customerAddButton.addActionListener(e -> addCustomer());
        customerDeleteButton.addActionListener(e -> deleteCustomer());
        customerClearButton.addActionListener(e -> clearCustomerFields());
        customerButtonsPanel.add(customerAddButton);
        customerButtonsPanel.add(customerDeleteButton);
        customerButtonsPanel.add(customerClearButton);

        JPanel customerTopPanel = new JPanel(new BorderLayout());
        customerTopPanel.add(customerInputPanel, BorderLayout.CENTER); customerTopPanel.add(customerButtonsPanel, BorderLayout.SOUTH);
        customerMgmtPanel.add(customerTopPanel, BorderLayout.NORTH);

        String[] customerColumnNames = {"ID", "Name", "Email", "Phone", "Address"};
        customerTableModel = new DefaultTableModel(customerColumnNames, 0) { public boolean isCellEditable(int r, int c) { return false; }};
        customerTable = new JTable(customerTableModel);
        customerMgmtPanel.add(new JScrollPane(customerTable), BorderLayout.CENTER);
        customerStatusLabel = new JLabel("Ready.");
        customerMgmtPanel.add(customerStatusLabel, BorderLayout.SOUTH);
        customerTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = customerTable.getSelectedRow();
                if(row != -1){
                    customerIdField.setText(customerTable.getValueAt(row, 0).toString());
                    customerNameField.setText(customerTable.getValueAt(row, 1).toString());
                    customerEmailField.setText(customerTable.getValueAt(row, 2).toString());
                    Object phone = customerTable.getValueAt(row, 3);
                    customerPhoneField.setText(phone != null ? phone.toString() : "");
                    Object addr = customerTable.getValueAt(row, 4);
                    customerAddressArea.setText(addr != null ? addr.toString() : "");
                }
            }
        });
        adminDashboardPanel.addTab("Customer Management", customerMgmtPanel);

        JPanel orderMgmtPanel = new JPanel(new BorderLayout(10, 10));
        orderMgmtPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel orderControlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        orderControlsPanel.add(new JLabel("Order ID:")); orderIdField = new JTextField(5); orderControlsPanel.add(orderIdField);
        orderControlsPanel.add(new JLabel("New Status:")); orderStatusTextField = new JTextField(10); orderControlsPanel.add(orderStatusTextField);
        JButton updateOrderStatusButton = new JButton("Update Status"); styleGenericButton(updateOrderStatusButton, new Color(52, 152, 219), new Color(41, 128, 185));
        updateOrderStatusButton.addActionListener(e -> updateOrder()); orderControlsPanel.add(updateOrderStatusButton);
        JButton refreshOrdersButton = new JButton("Refresh Orders"); styleGenericButton(refreshOrdersButton, new Color(149, 165, 166), new Color(127, 140, 141));
        refreshOrdersButton.addActionListener(e -> refreshOrderTable()); orderControlsPanel.add(refreshOrdersButton);
        orderMgmtPanel.add(orderControlsPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT); splitPane.setResizeWeight(0.6);
        String[] orderColumnNames = {"Order ID", "Customer ID", "Order Date", "Total Amount", "Status", "Payment Method"};
        orderTableModel = new DefaultTableModel(orderColumnNames, 0) { public boolean isCellEditable(int r, int c){ return false; }};
        orderTable = new JTable(orderTableModel); splitPane.setTopComponent(new JScrollPane(orderTable));
        String[] orderItemColumnNames = {"Item ID", "Product ID", "Product Name", "Quantity", "Price at Purchase"};
        orderItemsTableModel = new DefaultTableModel(orderItemColumnNames, 0) { public boolean isCellEditable(int r, int c){ return false; }};
        orderItemsTable = new JTable(orderItemsTableModel); splitPane.setBottomComponent(new JScrollPane(orderItemsTable));
        orderMgmtPanel.add(splitPane, BorderLayout.CENTER);
        orderStatusLabel = new JLabel("Ready."); orderMgmtPanel.add(orderStatusLabel, BorderLayout.SOUTH);
        orderTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = orderTable.getSelectedRow();
                if (row != -1) {
                    int id = (int) orderTable.getValueAt(row, 0);
                    orderIdField.setText(String.valueOf(id));
                    orderStatusTextField.setText(orderTable.getValueAt(row, 4).toString());
                    displayOrderItems(id);
                }
            }
        });
        adminDashboardPanel.addTab("Order Management", orderMgmtPanel);
    }

    private void initShopPanel() {
        shopPanel = new JPanel(new BorderLayout(10, 10));
        shopPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel shopControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        customerIdForShopLabel = new JLabel("Customer ID:");
        shopControls.add(customerIdForShopLabel);

        customerIdForShopField = new JTextField(10);
        shopControls.add(customerIdForShopField);

        JButton refreshProductsShopButton = new JButton("Refresh Products");
        styleGenericButton(refreshProductsShopButton, new Color(149, 165, 166), new Color(127, 140, 141));
        refreshProductsShopButton.addActionListener(e -> displayProductsInShop());
        shopControls.add(refreshProductsShopButton);

        viewMyOrdersButton = new JButton("View My Orders");
        styleGenericButton(viewMyOrdersButton, new Color(142, 68, 173), new Color(155, 89, 182));
        viewMyOrdersButton.addActionListener(e -> showMyOrdersDialog());
        shopControls.add(viewMyOrdersButton);

        JButton viewCartButton = new JButton("View Cart");
        styleGenericButton(viewCartButton, new Color(243, 156, 18), new Color(211, 84, 0));
        viewCartButton.addActionListener(e -> showCartDialog());
        shopControls.add(viewCartButton);

        JButton checkoutButton = new JButton("Proceed to Checkout");
        styleGenericButton(checkoutButton, new Color(46, 204, 113), new Color(39, 174, 96));
        checkoutButton.addActionListener(e -> checkoutCart());
        shopControls.add(checkoutButton);

        JButton returnToLoginFromShopButton = new JButton("Logout / Back to Welcome");
        styleGenericButton(returnToLoginFromShopButton, new Color(231, 76, 60), new Color(192, 57, 43));
        returnToLoginFromShopButton.addActionListener(e -> showWelcomePanel());
        shopControls.add(returnToLoginFromShopButton);

        shopPanel.add(shopControls, BorderLayout.NORTH);

        productDisplayPanel = new JPanel(new GridBagLayout());
        JScrollPane productScrollPane = new JScrollPane(productDisplayPanel);
        productScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        productScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        shopPanel.add(productScrollPane, BorderLayout.CENTER);

        shopStatusLabel = new JLabel("Welcome! Add products to your cart.", SwingConstants.CENTER);
        shopPanel.add(shopStatusLabel, BorderLayout.SOUTH);

        String[] cartColumnNames = {"Product ID", "Name", "Price", "Qty", "Subtotal"};
        cartTableModel = new DefaultTableModel(cartColumnNames, 0) { public boolean isCellEditable(int r, int c){return false;} };
        cartTable = new JTable(cartTableModel);
    }

    private void initMyOrdersDialog() {
        myOrdersDialog = new JDialog(this, "My Order History", true);
        myOrdersDialog.setSize(800, 600);
        myOrdersDialog.setLocationRelativeTo(this);
        myOrdersDialog.setLayout(new BorderLayout(10, 10));

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel myOrdersPanel = new JPanel(new BorderLayout());
        myOrdersPanel.setBorder(BorderFactory.createTitledBorder("My Orders"));
        String[] myOrdersColumnNames = {"Order ID", "Order Date", "Total Amount", "Status", "Payment Method"};
        myOrdersTableModel = new DefaultTableModel(myOrdersColumnNames, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        myOrdersTable = new JTable(myOrdersTableModel);
        myOrdersPanel.add(new JScrollPane(myOrdersTable), BorderLayout.CENTER);

        JPanel myOrderItemsPanel = new JPanel(new BorderLayout());
        myOrderItemsPanel.setBorder(BorderFactory.createTitledBorder("Items in Selected Order"));
        String[] myOrderItemsColumnNames = {"Product Name", "Quantity", "Price at Purchase"};
        myOrderItemsTableModel = new DefaultTableModel(myOrderItemsColumnNames, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        myOrderItemsTable = new JTable(myOrderItemsTableModel);
        myOrderItemsPanel.add(new JScrollPane(myOrderItemsTable), BorderLayout.CENTER);

        splitPane.setTopComponent(myOrdersPanel);
        splitPane.setBottomComponent(myOrderItemsPanel);

        myOrdersDialog.add(splitPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Close");
        styleGenericButton(closeButton, new Color(149, 165, 166), new Color(127, 140, 141));
        closeButton.addActionListener(e -> myOrdersDialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        buttonPanel.add(closeButton);
        myOrdersDialog.add(buttonPanel, BorderLayout.SOUTH);

        myOrdersTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int selectedRow = myOrdersTable.getSelectedRow();
                if (selectedRow != -1) {
                    int orderId = (int) myOrdersTableModel.getValueAt(selectedRow, 0);
                    displayMyOrderItems(orderId);
                }
            }
        });
    }

    private void styleGenericButton(JButton button, Color bgColor, Color hoverColor) {
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        Border padding = BorderFactory.createEmptyBorder(10, 20, 10, 20);
        Border line = BorderFactory.createLineBorder(bgColor.darker(), 1);
        button.setBorder(BorderFactory.createCompoundBorder(line, padding));
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { button.setBackground(hoverColor); }
            public void mouseExited(MouseEvent e) { button.setBackground(bgColor); }
        });
    }

    private JPanel createAdminWrapperPanel() {
        JPanel adminWrapperPanel = new JPanel(new BorderLayout());
        adminWrapperPanel.add(adminDashboardPanel, BorderLayout.CENTER);

        JButton logoutButton = new JButton("Logout and Return to Welcome Screen");
        styleGenericButton(logoutButton, new Color(231, 76, 60), new Color(192, 57, 43));
        logoutButton.addActionListener(e -> showWelcomePanel());

        JPanel logoutPanelContainer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        logoutPanelContainer.add(logoutButton);
        adminWrapperPanel.add(logoutPanelContainer, BorderLayout.SOUTH);

        return adminWrapperPanel;
    }

    private void attemptAdminLogin() {
        String username = adminUsernameField.getText().trim();
        String password = new String(adminPasswordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Admin username and password are required.", true, adminLoginStatusLabel);
            return;
        }
        try {
            Admin admin = dbManager.validateAdmin(username, password);
            if (admin != null) {
                showAdminDashboardPanel();
            } else {
                setStatus("Invalid admin username or password.", true, adminLoginStatusLabel);
            }
        } catch (SQLException ex) {
            handleDatabaseError("Admin Login Error: ", ex, adminLoginStatusLabel);
        }
    }

    private void attemptCustomerLogin() {
        String username = customerLoginUsernameField.getText().trim();
        String password = new String(customerLoginPasswordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            setStatus("Customer username and password are required.", true, customerLoginStatusLabel);
            return;
        }
        try {
            CustomerAccount account = dbManager.validateCustomerAccount(username, password);
            if (account != null) {
                loggedInCustomerAccount = account;
                loggedInCustomerProfile = dbManager.getCustomerById(account.getCustomerId());
                if (loggedInCustomerProfile != null) {
                    showShopPanel();
                } else {
                    setStatus("Login successful, but failed to load customer profile.", true, customerLoginStatusLabel);
                }
            } else {
                setStatus("Invalid customer username or password.", true, customerLoginStatusLabel);
            }
        } catch (SQLException ex) {
            handleDatabaseError("Customer Login Error: ", ex, customerLoginStatusLabel);
        }
    }

    private void addProduct() {
        if (!validateProductFields()) return;
        try {
            String name = productNameField.getText().trim();
            String desc = productDescriptionArea.getText().trim();
            BigDecimal price = new BigDecimal(productPriceField.getText().trim());
            int stock = Integer.parseInt(productStockField.getText().trim());
            String imageUrl = productImageUrlField.getText().trim();

            Product product = new Product(0, name, desc, price, stock, imageUrl.isEmpty() ? null : imageUrl);
            if (dbManager.insertProduct(product) != -1) {
                setStatus("Product '" + name + "' added successfully!", false, productStatusLabel);
                clearProductFields();
                refreshProductTable();
                displayProductsInShop();
            } else {
                setStatus("Failed to add product.", true, productStatusLabel);
            }
        } catch (SQLException ex) {
            handleDatabaseError("Error adding product: ", ex, productStatusLabel);
        }
    }

    private void updateProduct() {
        if (!validateProductFields()) return;
        try {
            int id = Integer.parseInt(productIdField.getText().trim());
            String name = productNameField.getText().trim();
            String desc = productDescriptionArea.getText().trim();
            BigDecimal price = new BigDecimal(productPriceField.getText().trim());
            int stock = Integer.parseInt(productStockField.getText().trim());
            String imageUrl = productImageUrlField.getText().trim();

            Product product = new Product(id, name, desc, price, stock, imageUrl.isEmpty() ? null : imageUrl);
            if (dbManager.updateProduct(product)) {
                setStatus("Product ID " + id + " updated successfully!", false, productStatusLabel);
                clearProductFields();
                refreshProductTable();
                displayProductsInShop();
            } else {
                setStatus("Failed to update product ID " + id + ". Not found?", true, productStatusLabel);
            }
        } catch (SQLException ex) {
            handleDatabaseError("Error updating product: ", ex, productStatusLabel);
        }
    }

    private boolean validateProductFields() {
        String name = productNameField.getText().trim();
        String priceStr = productPriceField.getText().trim();
        String stockStr = productStockField.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || stockStr.isEmpty()) {
            setStatus("Name, Price, and Stock cannot be empty.", true, productStatusLabel);
            return false;
        }

        try {
            BigDecimal price = new BigDecimal(priceStr);
            if (price.compareTo(BigDecimal.ZERO) < 0) {
                setStatus("Price cannot be negative.", true, productStatusLabel);
                return false;
            }
        } catch (NumberFormatException e) {
            setStatus("Invalid format for Price. Please enter a number.", true, productStatusLabel);
            return false;
        }

        try {
            int stock = Integer.parseInt(stockStr);
            if (stock < 0) {
                setStatus("Stock cannot be negative.", true, productStatusLabel);
                return false;
            }
        } catch (NumberFormatException e) {
            setStatus("Invalid format for Stock. Please enter a whole number.", true, productStatusLabel);
            return false;
        }

        return true;
    }

    private void deleteProduct() {
        try {
            int id = Integer.parseInt(productIdField.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete Product ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (dbManager.deleteProduct(id)) {
                setStatus("Product ID " + id + " deleted successfully!", false, productStatusLabel);
                clearProductFields();
                refreshProductTable();
                displayProductsInShop();
            } else {
                setStatus("Failed to delete product ID " + id + ". Not found or in use.", true, productStatusLabel);
            }
        } catch (NumberFormatException e) {
            setStatus("Error: Invalid ID format.", true, productStatusLabel);
        } catch (SQLException ex) {
            handleDatabaseError("Error deleting product: ", ex, productStatusLabel);
        }
    }

    private void refreshProductTable() {
        productTableModel.setRowCount(0);
        try {
            List<Product> products = dbManager.getAllProducts();
            for (Product p : products) {
                productTableModel.addRow(new Object[]{ p.getProductId(), p.getName(), p.getDescription(), p.getPrice(), p.getStockQuantity(), p.getImageUrl() });
            }
            setStatus(products.size() + " products loaded.", false, productStatusLabel);
        } catch (SQLException ex) {
            handleDatabaseError("Error refreshing product table: ", ex, productStatusLabel);
        }
    }

    private void clearProductFields() {
        productIdField.setText("");
        productNameField.setText("");
        productDescriptionArea.setText("");
        productPriceField.setText("");
        productStockField.setText("");
        productImageUrlField.setText("");
        setStatus("Fields cleared.", false, productStatusLabel);
    }

    private void addCustomer() {
        if (!validateCustomerFields()) return;
        try {
            String name = customerNameField.getText().trim();
            String email = customerEmailField.getText().trim();
            String phone = customerPhoneField.getText().trim();
            String address = customerAddressArea.getText().trim();

            Customer customer = new Customer(0, name, email, phone, address);
            if (dbManager.insertCustomer(customer) != -1) {
                setStatus("Customer '" + name + "' added successfully!", false, customerStatusLabel);
                clearCustomerFields();
                refreshCustomerTable();
            } else {
                setStatus("Failed to add customer.", true, customerStatusLabel);
            }
        } catch (SQLException ex) {
            handleDatabaseError("Error adding customer: ", ex, customerStatusLabel);
        }
    }

    private boolean validateCustomerFields() {
        String name = customerNameField.getText().trim();
        String email = customerEmailField.getText().trim();
        String phone = customerPhoneField.getText().trim();

        if (name.isEmpty() || email.isEmpty()) {
            setStatus("Name and Email cannot be empty.", true, customerStatusLabel);
            return false;
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            setStatus("Name must contain only letters and spaces.", true, customerStatusLabel);
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            setStatus("Invalid email format.", true, customerStatusLabel);
            return false;
        }
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            setStatus("Invalid phone format. Use only digits and an optional leading '+'.", true, customerStatusLabel);
            return false;
        }
        return true;
    }

    private void deleteCustomer() {
        try {
            int id = Integer.parseInt(customerIdField.getText().trim());
            int confirm = JOptionPane.showConfirmDialog(this, "Delete Customer ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) return;

            if (dbManager.deleteCustomer(id)) {
                setStatus("Customer ID " + id + " deleted.", false, customerStatusLabel);
                clearCustomerFields();
                refreshCustomerTable();
            } else {
                setStatus("Failed to delete customer ID " + id + ". Not found or has orders.", true, customerStatusLabel);
            }
        } catch (NumberFormatException e) {
            setStatus("Error: Invalid ID format.", true, customerStatusLabel);
        } catch (SQLException ex) {
            handleDatabaseError("Error deleting customer: ", ex, customerStatusLabel);
        }
    }

    private void refreshCustomerTable() {
        customerTableModel.setRowCount(0);
        try {
            List<Customer> customers = dbManager.getAllCustomers();
            for (Customer c : customers) {
                customerTableModel.addRow(new Object[]{ c.getCustomerId(), c.getName(), c.getEmail(), c.getPhoneNumber(), c.getAddress() });
            }
            setStatus(customers.size() + " customers loaded.", false, customerStatusLabel);
        } catch (SQLException ex) {
            handleDatabaseError("Error refreshing customer table: ", ex, customerStatusLabel);
        }
    }

    private void clearCustomerFields() {
        customerIdField.setText(""); customerNameField.setText(""); customerEmailField.setText("");
        customerPhoneField.setText(""); customerAddressArea.setText("");
        setStatus("Fields cleared.", false, customerStatusLabel);
    }

    private void refreshOrderTable() {
        orderTableModel.setRowCount(0);
        orderItemsTableModel.setRowCount(0);
        try {
            cachedOrders = dbManager.getAllOrders();
            for (Order o : cachedOrders) {
                orderTableModel.addRow(new Object[]{ o.getOrderId(), o.getCustomerId(), o.getOrderDate(), o.getTotalAmount(), o.getStatus(), o.getPaymentMethod() });
            }
            setStatus(cachedOrders.size() + " orders loaded.", false, orderStatusLabel);
        } catch (SQLException ex) {
            handleDatabaseError("Error refreshing order table: ", ex, orderStatusLabel);
        }
    }

    private void displayOrderItems(int orderId) {
        orderItemsTableModel.setRowCount(0);
        cachedOrders.stream().filter(o -> o.getOrderId() == orderId).findFirst().ifPresent(order -> {
            order.getOrderItems().forEach(item -> orderItemsTableModel.addRow(new Object[]{
                    item.getOrderItemId(), item.getProductId(), item.getProductName(), item.getQuantity(), item.getPriceAtPurchase()
            }));
            setStatus("Displaying items for Order ID: " + orderId, false, orderStatusLabel);
        });
    }

    private void updateOrder() {
        try {
            int orderId = Integer.parseInt(orderIdField.getText().trim());
            String newStatus = orderStatusTextField.getText().trim();
            if (newStatus.isEmpty() || (!newStatus.equalsIgnoreCase("Pending") && !newStatus.equalsIgnoreCase("Completed") && !newStatus.equalsIgnoreCase("Cancelled"))) {
                setStatus("Invalid status. Use 'Pending', 'Completed', or 'Cancelled'.", true, orderStatusLabel);
                return;
            }
            if (dbManager.updateOrderStatus(orderId, newStatus)) {
                setStatus("Order " + orderId + " status updated!", false, orderStatusLabel);
                refreshOrderTable();
                orderIdField.setText(""); orderStatusTextField.setText("");
            } else {
                setStatus("Failed to update status for Order " + orderId, true, orderStatusLabel);
            }
        } catch (NumberFormatException e) {
            setStatus("Error: Invalid Order ID.", true, orderStatusLabel);
        } catch (SQLException ex) {
            handleDatabaseError("Error updating order status: ", ex, orderStatusLabel);
        }
    }

    private void showCustomerRegistrationDialog() {
        if (registrationDialog == null) {
            registrationDialog = new JDialog(this, "New Customer Registration", true);
            registrationDialog.setSize(450, 450);
            registrationDialog.setLocationRelativeTo(this);
            registrationDialog.setLayout(new BorderLayout(10, 10));
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            JPanel formPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1.0;
            gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Full Name:"), gbc);
            gbc.gridx = 1; regNameField = new JTextField(20); formPanel.add(regNameField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; formPanel.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; regEmailField = new JTextField(20); formPanel.add(regEmailField, gbc);
            gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Phone Number:"), gbc);
            gbc.gridx = 1; regPhoneField = new JTextField(20); formPanel.add(regPhoneField, gbc);
            gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Address:"), gbc);
            gbc.gridx = 1; regAddressArea = new JTextArea(3, 20); formPanel.add(new JScrollPane(regAddressArea), gbc);
            gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Username:"), gbc);
            gbc.gridx = 1; regUsernameField = new JTextField(20); formPanel.add(regUsernameField, gbc);
            gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Password:"), gbc);
            gbc.gridx = 1; regPasswordField = new JPasswordField(20); formPanel.add(regPasswordField, gbc);
            regStatusLabel = new JLabel(" ", SwingConstants.CENTER);
            gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; formPanel.add(regStatusLabel, gbc);
            contentPanel.add(formPanel, BorderLayout.CENTER);
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton registerButton = new JButton("Register"); styleGenericButton(registerButton, new Color(46, 204, 113), new Color(39, 174, 96));
            registerButton.addActionListener(e -> registerNewCustomer());
            JButton cancelButton = new JButton("Cancel"); styleGenericButton(cancelButton, new Color(149, 165, 166), new Color(127, 140, 141));
            cancelButton.addActionListener(e -> registrationDialog.dispose());
            buttonPanel.add(registerButton); buttonPanel.add(cancelButton);
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            registrationDialog.add(contentPanel);
        }
        registrationDialog.setVisible(true);
    }

    private void registerNewCustomer() {
        if (!validateRegistrationFields()) return;
        try {
            String name = regNameField.getText().trim();
            String email = regEmailField.getText().trim();
            String phone = regPhoneField.getText().trim();
            String address = regAddressArea.getText().trim();
            String username = regUsernameField.getText().trim();
            String password = new String(regPasswordField.getPassword());

            Customer newCustomer = new Customer(0, name, email, phone, address);
            int customerId = dbManager.insertCustomer(newCustomer);
            if (customerId != -1) {
                CustomerAccount newAccount = new CustomerAccount(customerId, username, password);
                if (dbManager.registerCustomerAccount(newAccount) != -1) {
                    JOptionPane.showMessageDialog(registrationDialog, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    registrationDialog.dispose();
                    customerLoginUsernameField.setText(username);
                    customerLoginPasswordField.setText(password);
                    attemptCustomerLogin();
                } else { setStatus("Failed to create account. Username might be taken.", true, regStatusLabel); }
            } else { setStatus("Failed to create profile. Email might be in use.", true, regStatusLabel); }
        } catch (SQLException ex) { handleDatabaseError("Registration Error: ", ex, regStatusLabel); }
    }

    private boolean validateRegistrationFields() {
        String name = regNameField.getText().trim();
        String email = regEmailField.getText().trim();
        String phone = regPhoneField.getText().trim();
        String username = regUsernameField.getText().trim();
        String password = new String(regPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            setStatus("All fields are required.", true, regStatusLabel);
            return false;
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            setStatus("Name must contain only letters and spaces.", true, regStatusLabel);
            return false;
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            setStatus("Invalid email format.", true, regStatusLabel);
            return false;
        }
        if (!phone.isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            setStatus("Invalid phone format. Use only digits and an optional leading '+'.", true, regStatusLabel);
            return false;
        }
        try {
            if (dbManager.usernameExists(username)) {
                setStatus("Username already exists. Please choose another.", true, regStatusLabel);
                return false;
            }
        } catch (SQLException e) {
            handleDatabaseError("Error checking username: ", e, regStatusLabel);
            return false;
        }
        return true;
    }

    private void checkoutCart() {
        if (currentCart.isEmpty()) {
            setStatus("Cart is empty!", true, shopStatusLabel);
            return;
        }

        if (loggedInCustomerProfile == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to proceed to checkout.", "Login Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Customer customerForBill = loggedInCustomerProfile;
        // MODIFIED LINE: Removed "PayPal" and "Credit/Debit Card" from the options
        String[] paymentOptions = {"Cash on Delivery"};
        int choice = JOptionPane.showOptionDialog(this, "Select a payment method:", "Payment", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, paymentOptions, paymentOptions[0]);

        if (choice == JOptionPane.CLOSED_OPTION) {
            setStatus("Checkout cancelled.", false, shopStatusLabel);
            return;
        }

        String paymentMethod = paymentOptions[choice];
        try {
            int orderId = dbManager.createOrder(customerForBill.getCustomerId(), currentCart.getItems(), currentCart.getTotal(), paymentMethod);
            if(orderId != -1) {
                Order newOrder = dbManager.getOrderById(orderId);
                String pdfPath = PdfGenerator.generateBill(newOrder, customerForBill);
                JOptionPane.showMessageDialog(this, "Checkout complete! Bill saved at: " + pdfPath, "Success", JOptionPane.INFORMATION_MESSAGE);
                currentCart.clear();
                displayProductsInShop();
                refreshOrderTable();
            } else {
                setStatus("Failed to create order.", true, shopStatusLabel);
            }
        } catch (SQLException ex) {
            handleDatabaseError("Checkout error:", ex, shopStatusLabel);
        }
    }

    private void showCartDialog() {
        if (cartDialog == null) {
            cartDialog = new JDialog(this, "Shopping Cart", true);
            cartDialog.setSize(600, 450);
            cartDialog.setLocationRelativeTo(this);
            cartDialog.setLayout(new BorderLayout(10, 10));

            JScrollPane cartScrollPane = new JScrollPane(cartTable);
            cartDialog.add(cartScrollPane, BorderLayout.CENTER);

            JPanel controlPanel = new JPanel(new GridBagLayout());
            controlPanel.setBorder(BorderFactory.createTitledBorder("Manage Cart Item"));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5,5,5,5); gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0; controlPanel.add(new JLabel("Selected ID:"), gbc);
            gbc.gridx = 1; cartItemIdField = new JTextField(5); cartItemIdField.setEditable(false); controlPanel.add(cartItemIdField, gbc);
            gbc.gridx = 0; gbc.gridy = 1; controlPanel.add(new JLabel("New Quantity:"), gbc);
            gbc.gridx = 1; cartItemQuantityField = new JTextField(5); controlPanel.add(cartItemQuantityField, gbc);

            JButton updateCartItemButton = new JButton("Update Quantity"); styleGenericButton(updateCartItemButton, new Color(52, 152, 219), new Color(41, 128, 185));
            updateCartItemButton.addActionListener(e -> updateCartItem());
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 1; controlPanel.add(updateCartItemButton, gbc);

            JButton removeCartItemButton = new JButton("Remove Item"); styleGenericButton(removeCartItemButton, new Color(231, 76, 60), new Color(192, 57, 43));
            removeCartItemButton.addActionListener(e -> removeCartItem());
            gbc.gridx = 1; controlPanel.add(removeCartItemButton, gbc);

            JPanel footer = new JPanel(new BorderLayout(10, 10));
            footer.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
            cartTotalLabel = new JLabel("Total: $0.00", SwingConstants.RIGHT);
            cartTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            footer.add(controlPanel, BorderLayout.NORTH);
            footer.add(cartTotalLabel, BorderLayout.CENTER);

            JButton closeCartButton = new JButton("Close"); styleGenericButton(closeCartButton, new Color(149, 165, 166), new Color(127, 140, 141));
            closeCartButton.addActionListener(e -> cartDialog.dispose());
            JPanel closePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            closePanel.add(closeCartButton);
            footer.add(closePanel, BorderLayout.SOUTH);

            cartDialog.add(footer, BorderLayout.SOUTH);

            cartTable.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    int row = cartTable.getSelectedRow();
                    if (row != -1) {
                        cartItemIdField.setText(cartTable.getValueAt(row, 0).toString());
                        cartItemQuantityField.setText(cartTable.getValueAt(row, 3).toString());
                    }
                }
            });
        }
        refreshCartTable();
        cartDialog.setVisible(true);
    }

    private void updateCartItem() {
        if (cartItemIdField.getText().isEmpty() || cartItemQuantityField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(cartDialog, "Please select an item and enter a quantity.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int productId = Integer.parseInt(cartItemIdField.getText());
            int newQuantity = Integer.parseInt(cartItemQuantityField.getText());

            if (newQuantity < 0) {
                JOptionPane.showMessageDialog(cartDialog, "Quantity cannot be negative.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Product p = dbManager.getProductById(productId);
            if (newQuantity > p.getStockQuantity()) {
                JOptionPane.showMessageDialog(cartDialog, "Not enough stock. Available: " + p.getStockQuantity(), "Stock Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (newQuantity == 0) {
                currentCart.removeItem(productId);
            } else {
                currentCart.setItemQuantity(productId, newQuantity);
            }
            refreshCartTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(cartDialog, "Invalid quantity.", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            handleDatabaseError("Error checking stock:", ex, shopStatusLabel);
        }
    }

    private void removeCartItem() {
        if (cartItemIdField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(cartDialog, "Please select an item to remove.", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int productId = Integer.parseInt(cartItemIdField.getText());
        currentCart.removeItem(productId);
        refreshCartTable();
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        for (Map.Entry<Integer, Integer> entry : currentCart.getItems().entrySet()) {
            Product p = currentCart.getProductDetails(entry.getKey());
            if (p != null) {
                BigDecimal subtotal = p.getPrice().multiply(new BigDecimal(entry.getValue()));
                cartTableModel.addRow(new Object[]{p.getProductId(), p.getName(), p.getPrice(), entry.getValue(), subtotal.setScale(2, BigDecimal.ROUND_HALF_UP)});
            }
        }
        cartTotalLabel.setText("Total: $" + currentCart.getTotal().setScale(2, BigDecimal.ROUND_HALF_UP));
        cartItemIdField.setText("");
        cartItemQuantityField.setText("");
    }

    private void displayProductsInShop() {
        productDisplayPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        try {
            List<Product> products = dbManager.getAllProducts();
            int column = 0;
            int row = 0;
            int maxColumns = 3;

            for (Product product : products) {
                JPanel card = new JPanel(new BorderLayout(5, 5));
                // CHANGE: Adjusted card size as requested
                card.setPreferredSize(new Dimension(400, 325));
                card.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));

                JLabel imageLabel = new JLabel();
                imageLabel.setPreferredSize(new Dimension(250, 200));
                imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
                if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                    new SwingWorker<ImageIcon, Void>() {
                        @Override
                        protected ImageIcon doInBackground() {
                            try {
                                URL url = new URL(product.getImageUrl());
                                BufferedImage img = ImageIO.read(url);
                                if (img != null) {
                                    return new ImageIcon(img.getScaledInstance(180, 180, Image.SCALE_SMOOTH));
                                }
                            } catch (Exception ex) {
                                System.err.println("Failed to load image: " + product.getImageUrl());
                            }
                            return null;
                        }
                        @Override
                        protected void done() {
                            try {
                                ImageIcon icon = get();
                                if (icon != null) {
                                    imageLabel.setIcon(icon);
                                } else {
                                    imageLabel.setText("No Image");
                                }
                            } catch (Exception ex) {
                                imageLabel.setText("Image Error");
                            }
                        }
                    }.execute();
                } else {
                    imageLabel.setText("No Image Available");
                }
                card.add(imageLabel, BorderLayout.NORTH);

                // FIX: Using BoxLayout for reliable vertical stacking of product info
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

                JLabel nameLabel = new JLabel("<html><b>" + product.getName() + "</b></html>");
                JLabel priceLabel = new JLabel("Price: $" + product.getPrice());
                JLabel stockLabel = new JLabel("Stock: " + product.getStockQuantity());
                if(product.getStockQuantity() == 0) stockLabel.setForeground(Color.RED);

                infoPanel.add(nameLabel);
                infoPanel.add(priceLabel);
                infoPanel.add(stockLabel);
                card.add(infoPanel, BorderLayout.CENTER);

                JPanel cartActionsPanel = new JPanel();
                JTextField quantityField = new JTextField("1", 3);
                JButton addToCartButton = new JButton("Add to Cart");
                styleGenericButton(addToCartButton, new Color(52, 152, 219), new Color(41, 128, 185));
                if(product.getStockQuantity() == 0) addToCartButton.setEnabled(false);
                addToCartButton.addActionListener(e -> {
                    try {
                        int quantity = Integer.parseInt(quantityField.getText());
                        if(quantity > 0 && quantity <= product.getStockQuantity()) {
                            currentCart.addItem(product, quantity);
                            setStatus(quantity + "x " + product.getName() + " added to cart.", false, shopStatusLabel);
                        } else {
                            setStatus("Invalid quantity or not enough stock.", true, shopStatusLabel);
                        }
                    } catch (NumberFormatException ex) {
                        setStatus("Please enter a valid number.", true, shopStatusLabel);
                    }
                });
                cartActionsPanel.add(new JLabel("Qty:"));
                cartActionsPanel.add(quantityField);
                cartActionsPanel.add(addToCartButton);
                card.add(cartActionsPanel, BorderLayout.SOUTH);

                gbc.gridx = column; gbc.gridy = row;
                productDisplayPanel.add(card, gbc);

                column++;
                if (column >= maxColumns) { column = 0; row++; }
            }
        } catch (SQLException e) {
            handleDatabaseError("Error loading products:", e, shopStatusLabel);
        }
        productDisplayPanel.revalidate();
        productDisplayPanel.repaint();
    }

    private void showMyOrdersDialog() {
        if (loggedInCustomerProfile == null) {
            JOptionPane.showMessageDialog(this, "You must be logged in to view your orders.", "Login Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        myOrdersTableModel.setRowCount(0);
        myOrderItemsTableModel.setRowCount(0);

        try {
            List<Order> customerOrders = dbManager.getOrdersByCustomerId(loggedInCustomerProfile.getCustomerId());
            if (customerOrders.isEmpty()) {
                myOrdersTableModel.addRow(new Object[]{"No orders found.", "", "", "", ""});
            } else {
                for (Order order : customerOrders) {
                    myOrdersTableModel.addRow(new Object[]{
                            order.getOrderId(),
                            order.getOrderDate(),
                            order.getTotalAmount(),
                            order.getStatus(),
                            order.getPaymentMethod()
                    });
                }
            }
        } catch (SQLException ex) {
            handleDatabaseError("Error fetching your orders: ", ex, new JLabel());
        }

        myOrdersDialog.setVisible(true);
    }

    private void displayMyOrderItems(int orderId) {
        myOrderItemsTableModel.setRowCount(0);
        try {
            Order selectedOrder = dbManager.getOrderById(orderId);
            if (selectedOrder != null) {
                for (OrderItem item : selectedOrder.getOrderItems()) {
                    myOrderItemsTableModel.addRow(new Object[]{
                            item.getProductName(),
                            item.getQuantity(),
                            item.getPriceAtPurchase()
                    });
                }
            }
        } catch (SQLException ex) {
            handleDatabaseError("Error fetching details for order " + orderId, ex, new JLabel());
        }
    }

    private void showWelcomePanel() {
        cardLayout.show(mainPanel, "Welcome");
        clearUserSession();
    }

    private void showLoginChoicePanel() { cardLayout.show(mainPanel, "LoginChoice"); }

    private void showAdminLoginPanel() { cardLayout.show(mainPanel, "AdminLogin"); }

    private void showCustomerLoginPanel() { cardLayout.show(mainPanel, "CustomerLogin"); }

    private void showAdminDashboardPanel() {
        cardLayout.show(mainPanel, "AdminDashboard");
        refreshAdminTables();
    }

    private void showShopPanel() {
        cardLayout.show(mainPanel, "Shop");
        displayProductsInShop();
        if (loggedInCustomerProfile != null) {
            customerIdForShopLabel.setVisible(true);
            customerIdForShopField.setVisible(true);
            customerIdForShopField.setText(String.valueOf(loggedInCustomerProfile.getCustomerId()));
            customerIdForShopField.setEditable(false);
            shopStatusLabel.setText("Welcome, " + loggedInCustomerProfile.getName() + "!");
            viewMyOrdersButton.setVisible(true);
        } else {
            customerIdForShopLabel.setVisible(false);
            customerIdForShopField.setVisible(false);
            shopStatusLabel.setText("Welcome Guest! Please log in or register to place an order.");
            viewMyOrdersButton.setVisible(false);
        }
    }

    private void clearUserSession() {
        loggedInCustomerAccount = null;
        loggedInCustomerProfile = null;
        currentCart.clear();
        clearLoginFields();
    }

    private void clearLoginFields() {
        adminUsernameField.setText(""); adminPasswordField.setText("");
        customerLoginUsernameField.setText(""); customerLoginPasswordField.setText("");
        if(adminLoginStatusLabel != null) adminLoginStatusLabel.setText(" ");
        if(customerLoginStatusLabel != null) customerLoginStatusLabel.setText(" ");
    }

    private void refreshAdminTables() {
        refreshProductTable();
        refreshCustomerTable();
        refreshOrderTable();
    }

    private void setStatus(String message, boolean isError, JLabel statusLabel) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            statusLabel.setForeground(isError ? Color.RED : new Color(0, 100, 0));
        }
    }

    private void handleDatabaseError(String contextMessage, SQLException ex, JLabel statusLabel) {
        System.err.println("DATABASE ERROR: " + contextMessage + ex.getMessage());
        ex.printStackTrace();
        setStatus(contextMessage + "See console for details.", true, statusLabel);
        JOptionPane.showMessageDialog(this, contextMessage + "\n" + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}
