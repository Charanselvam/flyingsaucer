package com.example.flyingsaucer.model;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.util.List;


@Schema(description = "Request object for PDF generation containing invoice data")
public class PdfRequest {
    
    @Schema(description = "Business identifier for the invoice", example = "INV-2024-001", required = true)
    private String invoiceId;
    
    @Schema(description = "Name of the customer", example = "John Doe", required = true)
    private String customerName;
    
    @Schema(description = "Date of the invoice in YYYY-MM-DD format", example = "2024-01-15", required = true)
    private String date;
    
    @Schema(description = "List of items included in the invoice", required = true)
    private List<Item> items;
    
    @Schema(description = "Total amount of the invoice", example = "299.99", required = true)
    private BigDecimal totalAmount;

    @Schema(description = "Individual item within an invoice request")
    public static class Item {
        
        @Schema(description = "Name of the item", example = "Product A", required = true)
        private String name;
        
        @Schema(description = "Quantity of the item", example = "2", required = true)
        private int quantity;
        
        @Schema(description = "Price per unit of the item", example = "99.99", required = true)
        private BigDecimal price;

        // Getters and Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public BigDecimal getPrice() { return price; }
        public void setPrice(BigDecimal price) { this.price = price; }
    }

    // Getters and Setters
    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}