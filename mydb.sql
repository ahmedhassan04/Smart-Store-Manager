-- Drop the database if it exists to start fresh.
DROP DATABASE IF EXISTS `mydb`;

-- Create the database and select it for use.
CREATE DATABASE `mydb`;
USE `mydb`;

-- -----------------------------------------------------
-- Table `admins` 
-- -----------------------------------------------------
CREATE TABLE `admins` (
  `admin_id` INT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL, -- In a real application, this should be a hash.
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `customers`
-- -----------------------------------------------------
CREATE TABLE `customers` (
  `customer_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL UNIQUE,
  `phone_number` VARCHAR(20) NULL,
  `address` TEXT NULL,
  PRIMARY KEY (`customer_id`)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `customer_accounts` 
-- -----------------------------------------------------
CREATE TABLE `customer_accounts` (
  `account_id` INT NOT NULL AUTO_INCREMENT,
  `customer_id` INT NOT NULL UNIQUE,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`account_id`),
  CONSTRAINT `fk_customer_accounts_customers`
    FOREIGN KEY (`customer_id`)
    REFERENCES `customers` (`customer_id`)
    ON DELETE CASCADE
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `products` 
-- -----------------------------------------------------
CREATE TABLE `products` (
  `product_id` INT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` TEXT NULL,
  `price` DECIMAL(10, 2) NOT NULL,
  `stock_quantity` INT NOT NULL DEFAULT 0,
  `image_url` VARCHAR(2048) NULL,
  PRIMARY KEY (`product_id`),
  CONSTRAINT `chk_product_price` CHECK (`price` >= 0),
  CONSTRAINT `chk_product_stock` CHECK (`stock_quantity` >= 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `orders` 
-- -----------------------------------------------------
CREATE TABLE `orders` (
  `order_id` INT NOT NULL AUTO_INCREMENT,
  `customer_id` INT NOT NULL,
  `order_date` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `total_amount` DECIMAL(10, 2) NOT NULL,
  `status` ENUM('Pending', 'Completed', 'Cancelled') NOT NULL DEFAULT 'Pending',
  `payment_method` VARCHAR(50) NULL,
  PRIMARY KEY (`order_id`),
  CONSTRAINT `fk_orders_customers`
    FOREIGN KEY (`customer_id`)
    REFERENCES `customers` (`customer_id`)
    ON DELETE RESTRICT
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Table `order_items`
-- -----------------------------------------------------
CREATE TABLE `order_items` (
  `order_item_id` INT NOT NULL AUTO_INCREMENT,
  `order_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL,
  `price_at_purchase` DECIMAL(10, 2) NOT NULL,
  PRIMARY KEY (`order_item_id`),
  CONSTRAINT `fk_order_items_orders`
    FOREIGN KEY (`order_id`)
    REFERENCES `orders` (`order_id`)
    ON DELETE CASCADE,
  CONSTRAINT `fk_order_items_products`
    FOREIGN KEY (`product_id`)
    REFERENCES `products` (`product_id`)
    ON DELETE RESTRICT,
  CONSTRAINT `chk_order_item_quantity` CHECK (`quantity` > 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Sample Data Insertion
-- -----------------------------------------------------
INSERT INTO `admins` (`username`, `password`) VALUES ('admin', 'admin123');

INSERT INTO `products` (`name`, `description`, `price`, `stock_quantity`, `image_url`) VALUES
('Laptop Pro 15', 'A powerful laptop for professionals with a 15-inch screen and 16GB RAM.', 1299.99, 50, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTDwOC6_XLG0gr4PCKqb1CzYd1SzD6yb39CkA&s'),
('Wireless Mouse', 'Ergonomic wireless mouse with a long-lasting battery.', 25.50, 200, 'https://img.pikbest.com/backgrounds/20241129/wireless-gaming-mouse-for-pc-transparent-background-image_11145605.jpg!sw800'),
('Mechanical Keyboard', 'RGB backlit mechanical keyboard with tactile switches.', 89.99, 120, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS0-CcCafy-i2xenaniSP3VExk_BRwCSnEXBA&s'),
('4K Monitor 27"', 'A 27-inch 4K UHD monitor with vibrant colors and crisp details.', 349.00, 75, 'https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT14DQNyNLKz8MU2uSJ8RluTD-NF0xq-Nmkkg&s'),
('USB-C Hub', 'A 7-in-1 USB-C hub with HDMI, USB 3.0, and SD card reader.', 39.99, 150, 'https://plugable.com/cdn/shop/files/main_ori_0a84a6f9-0f78-48ba-9222-977955e1f3d4.jpg?v=1718235273');

INSERT INTO `customers` (`name`, `email`, `phone_number`, `address`) VALUES
('John Doe', 'john.doe@example.com', '123-456-7890', '123 Main St, Anytown, USA'),
('Jane Smith', 'jane.smith@example.com', '987-654-3210', '456 Oak Ave, Somecity, USA');

INSERT INTO `customer_accounts` (`customer_id`, `username`, `password`) VALUES
(1, 'johndoe', 'password123'),
(2, 'janesmith', '123');

INSERT INTO `orders` (`customer_id`, `total_amount`, `status`, `payment_method`) VALUES
(1, 1325.49, 'Completed', 'Cash On Delivery');

INSERT INTO `order_items` (`order_id`, `product_id`, `quantity`, `price_at_purchase`) VALUES
(1, 1, 1, 1299.99),
(1, 2, 1, 25.50);
