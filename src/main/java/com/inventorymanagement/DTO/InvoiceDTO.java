package com.inventorymanagement.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class InvoiceDTO {

    private Long id;
    private LocalDateTime invoiceDate;
    private LocalDate dueDate;
    private BigDecimal totalAmount;
    private BigDecimal outstandingAmount;
    private String status;
    private String invoiceType; // e.g., "BUY", "SELL"
    private VendorDTO vendor;
    private CustomerDTO customer;
    private List<InvoiceItemDTO> invoiceItems;

    public InvoiceDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(LocalDateTime invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getOutstandingAmount() {
        return outstandingAmount;
    }

    public void setOutstandingAmount(BigDecimal outstandingAmount) {
        this.outstandingAmount = outstandingAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    public VendorDTO getVendor() {
        return vendor;
    }

    public void setVendor(VendorDTO vendor) {
        this.vendor = vendor;
    }

    public CustomerDTO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerDTO customer) {
        this.customer = customer;
    }

    public List<InvoiceItemDTO> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItemDTO> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }
}