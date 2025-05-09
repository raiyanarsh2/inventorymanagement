package com.inventorymanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorymanagement.entity.Item;
import com.inventorymanagement.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

	@Autowired
	private ItemRepository itemRepository;

	public Item updateItem(Item item) {

		return itemRepository.save(item);

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
}