package com.example.flyingsaucer.repository;

import com.example.flyingsaucer.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // You can add custom queries here if needed
    // e.g., findByInvoiceId(String invoiceId)
}