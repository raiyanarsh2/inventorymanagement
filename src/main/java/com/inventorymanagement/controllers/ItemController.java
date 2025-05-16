package com.inventorymanagement.controllers;

import com.inventorymanagement.DTO.ItemDTO;
import com.inventorymanagement.entity.Item;
import com.inventorymanagement.service.ItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
//@CrossOrigin(origins = "http://localhost:3000")
@CrossOrigin(origins = "*")
public class ItemController {

	private final ItemService itemService;

	public ItemController(ItemService itemService) {
		this.itemService = itemService;
	}

	@PostMapping
	public ResponseEntity<Item> createItem(@RequestBody ItemDTO itemDTO) {
		Item createdItem = itemService.createItemFromDTO(itemDTO);
		return new ResponseEntity<>(createdItem, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ItemDTO> getItemById(@PathVariable Long id) {
		Optional<ItemDTO> item = itemService.findItemByIdDTOs(id);
		return item.map(ResponseEntity::ok)
				.orElseGet(() -> ResponseEntity.notFound().build());
	}


	@GetMapping
	public ResponseEntity<List<ItemDTO>> getAllItems() {
		List<ItemDTO> itemDTOs = itemService.findAllItemDTOs();
		return new ResponseEntity<>(itemDTOs, HttpStatus.OK);
	}


//	@GetMapping
//	public ResponseEntity<List<Item>> getAllItems() {
//		List<Item> items = itemService.findAllItems();
//		return new ResponseEntity<>(items, HttpStatus.OK);
//	}

//	@PutMapping
//	public ResponseEntity<Item> updateItem(@RequestBody Item item) {
//		Item updatedItem = itemService.updateItem(item);
//		if (item != null)
//			return new ResponseEntity<>(updatedItem, HttpStatus.CREATED);
//		return new ResponseEntity<>(updatedItem, HttpStatus.NOT_FOUND);
//	}

	@PutMapping("/{id}")
	public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody ItemDTO itemDTO) {
		Item updatedItem = itemService.updateItemFromDTO(id, itemDTO);
		if (updatedItem != null) {
			return new ResponseEntity<>(updatedItem, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}


	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
		itemService.deleteItem(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}