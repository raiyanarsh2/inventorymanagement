package com.inventorymanagement.controllers;

import com.inventorymanagement.DTO.InvoiceDTO;
import com.inventorymanagement.service.InvoiceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
public class InvoiceController {
    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody InvoiceDTO invoiceDTO) {
        return ResponseEntity.ok(invoiceService.createInvoice(invoiceDTO));
    }

    @PutMapping("/{id}/confirm-payment")
    public ResponseEntity<InvoiceDTO> confirmPayment(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.confirmPayment(id));
    }
}