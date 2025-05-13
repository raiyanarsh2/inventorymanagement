package com.inventorymanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventorymanagement.entity.InvoiceItem;

import java.util.List;

@Repository
public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    // Consider adding custom query methods here, for example:
    // List<InvoiceItem> findByInvoiceId(Long invoiceId);

}