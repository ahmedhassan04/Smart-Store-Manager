// PdfGenerator.java - Generates PDF bills for orders
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Utility class to generate a PDF bill for a given order and customer.
 * This uses the iTextPdf library (version 5.x).
 */
public class PdfGenerator {

    // --- FIX: Store Information moved to constants for easy editing ---
    private static final String STORE_NAME = "ShelfWare";
    private static final String STORE_ADDRESS = "123 Main Street, Anytown, USA";
    private static final String STORE_CONTACT = "Phone: (123) 456-7890 | Email: info@shelfware.com";

    // --- Fonts for consistent styling ---
    private static final Font FONT_TITLE = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, BaseColor.DARK_GRAY);
    private static final Font FONT_HEADING = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
    private static final Font FONT_NORMAL = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);
    private static final Font FONT_BOLD = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static final Font FONT_SMALL = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.GRAY);
    private static final Font FONT_TOTAL = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK);

    /**
     * Generates a PDF bill for a specific order and customer.
     * The PDF will be saved to a 'bills' subdirectory.
     *
     * @param order The Order object containing all order details.
     * @param customer The Customer object associated with the order.
     * @return The absolute path to the generated PDF file, or null if an error occurs.
     */
    public static String generateBill(Order order, Customer customer) {
        File billsDir = new File("bills");
        if (!billsDir.exists()) {
            billsDir.mkdirs();
        }

        String fileName = "bill_order_" + order.getOrderId() + "_" +
                new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".pdf";
        String filePath = billsDir.getAbsolutePath() + File.separator + fileName;

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();

            addStoreHeader(document);
            document.add(Chunk.NEWLINE);

            addBillDetails(document, order);
            document.add(Chunk.NEWLINE);

            addCustomerDetails(document, customer);
            document.add(Chunk.NEWLINE);

            addOrderItemsTable(document, order.getOrderItems());
            document.add(Chunk.NEWLINE);

            addTotalAmount(document, order.getTotalAmount());
            document.add(Chunk.NEWLINE);

            addFooter(document);

            document.close();

            System.out.println("PDF Bill generated successfully at: " + filePath);
            return filePath;

        } catch (DocumentException | IOException e) {
            System.err.println("Error generating PDF bill: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Adds the store header to the PDF document using the defined constants.
     */
    private static void addStoreHeader(Document document) throws DocumentException {
        Paragraph title = new Paragraph(STORE_NAME, FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        Paragraph address = new Paragraph(STORE_ADDRESS, FONT_SMALL);
        address.setAlignment(Element.ALIGN_CENTER);
        document.add(address);

        Paragraph contact = new Paragraph(STORE_CONTACT, FONT_SMALL);
        contact.setAlignment(Element.ALIGN_CENTER);
        document.add(contact);

        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.GRAY);
        ls.setPercentage(90);
        document.add(new Chunk(ls));
    }

    /**
     * Adds general bill details like Order ID, Date, and Status.
     */
    private static void addBillDetails(Document document, Order order) throws DocumentException {
        Paragraph billInfo = new Paragraph();
        billInfo.add(new Chunk("Bill/Invoice Details\n", FONT_HEADING));
        billInfo.add(new Chunk("Order ID: ", FONT_NORMAL));
        billInfo.add(new Chunk(String.valueOf(order.getOrderId()) + "\n", FONT_BOLD));
        billInfo.add(new Chunk("Order Date: ", FONT_NORMAL));
        billInfo.add(new Chunk(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getOrderDate()) + "\n", FONT_BOLD));
        billInfo.add(new Chunk("Status: ", FONT_NORMAL));
        billInfo.add(new Chunk(order.getStatus() + "\n", FONT_BOLD));
        if (order.getPaymentMethod() != null && !order.getPaymentMethod().isEmpty()) {
            billInfo.add(new Chunk("Payment Method: ", FONT_NORMAL));
            billInfo.add(new Chunk(order.getPaymentMethod() + "\n", FONT_BOLD));
        }
        document.add(billInfo);
    }

    /**
     * Adds customer details to the PDF document.
     */
    private static void addCustomerDetails(Document document, Customer customer) throws DocumentException {
        Paragraph custInfo = new Paragraph();
        custInfo.add(new Chunk("Customer Information\n", FONT_HEADING));
        custInfo.add(new Chunk("Name: ", FONT_NORMAL));
        custInfo.add(new Chunk(customer.getName() + "\n", FONT_BOLD));
        custInfo.add(new Chunk("Email: ", FONT_NORMAL));
        custInfo.add(new Chunk(customer.getEmail() + "\n", FONT_BOLD));
        if (customer.getPhoneNumber() != null && !customer.getPhoneNumber().isEmpty()) {
            custInfo.add(new Chunk("Phone: ", FONT_NORMAL));
            custInfo.add(new Chunk(customer.getPhoneNumber() + "\n", FONT_BOLD));
        }
        if (customer.getAddress() != null && !customer.getAddress().isEmpty()) {
            custInfo.add(new Chunk("Address: ", FONT_NORMAL));
            custInfo.add(new Chunk(customer.getAddress() + "\n", FONT_BOLD));
        }
        document.add(custInfo);
    }

    /**
     * Adds a table containing all ordered items.
     */
    private static void addOrderItemsTable(Document document, List<OrderItem> items) throws DocumentException {
        document.add(new Paragraph("Order Items\n", FONT_HEADING));

        PdfPTable table = new PdfPTable(5);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        float[] columnWidths = {0.8f, 3f, 1f, 1.5f, 1.5f};
        table.setWidths(columnWidths);

        addCell(table, "S.No.", FONT_HEADING, true);
        addCell(table, "Product Name", FONT_HEADING, true);
        addCell(table, "Quantity", FONT_HEADING, true);
        addCell(table, "Unit Price", FONT_HEADING, true);
        addCell(table, "Total", FONT_HEADING, true);

        int sNo = 1;
        for (OrderItem item : items) {
            BigDecimal itemTotal = item.getPriceAtPurchase().multiply(new BigDecimal(item.getQuantity()));
            addCell(table, String.valueOf(sNo++), FONT_NORMAL, false);
            addCell(table, item.getProductName(), FONT_NORMAL, false);
            addCell(table, String.valueOf(item.getQuantity()), FONT_NORMAL, false);
            addCell(table, "$" + item.getPriceAtPurchase().setScale(2, BigDecimal.ROUND_HALF_UP), FONT_NORMAL, false);
            addCell(table, "$" + itemTotal.setScale(2, BigDecimal.ROUND_HALF_UP), FONT_NORMAL, false);
        }
        document.add(table);
    }

    /**
     * Helper method to add a styled cell to a PdfPTable.
     */
    private static void addCell(PdfPTable table, String text, Font font, boolean isHeader) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5);
        if (isHeader) {
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        } else {
            if (text.startsWith("$") || isNumeric(text)) {
                cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            } else {
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            }
        }
        cell.setBorder(Rectangle.BOTTOM | Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
        cell.setBorderColor(BaseColor.GRAY);
        table.addCell(cell);
    }

    /**
     * Checks if a string is numeric.
     */
    private static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str.replace("$", "").replace(",", ""));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Adds the total amount section to the PDF document.
     */
    private static void addTotalAmount(Document document, BigDecimal totalAmount) throws DocumentException {
        Paragraph totalPara = new Paragraph("Total Amount: $" + totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP), FONT_TOTAL);
        totalPara.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalPara);
    }

    /**
     * Adds a generic footer message to the PDF document.
     */
    private static void addFooter(Document document) throws DocumentException {
        document.add(Chunk.NEWLINE);
        LineSeparator ls = new LineSeparator();
        ls.setLineColor(BaseColor.GRAY);
        ls.setPercentage(90);
        document.add(new Chunk(ls));

        Paragraph thankYou = new Paragraph("Thank you for your purchase!", FONT_NORMAL);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        document.add(thankYou);

        Paragraph visitAgain = new Paragraph("Visit us again soon!", FONT_SMALL);
        visitAgain.setAlignment(Element.ALIGN_CENTER);
        document.add(visitAgain);
    }
}
