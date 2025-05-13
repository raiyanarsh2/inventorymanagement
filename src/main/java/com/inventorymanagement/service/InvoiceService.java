package com.inventorymanagement.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper; // If using ModelMapper
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventorymanagement.DTO.InvoiceDTO;
import com.inventorymanagement.DTO.InvoiceItemDTO;
import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.entity.Invoice;
import com.inventorymanagement.entity.InvoiceItem;
import com.inventorymanagement.entity.Item;
import com.inventorymanagement.entity.Vendor;
import com.inventorymanagement.repository.InvoiceItemRepository;
import com.inventorymanagement.repository.InvoiceRepository;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceItemRepository invoiceItemRepository;

    @Autowired
    private ItemService itemService; // Inject ItemService

    @Autowired
    private VendorService vendorService; // Inject VendorService

    @Autowired
    private CustomerService customerService; // Inject CustomerService

    @Autowired // If using ModelMapper
    private ModelMapper modelMapper;

    @Transactional
    public InvoiceDTO createInvoice(InvoiceDTO invoiceDto) {
        // 1. Map DTO to Entity (Invoice)
        Invoice invoice = modelMapper.map(invoiceDto, Invoice.class);

        // 2. Set Invoice Date and initial Status
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setStatus("Issued"); // Or "Pending", depending on your workflow
        invoice.setOutstandingAmount(BigDecimal.ZERO); // Will be calculated

        // 3. Handle relationships based on invoice type
        if ("BUY".equalsIgnoreCase(invoice.getInvoiceType())) {
            // Fetch Vendor Entity for BUY invoices
            if (invoiceDto.getVendor() != null && invoiceDto.getVendor().getId() != null) {
                vendorService.findVendorById(invoiceDto.getVendor().getId()).ifPresent(vendorDTO -> {
                    Vendor vendor = modelMapper.map(vendorDTO, Vendor.class);
                    invoice.setVendor(vendor);
                });
            } else {
                throw new IllegalArgumentException("Vendor details are required for BUY invoices.");
            }
            invoice.setCustomer(null); // Ensure customer is null for BUY invoices

        } else if ("SELL".equalsIgnoreCase(invoice.getInvoiceType())) {
            // Fetch Customer Entity for SELL invoices
            if (invoiceDto.getCustomer() != null && invoiceDto.getCustomer().getId() != null) {
            	System.out.println("1");
                 customerService.findCustomerById(invoiceDto.getCustomer().getId()).ifPresent(customerDTO -> {
                     Customer customer = modelMapper.map(customerDTO, Customer.class);
                     invoice.setCustomer(customer);
                 });
                 System.out.println("11");
            } else {
                 throw new IllegalArgumentException("Customer details are required for SELL invoices.");
            }
            invoice.setVendor(null); // Ensure vendor is null for SELL invoices

        } else {
            throw new IllegalArgumentException("Invalid invoice type: " + invoice.getInvoiceType());
        }


        // 4. Save the Invoice first to get the generated ID
        System.out.println("2");
        System.out.println(invoice);
        Invoice savedInvoice = invoiceRepository.save(invoice);
        System.out.println("22");
        // 5. Process Invoice Items and prepare a list for saving
        List<InvoiceItem> invoiceItemsToSave = new ArrayList<>(); // Use ArrayList for mutability
        if (invoiceDto.getInvoiceItems() != null) {
        	System.out.println("3");
             for (InvoiceItemDTO invoiceItemDTO : invoiceDto.getInvoiceItems()) { // Iterate with a traditional loop
                InvoiceItem item = modelMapper.map(invoiceItemDTO, InvoiceItem.class);
                item.setInvoice(savedInvoice); // Set the relationship
                System.out.println("322");
                // Fetch Item entity and set to InvoiceItem
                if (invoiceItemDTO.getItem() != null && invoiceItemDTO.getItem().getId() != null) {
                	System.out.println("222");
                    itemService.findItemById(invoiceItemDTO.getItem().getId()).ifPresent(foundItemDTO -> {
                         Item itemEntity = modelMapper.map(foundItemDTO, Item.class);
                         item.setItem(itemEntity);
                    });
                }

                 // Update inventory based on invoice type
                if ("BUY".equalsIgnoreCase(savedInvoice.getInvoiceType())) {
                    itemService.addStock(item.getItem().getId(), item.getQuantity());
                } 
                else if ("SELL".equalsIgnoreCase(savedInvoice.getInvoiceType())) {
                    // You would typically perform stock check *before* deducting
                    // For simplicity here, we'll deduct directly.
                    // A more robust implementation would check stock availability first.
                	System.out.println("2222");
                    itemService.removeStock(item.getItem().getId(), item.getQuantity());
                }
                invoiceItemsToSave.add(item); // Add to the mutable list
             }
        }

        // 6. Save Invoice Items
        invoiceItemRepository.saveAll(invoiceItemsToSave);
        savedInvoice.setInvoiceItems(invoiceItemsToSave); // Set relationship back to Invoice

        // 7. Calculate total amount after processing all items
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (InvoiceItem item : invoiceItemsToSave) {
             BigDecimal itemTotal = item.getPriceAtInvoice().multiply(BigDecimal.valueOf(item.getQuantity()));
             totalAmount = totalAmount.add(itemTotal);
        }


        // 8. Update Invoice with calculated total amount and initial outstanding
        savedInvoice.setTotalAmount(totalAmount);
        savedInvoice.setOutstandingAmount(totalAmount); // Initially, outstanding is the total amount
        invoiceRepository.save(savedInvoice);


        // 9. Map the saved Entity back to DTO for the response
        return modelMapper.map(savedInvoice, InvoiceDTO.class);
    }

    public Optional<InvoiceDTO> findInvoiceById(Long id) {
        Optional<Invoice> invoiceOptional = invoiceRepository.findById(id);

        // Convert the found entity to DTO if present
        return invoiceOptional.map(invoice -> modelMapper.map(invoice, InvoiceDTO.class));
    }

    public List<InvoiceDTO> findAllInvoices() {
        List<Invoice> invoices = invoiceRepository.findAll();
        return invoices.stream()
                .map(invoice -> modelMapper.map(invoice, InvoiceDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public InvoiceDTO updateInvoice(Long id, InvoiceDTO invoiceDto) {
        // Implement update logic:
        // 1. Find existing invoice by ID.
        // 2. If found, update its properties from the DTO.
        // 3. Handle updates to InvoiceItems (add, remove, modify).
        // 4. Recalculate totalAmount and outstandingAmount if items change.
        // 5. Update inventory if quantities change (more complex for updates).
        // 6. Save the updated invoice and items.
        // 7. Map and return the updated DTO.
         return null; // Placeholder
    }

    @Transactional
    public void cancelInvoice(Long id) {
        // Implement cancel logic:
        // 1. Find invoice by ID.
        // 2. If found and status allows cancellation:
        //    - Update status to "Canceled".
        //    - Consider implications for inventory if stock was already moved (requires return logic).
        //    - Save the updated invoice.
         return; // Placeholder
    }

    // You would also add methods here for:
    // - Recording payments against invoices (updating outstandingAmount)
    // - Querying outstanding invoices by type and date
    // - Other specific invoice-related business logic

}
