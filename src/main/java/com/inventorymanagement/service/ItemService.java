package com.inventorymanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.inventorymanagement.DTO.ItemDTO;
import com.inventorymanagement.entity.Vendor;
import com.inventorymanagement.repository.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorymanagement.entity.Item;
import com.inventorymanagement.repository.ItemRepository;

import jakarta.transaction.Transactional;

@Service
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

    @Autowired
    private VendorRepository vendorRepository;

    public Item createItem(ItemDTO itemDTO) {
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setQuantity(itemDTO.getQuantity());
        item.setPrice(itemDTO.getPrice());

        Vendor vendor = vendorRepository.findById(itemDTO.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        item.setVendor(vendor);
        return itemRepository.save(item);
    }

    public Item createItemFromDTO(ItemDTO itemDTO) {
        Item item = new Item();
        item.setName(itemDTO.getName());
        item.setDescription(itemDTO.getDescription());
        item.setQuantity(itemDTO.getQuantity());
        item.setPrice(itemDTO.getPrice());

        Vendor vendor = vendorRepository.findById(itemDTO.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found with ID: " + itemDTO.getVendorId()));
        item.setVendor(vendor);

        return itemRepository.save(item);
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

	public Item saveItem(Item item) {
		return itemRepository.save(item);
	}

	public Optional<Item> findItemById(Long id) {
		return itemRepository.findById(id);
	}

	public List<Item> findAllItems() {
		return itemRepository.findAll();
	}

    public List<ItemDTO> findAllItemDTOs() {
        List<Item> items = itemRepository.findAll();
        return items.stream().map(ItemDTO::new).collect(Collectors.toList());
    }

    public Optional<ItemDTO> findItemByIdDTOs(Long id) {
        Optional<Item> item = itemRepository.findById(id);
        return item.map(ItemDTO::new);  // map item to itemDTO if present
    }


    public void deleteItem(Long id) {
		itemRepository.deleteById(id);
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
	
	@Transactional
    public void removeStock(Long itemId, int quantity) {
        Optional<Item> itemOptional = itemRepository.findById(itemId);
        if (itemOptional.isPresent()) {
            Item item = itemOptional.get();
            if (item.getQuantity() >= quantity) {
                item.setQuantity(item.getQuantity() - quantity);
                itemRepository.save(item);
            } else {
                throw new RuntimeException("Insufficient stock for item with id: " + itemId); 
            }
        } else {
            throw new RuntimeException("Item not found with id: " + itemId);
        }
    }

    public Item updateItemFromDTO(Long id, ItemDTO itemDTO) {
        Optional<Item> existingItemOpt = itemRepository.findById(id);
        if (existingItemOpt.isEmpty()) return null;

        Optional<Vendor> vendorOpt = vendorRepository.findById(itemDTO.getVendorId());
        if (vendorOpt.isEmpty()) throw new IllegalArgumentException("Vendor not found");

        Item existingItem = existingItemOpt.get();
        existingItem.setName(itemDTO.getName());
        existingItem.setDescription(itemDTO.getDescription());
        existingItem.setQuantity(itemDTO.getQuantity());
        existingItem.setPrice(itemDTO.getPrice());
        existingItem.setVendor(vendorOpt.get());

        return itemRepository.save(existingItem);
    }

}