// Cart.java - Helper class for shopping cart logic
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Collections; // For unmodifiable map

/**
 * Cart class manages the items a customer wants to purchase in the current session.
 * It stores Product objects along with the quantity desired.
 */
class Cart {
    // Map to store product IDs and their quantities in the cart: Product ID -> Quantity
    private Map<Integer, Integer> items;
    // Map to store product details for quick access without repeated DB lookups: Product ID -> Product object
    private Map<Integer, Product> productDetails;

    public Cart() {
        this.items = new HashMap<>();
        this.productDetails = new HashMap<>();
    }

    /**
     * Adds a product to the cart or updates its quantity if already present.
     * @param product The product to add.
     * @param quantityToAdd The quantity to add to the current cart quantity.
     */
    public void addItem(Product product, int quantityToAdd) {
        if (product == null || quantityToAdd <= 0) {
            System.err.println("Cart: Cannot add null product or non-positive quantity.");
            return; // Invalid input
        }
        items.put(product.getProductId(), items.getOrDefault(product.getProductId(), 0) + quantityToAdd);
        productDetails.putIfAbsent(product.getProductId(), product); // Store product details only if not already there
    }

    /**
     * Removes a product entirely from the cart.
     * @param productId The ID of the product to remove.
     */
    public void removeItem(int productId) {
        items.remove(productId);
        productDetails.remove(productId);
    }

    /**
     * Updates the quantity of a product in the cart to a specific new quantity.
     * If new quantity is 0 or less, removes the item.
     * @param productId The ID of the product to update.
     * @param newQuantity The new desired quantity.
     */
    public void setItemQuantity(int productId, int newQuantity) {
        if (newQuantity <= 0) {
            removeItem(productId);
        } else if (items.containsKey(productId)) {
            items.put(productId, newQuantity);
        } else {
            System.err.println("Cart: Product ID " + productId + " not found in cart to update quantity.");
        }
    }

    /**
     * Clears all items from the cart.
     */
    public void clear() {
        items.clear();
        productDetails.clear();
    }

    /**
     * Gets the current items and their quantities in the cart.
     * @return An unmodifiable map of Product ID to Quantity.
     */
    public Map<Integer, Integer> getItems() {
        return Collections.unmodifiableMap(items); // Return an unmodifiable copy to prevent external modification
    }

    /**
     * Gets the Product object for a given product ID in the cart.
     * @param productId The ID of the product.
     * @return The Product object, or null if not in cart or details not loaded.
     */
    public Product getProductDetails(int productId) {
        return productDetails.get(productId);
    }

    /**
     * Calculates the total price of all items in the cart.
     * @return The total BigDecimal amount, rounded to 2 decimal places.
     */
    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Integer, Integer> entry : items.entrySet()) {
            int productId = entry.getKey();
            int quantity = entry.getValue();
            Product product = productDetails.get(productId); // Get the product details from the stored map
            if (product != null) {
                total = total.add(product.getPrice().multiply(new BigDecimal(quantity)));
            } else {
                System.err.println("Cart: Product details not found for ID " + productId + " while calculating total.");
            }
        }
        return total.setScale(2, BigDecimal.ROUND_HALF_UP); // Always round to 2 decimal places for currency
    }

    /**
     * Checks if the cart is empty.
     * @return true if empty, false otherwise.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
}
