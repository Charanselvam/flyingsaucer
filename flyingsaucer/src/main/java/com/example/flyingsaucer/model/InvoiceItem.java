package com.example.flyingsaucer.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Schema(description = "Individual item within an invoice")
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the invoice item", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Schema(description = "Name of the item", example = "Product A", required = true)
    private String name;

    @Schema(description = "Quantity of the item", example = "2", required = true)
    private int quantity;

    @Schema(description = "Price per unit of the item", example = "99.99", required = true)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "invoice_id", nullable = false)
    @Schema(description = "Parent invoice that contains this item", accessMode = Schema.AccessMode.READ_ONLY)
    private Invoice invoice;

    // Constructors
    public InvoiceItem() {}
    
    public InvoiceItem(String name, int quantity, BigDecimal price) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
}