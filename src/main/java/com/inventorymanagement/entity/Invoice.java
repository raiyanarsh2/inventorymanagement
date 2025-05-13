package com.inventorymanagement.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Column(nullable = false)
    private LocalDateTime invoiceDate;

//    @Column(nullable = false)
    private LocalDate dueDate;

//    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalAmount;

//    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal outstandingAmount;

//    @Column(nullable = false)
    private String status; // e.g., "Pending", "Issued", "Paid", "Canceled"

//    @Column(nullable = false)
    private String invoiceType; // e.g., "BUY", "SELL"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InvoiceItem> invoiceItems = new ArrayList<>();

    // Getters and Setters

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

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    // Helper methods to manage the relationship with InvoiceItem
    public void addInvoiceItem(InvoiceItem invoiceItem) {
        invoiceItems.add(invoiceItem);
        invoiceItem.setInvoice(this);
    }

    public void removeInvoiceItem(InvoiceItem invoiceItem) {
        invoiceItems.remove(invoiceItem);
        invoiceItem.setInvoice(null);
    }

	@Override
	public String toString() {
		return "Invoice [id=" + id + ", invoiceDate=" + invoiceDate + ", dueDate=" + dueDate + ", totalAmount="
				+ totalAmount + ", outstandingAmount=" + outstandingAmount + ", status=" + status + ", invoiceType="
				+ invoiceType + ", vendor=" + vendor + ", customer=" + customer + ", invoiceItems=" + invoiceItems
				+ "]";
	}
    
}