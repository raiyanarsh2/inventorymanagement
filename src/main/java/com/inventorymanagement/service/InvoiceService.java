package com.inventorymanagement.service;

import com.inventorymanagement.DTO.InvoiceDTO;
import com.inventorymanagement.entity.Invoice;
import com.inventorymanagement.entity.Item;
import com.inventorymanagement.entity.Vendor;
import com.inventorymanagement.repository.InvoiceRepository;
import com.inventorymanagement.repository.ItemRepository;
import com.inventorymanagement.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO) {
        Invoice invoice = new Invoice();
        
        // Set vendor
        Vendor vendor = vendorRepository.findById(invoiceDTO.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        invoice.setVendor(vendor);

        // Create and save new item
        Item item = new Item();
        item.setName(invoiceDTO.getItemName());
        item.setDescription(invoiceDTO.getItemDescription());
        item.setQuantity(invoiceDTO.getQuantity());
        item.setPrice(invoiceDTO.getUnitPrice());
        item.setVendor(vendor);
        
        Item savedItem = itemRepository.save(item);
        
        // Set item in invoice
        invoice.setItem(savedItem);

        // Set other details
        invoice.setQuantity(invoiceDTO.getQuantity());
        invoice.setUnitPrice(invoiceDTO.getUnitPrice());
        invoice.setTotalAmount(invoiceDTO.getTotalAmount());
        invoice.setStatus("PENDING");
        invoice.setCreatedDate(LocalDateTime.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertToDTO(savedInvoice);
    }

    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDTO, Item existingItem) {
        Invoice invoice = new Invoice();
        
        // Set vendor
        Vendor vendor = vendorRepository.findById(invoiceDTO.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        invoice.setVendor(vendor);

        // Use the existing item
        invoice.setItem(existingItem);

        // Set other details
        invoice.setQuantity(invoiceDTO.getQuantity());
        invoice.setUnitPrice(invoiceDTO.getUnitPrice());
        invoice.setTotalAmount(invoiceDTO.getTotalAmount());
        invoice.setStatus("PENDING");
        invoice.setCreatedDate(LocalDateTime.now());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return convertToDTO(savedInvoice);
    }

    @Transactional
    public InvoiceDTO confirmPayment(Long invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        invoice.setStatus("PAID");
        invoice.setPaymentDate(LocalDateTime.now());

        Invoice updatedInvoice = invoiceRepository.save(invoice);
        return convertToDTO(updatedInvoice);
    }

    private InvoiceDTO convertToDTO(Invoice invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setId(invoice.getId());
        dto.setVendorId(invoice.getVendor().getId());
        dto.setItemName(invoice.getItem().getName());
        dto.setItemDescription(invoice.getItem().getDescription());
        dto.setQuantity(invoice.getQuantity());
        dto.setUnitPrice(invoice.getUnitPrice());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setStatus(invoice.getStatus());
        dto.setCreatedDate(invoice.getCreatedDate());
        dto.setPaymentDate(invoice.getPaymentDate());
        return dto;
    }
}