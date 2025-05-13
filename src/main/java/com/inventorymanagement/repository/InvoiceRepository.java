package com.inventorymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventorymanagement.entity.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    // Custom query methods can be added here later for finding invoices
    // by type, vendor, customer, date range, etc.
    // Example:
    // List<Invoice> findByInvoiceType(String invoiceType);
    // List<Invoice> findByVendor(Vendor vendor);
    // List<Invoice> findByCustomer(Customer customer);
    // List<Invoice> findByInvoiceDateBetween(LocalDateTime startDate, LocalDateTime endDate);
}