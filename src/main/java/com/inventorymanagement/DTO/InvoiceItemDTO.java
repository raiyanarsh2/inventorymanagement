package com.inventorymanagement.DTO;

import java.math.BigDecimal;

public class InvoiceItemDTO {

    private Long id;
    private int quantity;
    private BigDecimal priceAtInvoice;
    private ItemDTO item;

    public InvoiceItemDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceAtInvoice() {
        return priceAtInvoice;
    }

    public void setPriceAtInvoice(BigDecimal priceAtInvoice) {
        this.priceAtInvoice = priceAtInvoice;
    }

    public ItemDTO getItem() {
        return item;
    }

    public void setItem(ItemDTO item) {
        this.item = item;
    }
}
