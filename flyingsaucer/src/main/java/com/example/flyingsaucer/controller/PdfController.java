package com.example.flyingsaucer.controller;

import com.example.flyingsaucer.model.PdfRequest;
import com.example.flyingsaucer.service.PdfService;
import com.itextpdf.text.DocumentException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/pdf")
@Tag(name = "PDF Generation API", description = "APIs for generating and managing PDF invoices")
public class PdfController {

    @Autowired
    private PdfService pdfService;

    @Operation(
        summary = "Generate PDF from JSON request",
        description = "Creates a PDF invoice from the provided JSON data and returns it as a downloadable file"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF generated successfully", 
                    content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> generatePdf(
            @Parameter(description = "PDF request data", required = true)
            @Valid @RequestBody PdfRequest request) {
        try {
            String html = pdfService.renderHtmlFromTemplate(request, "invoice");
            byte[] pdfBytes = generatePdfAsBytes(html);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice.pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(
        summary = "Generate PDF with download link",
        description = "Creates a PDF invoice and returns JSON with file information and download URL"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF generated successfully"),
        @ApiResponse(responseCode = "500", description = "PDF generation failed")
    })
    @PostMapping("/generate-with-link")
    public ResponseEntity<Map<String, String>> generatePdfWithLink(
            @Parameter(description = "PDF request data", required = true)
            @Valid @RequestBody PdfRequest request) {
        try {
            String html = pdfService.renderHtmlFromTemplate(request, "invoice");
            String fileName = "invoice_" + UUID.randomUUID();
            String filePath = pdfService.generatePdfFromHtml(html, fileName);

            Map<String, String> response = new HashMap<>();
            response.put("message", "PDF generated successfully");
            response.put("filePath", filePath);
            response.put("downloadUrl", "/api/pdf/download/" + fileName + ".pdf");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "PDF generation failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    @Operation(
        summary = "Get PDF from database by ID",
        description = "Retrieves an invoice from the database and returns it as a PDF file"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF generated successfully",
                    content = @Content(mediaType = "application/pdf")),
        @ApiResponse(responseCode = "404", description = "Invoice not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping(value = "/invoice/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getPdfFromDatabase(
            @Parameter(description = "Invoice ID", example = "1", required = true)
            @PathVariable Long id) {
        try {
            byte[] pdfBytes = pdfService.generatePdfBytesFromDatabase(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "invoice_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(
        summary = "Generate PDF from database (JSON response)",
        description = "Generates a PDF from database and returns file information in JSON format"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "PDF generated successfully"),
        @ApiResponse(responseCode = "404", description = "Invoice not found")
    })
    @GetMapping("/generate/{id}")
    public ResponseEntity<Map<String, String>> generatePdfFromDb(
            @Parameter(description = "Invoice ID", example = "1", required = true)
            @PathVariable Long id) {
        try {
            String filePath = pdfService.generatePdfFromDatabase(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "PDF generated from database");
            response.put("filePath", filePath);
            response.put("invoiceId", id.toString());
            response.put("downloadUrl", "/api/pdf/invoice/" + id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to generate PDF from DB: " + e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    @Operation(
        summary = "Save invoice to database",
        description = "Saves invoice data to the database and returns the saved entity"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Invoice saved successfully"),
        @ApiResponse(responseCode = "500", description = "Save operation failed")
    })
    @PostMapping("/save")
    public ResponseEntity<?> saveToDatabase(
            @Parameter(description = "PDF request data", required = true,
                      content = @Content(examples = @ExampleObject(value = "{\n" +
                              "  \"invoiceId\": \"INV-001\",\n" +
                              "  \"customerName\": \"John Doe\",\n" +
                              "  \"date\": \"2024-01-15\",\n" +
                              "  \"items\": [\n" +
                              "    {\"name\": \"Item 1\", \"quantity\": 2, \"price\": 100},\n" +
                              "    {\"name\": \"Item 2\", \"quantity\": 1, \"price\": 200}\n" +
                              "  ],\n" +
                              "  \"totalAmount\": 400\n" +
                              "}")))
            @RequestBody PdfRequest request) {
        try {
            var saved = pdfService.saveInvoiceFromRequest(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invoice saved to database");
            response.put("id", saved.getId());
            response.put("invoiceId", saved.getInvoiceId());
            response.put("pdfUrl", "/api/pdf/invoice/" + saved.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Save failed: " + e.getMessage());
        }
    }

    private byte[] generatePdfAsBytes(String html) throws IOException, DocumentException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            org.xhtmlrenderer.pdf.ITextRenderer renderer = new org.xhtmlrenderer.pdf.ITextRenderer();
            renderer.setDocumentFromString(html);
            renderer.layout();
            renderer.createPDF(outputStream);
            return outputStream.toByteArray();
        }
    }
}