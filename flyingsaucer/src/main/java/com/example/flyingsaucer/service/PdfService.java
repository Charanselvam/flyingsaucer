package com.example.flyingsaucer.service;

import com.example.flyingsaucer.model.Invoice;
import com.example.flyingsaucer.model.InvoiceItem;
import com.example.flyingsaucer.model.PdfRequest;
import com.example.flyingsaucer.repository.InvoiceRepository;
import com.itextpdf.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private InvoiceRepository invoiceRepository;

    private static final String OUTPUT_DIR = "generated-pdfs/";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Generate PDF from direct JSON input (existing)
    public String generatePdfFromHtml(String html, String fileName) throws IOException, DocumentException {
        Path outputDirectory = Paths.get(OUTPUT_DIR);
        if (!Files.exists(outputDirectory)) {
            Files.createDirectories(outputDirectory);
        }

        String filePath = OUTPUT_DIR + fileName + ".pdf";
        try (OutputStream outputStream = new FileOutputStream(filePath)) {
            ITextRenderer renderer = new ITextRenderer();
            
            // Add image support
            renderer.getSharedContext().setBaseURL("file:///");
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
        }
        return filePath;
    }

    // Render HTML from Thymeleaf template
    public String renderHtmlFromTemplate(Object data, String templateName) {
        Context context = new Context();
        if (data instanceof PdfRequest) {
            PdfRequest request = (PdfRequest) data;
            context.setVariable("invoiceId", request.getInvoiceId());
            context.setVariable("customerName", request.getCustomerName());
            context.setVariable("date", request.getDate());
            context.setVariable("items", request.getItems());
            context.setVariable("totalAmount", request.getTotalAmount());
        } else if (data instanceof Invoice) {
            Invoice invoice = (Invoice) data;
            context.setVariable("invoiceId", invoice.getInvoiceId());
            context.setVariable("customerName", invoice.getCustomerName());
            context.setVariable("date", invoice.getDate().format(FORMATTER));
            context.setVariable("items", invoice.getItems());
            context.setVariable("totalAmount", invoice.getTotalAmount());
        }
        return templateEngine.process(templateName, context);
    }

    // Generate PDF from Database by invoice ID
    public String generatePdfFromDatabase(Long invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        String html = renderHtmlFromTemplate(invoice, "invoice");
        String fileName = "invoice_db_" + invoice.getInvoiceId() + "_" + System.currentTimeMillis();
        return generatePdfFromHtml(html, fileName);
    }

    // Generate PDF as byte array from database
    public byte[] generatePdfBytesFromDatabase(Long invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

        String html = renderHtmlFromTemplate(invoice, "invoice");
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            
            // Add image support for byte array generation
            renderer.getSharedContext().setBaseURL("file:///");
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

    // Optional: Enhanced method with better image handling
    public byte[] generatePdfWithLogo(Long invoiceId) throws Exception {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        String html = renderHtmlFromTemplate(invoice, "invoice");
        
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ITextRenderer renderer = new ITextRenderer();
            
            // Set base URL for resolving relative paths
            String basePath = new ClassPathResource("static/").getFile().getAbsolutePath();
            renderer.getSharedContext().setBaseURL("file://" + basePath + "/");
            
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }

    // Save JSON data to DB
    public Invoice saveInvoiceFromRequest(PdfRequest request) {
        Invoice invoice = new Invoice();
        invoice.setInvoiceId(request.getInvoiceId());
        invoice.setCustomerName(request.getCustomerName());
        invoice.setDate(java.time.LocalDate.parse(request.getDate()));
        invoice.setTotalAmount(request.getTotalAmount());

        List<InvoiceItem> items = request.getItems().stream().map(item -> {
            InvoiceItem ii = new InvoiceItem(item.getName(), item.getQuantity(), item.getPrice());
            ii.setInvoice(invoice);
            return ii;
        }).collect(Collectors.toList());

        invoice.setItems(items);
        return invoiceRepository.save(invoice);
    }

    // Getter for repository
    public InvoiceRepository getInvoiceRepository() {
        return invoiceRepository;
    }
}