package com.example.flyingsaucer.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "invoices")
@Schema(description = "Invoice entity representing a complete invoice document")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Unique identifier of the invoice", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "invoice_id", nullable = false)
    @Schema(description = "Business identifier for the invoice", example = "INV-2024-001", required = true)
    private String invoiceId;

    @Column(name = "customer_name", nullable = false)
    @Schema(description = "Name of the customer", example = "John Doe", required = true)
    private String customerName;

    @Column(nullable = false)
    @Schema(description = "Date when the invoice was created", example = "2024-01-15", required = true)
    private LocalDate date;

    @Column(name = "total_amount", precision = 10, scale = 2)
    @Schema(description = "Total amount of the invoice", example = "299.99", required = true)
    private BigDecimal totalAmount;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "invoice")
    @Schema(description = "List of items included in the invoice")
    private List<InvoiceItem> items;

    // Default constructor
    public Invoice() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceId() { return invoiceId; }
    public void setInvoiceId(String invoiceId) { this.invoiceId = invoiceId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }
}