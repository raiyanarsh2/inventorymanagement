package com.inventorymanagement.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.inventorymanagement.DTO.PurchaseOrderDTO;
import com.inventorymanagement.entity.PurchaseOrder;
import com.inventorymanagement.service.PurchaseOrderService;

@RestController
@RequestMapping("/api/purchaseorders")
public class PurchaseOrderController {

	@Autowired
	private PurchaseOrderService purchaseOrderService;

//	@PostMapping
//	public ResponseEntity<PurchaseOrderDTO> createPurchaseOrder(@RequestBody PurchaseOrder purchaseOrder) {
//		PurchaseOrder createdPurchaseOrder = purchaseOrderService.createPurchaseOrder(purchaseOrder);
//		return new ResponseEntity<>(createdPurchaseOrder, HttpStatus.CREATED);
//	}
//
//	@GetMapping("/{id}")
//	public ResponseEntity<PurchaseOrderDTO> getPurchaseOrderById(@PathVariable Long id) {
//		Optional<PurchaseOrder> purchaseOrderDtoOptional = purchaseOrderService.findPurchaseOrderById(id);
//		return purchaseOrderDtoOptional.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//	}
//
//	@GetMapping
//	public ResponseEntity<List<PurchaseOrderDTO>> getAllPurchaseOrders() {
//		List<PurchaseOrder> purchaseOrder = purchaseOrderService.findAllPurchaseOrders();
//		return ResponseEntity.ok(purchaseOrderDtos);
//	}
//
//	@PutMapping("/{id}")
//	public ResponseEntity<PurchaseOrderDTO> updatePurchaseOrder(@PathVariable Long id,
//			@RequestBody PurchaseOrder purchaseOrder) {
//		purchaseOrder.setId(id);
//		Optional<PurchaseOrder> updatedPurchaseOrder = purchaseOrderService.updatePurchaseOrder(id, purchaseOrder);
//		return updatedPurchaseOrder.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
//	}

//	@PutMapping("/{id}/receive-stock")
//	public ResponseEntity<Void> receiveStockForPurchaseOrder(@PathVariable Long id) {
//		boolean success = purchaseOrderService.receiveStockForPurchaseOrder(id);
//		if (success) {
//			return ResponseEntity.noContent().build();
//		} else {
//
//			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//		}
//	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePurchaseOrder(@PathVariable Long id) {
		boolean deleted = purchaseOrderService.deletePurchaseOrder(id);
		if (deleted) {
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
