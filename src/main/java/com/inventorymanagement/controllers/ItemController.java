package com.inventorymanagement.controllers;

import com.inventorymanagement.entity.Item;
import com.inventorymanagement.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@PostMapping
	public ResponseEntity<Item> createItem(@RequestBody Item item) {
		Item createdItem = itemService.saveItem(item);
		return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Item> getItemById(@PathVariable Long id) {
		Optional<Item> item = itemService.findItemById(id);
		return item.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<Item>> getAllItems() {
		List<Item> items = itemService.findAllItems();
		return new ResponseEntity<>(items, HttpStatus.OK);
	}

	@PutMapping
	public ResponseEntity<Item> updateItem(@RequestBody Item item) {
		Item updatedItem = itemService.updateItem(item);
		if (item != null)
			return new ResponseEntity<>(updatedItem, HttpStatus.CREATED);
		return new ResponseEntity<>(updatedItem, HttpStatus.NOT_FOUND);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
		itemService.deleteItem(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}