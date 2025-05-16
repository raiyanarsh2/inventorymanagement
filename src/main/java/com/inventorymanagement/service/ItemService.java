package com.inventorymanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorymanagement.DTO.InvoiceDTO;
import com.inventorymanagement.DTO.ItemDTO;
import com.inventorymanagement.entity.Item;
import com.inventorymanagement.entity.Vendor;
import com.inventorymanagement.repository.ItemRepository;
import com.inventorymanagement.repository.VendorRepository;

import jakarta.transaction.Transactional;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private InvoiceService invoiceService;

    @Transactional
    public Item createItemFromDTO(ItemDTO itemDTO) {
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setQuantity(itemDTO.getQuantity());
        item.setPrice(itemDTO.getPrice());

        Vendor vendor = vendorRepository.findById(itemDTO.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + itemDTO.getVendorId()));
        item.setVendor(vendor);

        Item savedItem = itemRepository.save(item);

        // Create invoice automatically
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setVendorId(itemDTO.getVendorId());
        invoiceDTO.setItemName(itemDTO.getName());
        invoiceDTO.setItemDescription(itemDTO.getDescription());
        invoiceDTO.setQuantity(itemDTO.getQuantity());
        invoiceDTO.setUnitPrice(itemDTO.getPrice());
        invoiceDTO.setTotalAmount(itemDTO.getQuantity() * itemDTO.getPrice());

        // Pass the saved item to createInvoice
        invoiceService.createInvoice(invoiceDTO, savedItem);

        return savedItem;
    }

    @Transactional
    public List<Item> createItemsFromDTOList(List<ItemDTO> itemDTOs) {
        if (itemDTOs == null || itemDTOs.isEmpty()) {
            throw new RuntimeException("No items provided");
        }

        // Verify all items are from the same vendor
        Long vendorId = itemDTOs.get(0).getVendorId();
        boolean allSameVendor = itemDTOs.stream()
                .allMatch(dto -> dto.getVendorId().equals(vendorId));

        if (!allSameVendor) {
            throw new RuntimeException("All items must be from the same vendor");
        }

        // Get the vendor
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + vendorId));

        // Create and save all items
        List<Item> savedItems = itemDTOs.stream()
                .map(itemDTO -> {
                    Item item = new Item();
                    item.setName(itemDTO.getName());
                    item.setDescription(itemDTO.getDescription());
                    item.setQuantity(itemDTO.getQuantity());
                    item.setPrice(itemDTO.getPrice());
                    item.setVendor(vendor);
                    return itemRepository.save(item);
                })
                .collect(Collectors.toList());

        // Create a single invoice for all items
        InvoiceDTO invoiceDTO = new InvoiceDTO();
        invoiceDTO.setVendorId(vendorId);

        // Calculate total amount for all items
        double totalAmount = itemDTOs.stream()
                .mapToDouble(itemDTO -> itemDTO.getQuantity() * itemDTO.getPrice())
                .sum();

        // Add all items to the invoice
        List<String> itemNames = savedItems.stream()
                .map(Item::getName)
                .collect(Collectors.toList());
        invoiceDTO.setItemName(String.join(", ", itemNames));

        String itemDescriptions = savedItems.stream()
                .map(Item::getDescription)
                .collect(Collectors.joining("; "));
        invoiceDTO.setItemDescription(itemDescriptions);

        // Set total quantity and amount
        int totalQuantity = savedItems.stream()
                .mapToInt(Item::getQuantity)
                .sum();
        invoiceDTO.setQuantity(totalQuantity);
        invoiceDTO.setTotalAmount(totalAmount);

        // Create the invoice with PENDING status and pass the first saved item
        invoiceService.createInvoice(invoiceDTO, savedItems.get(0));

        return savedItems;
    }

    public List<ItemDTO> findAllItemDTOs() {
        return itemRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<ItemDTO> findItemByIdDTOs(Long id) {
        return itemRepository.findById(id)
                .map(this::convertToDTO);
    }

    private ItemDTO convertToDTO(Item item) {
        ItemDTO dto = new ItemDTO();
        dto.setId(item.getId());
        dto.setName(item.getName());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setVendorId(item.getVendor().getId());
        dto.setVendorName(item.getVendor().getName());
        return dto;
    }

    public Item updateItem(Item updatedItem) {
        Optional<Item> existingItemOptional = itemRepository.findById(updatedItem.getId());

        if (existingItemOptional.isPresent()) {
            Item existingItem = existingItemOptional.get();

            // Update basic fields
            existingItem.setName(updatedItem.getName());
            existingItem.setDescription(updatedItem.getDescription());
            existingItem.setPrice(updatedItem.getPrice());
            existingItem.setQuantity(updatedItem.getQuantity());

            // Update vendor if provided
            if (updatedItem.getVendor() != null) {
                existingItem.setVendor(updatedItem.getVendor());
            }

            return itemRepository.save(existingItem);
        } else {
            throw new RuntimeException("Item not found with ID: " + updatedItem.getId());
        }
    }

@Transactional
    public void deleteItem(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found with ID: " + id));
        itemRepository.delete(item); // This will automatically delete associated invoices due to cascade
    }

    public void addStock(Long itemId, int quantity) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);

        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            item.setQuantity(item.getQuantity() + quantity);
            itemRepository.save(item);
        } else {
            System.err.println("Item with ID " + itemId + " not found. Cannot add stock.");
        }
    }

    public Item updateItemFromDTO(Long id, ItemDTO itemDTO) {
        Optional<Item> existingItemOpt = itemRepository.findById(id);
        if (existingItemOpt.isEmpty()) {
            return null;
        }

        Optional<Vendor> vendorOpt = vendorRepository.findById(itemDTO.getVendorId());
        if (vendorOpt.isEmpty()) {
            throw new IllegalArgumentException("Vendor not found");
        }

        Item existingItem = existingItemOpt.get();
        existingItem.setName(itemDTO.getName());
        existingItem.setDescription(itemDTO.getDescription());
        existingItem.setQuantity(itemDTO.getQuantity());
        existingItem.setPrice(itemDTO.getPrice());
        existingItem.setVendor(vendorOpt.get());

        return itemRepository.save(existingItem);
    }
}
