// Main.java - Entry point of the GUI application
import javax.swing.SwingUtilities;
import javax.swing.JOptionPane;
import java.sql.SQLException;

/**
 * Main class to launch the Store Management System GUI application.
 * It initializes the database manager and ensures the GUI is created and updated
 * on the Event Dispatch Thread (EDT), which is essential for Swing applications.
 * This class also manages the single database connection for the entire application lifecycle.
 */
public class Main {

    public static void main(String[] args) {
        // Database connection details
        // IMPORTANT: Replace 'your_username' and 'your_password' with your actual MySQL credentials.
        // For example, if you set 'root' with password 'root123', use those values.
        String url = "jdbc:mysql://localhost:3306/mydb";
        String username = "root"; // e.g., "root" or "app_user"
        String password = "root"; // e.g., "root123" or "app_password"

        // Use SwingUtilities.invokeLater to ensure the GUI is created and updated on the Event Dispatch Thread (EDT).
        SwingUtilities.invokeLater(() -> {
            DatabaseManager dbManager = null;
            try {
                // Initialize DatabaseManager
                dbManager = new DatabaseManager(url, username, password);
                // Establish database connection once at application startup
                dbManager.connect();
                System.out.println("Database connection established for Store Management App.");

                // Create and show the main Store Management Application GUI
                StoreManagementApp app = new StoreManagementApp(dbManager);
                app.setVisible(true);

                // Add a window listener to close the database connection when the application exits
                final DatabaseManager finalDbManager = dbManager; // Make dbManager effectively final for lambda
                app.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        if (finalDbManager != null) {
                            finalDbManager.close();
                            System.out.println("Database connection closed gracefully.");
                        }
                        System.exit(0); // Ensure the application exits completely
                    }
                });

            } catch (SQLException e) {
                // Display a critical error if connection fails at startup
                JOptionPane.showMessageDialog(null,
                        "Failed to connect to the database. Please check your MySQL server and credentials.\n" + e.getMessage(),
                        "Database Connection Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                // If connection failed at start, no need to call close on dbManager
                System.exit(1); // Exit application if database connection cannot be established
            } catch (Exception e) {
                // Catch any other unexpected exceptions during application startup
                JOptionPane.showMessageDialog(null,
                        "An unexpected error occurred during application startup:\n" + e.getMessage(),
                        "Application Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                // Ensure dbManager is closed if it was initialized but some other error occurred
                if (dbManager != null) {
                    dbManager.close();
                }
                System.exit(1);
            }
        });
    }
}
