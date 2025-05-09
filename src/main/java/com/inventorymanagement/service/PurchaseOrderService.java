package com.inventorymanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.inventorymanagement.entity.PurchaseOrder;
import com.inventorymanagement.entity.PurchaseOrderItems;
import com.inventorymanagement.repository.PurchaseOrderItemRepository;
import com.inventorymanagement.repository.PurchaseOrderRepository;

@Service
public class PurchaseOrderService {

	@Autowired
	private PurchaseOrderRepository purchaseOrderRepository;

	@Autowired
	private PurchaseOrderItemRepository purchaseOrderItemRepository;

	@Autowired
	private ItemService itemService;

	public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
		return purchaseOrderRepository.save(purchaseOrder);
	}

	public Optional<PurchaseOrder> findPurchaseOrderById(Long id) {
		return purchaseOrderRepository.findById(id);
	}

	public List<PurchaseOrder> findAllPurchaseOrders() {
		return purchaseOrderRepository.findAll();
	}

	public Optional<PurchaseOrder> updatePurchaseOrder(Long id, PurchaseOrder purchaseOrderDetails) {
		Optional<PurchaseOrder> existingPurchaseOrder = purchaseOrderRepository.findById(id);

		if (existingPurchaseOrder.isPresent()) {
			PurchaseOrder purchaseOrderToUpdate = existingPurchaseOrder.get();
			purchaseOrderToUpdate.setOrderDate(purchaseOrderDetails.getOrderDate());
			purchaseOrderToUpdate.setExpectedDeliveryDate(purchaseOrderDetails.getExpectedDeliveryDate());
			purchaseOrderToUpdate.setTotalAmount(purchaseOrderDetails.getTotalAmount());
			purchaseOrderToUpdate.setStatus(purchaseOrderDetails.getStatus());
			purchaseOrderToUpdate.setVendor(purchaseOrderDetails.getVendor());

			return Optional.of(purchaseOrderRepository.save(purchaseOrderToUpdate));
		} else {
			return Optional.empty();
		}
	}

	public boolean deletePurchaseOrder(Long id) {
		if (purchaseOrderRepository.existsById(id)) {
			purchaseOrderRepository.deleteById(id);
			return true;
		} else {
			return false;
		}
	}

	public Optional<PurchaseOrder> receiveStockForPurchaseOrder(Long purchaseOrderId) {
		Optional<PurchaseOrder> purchaseOrderOptional = purchaseOrderRepository.findById(purchaseOrderId);

		if (purchaseOrderOptional.isPresent()) {
			PurchaseOrder purchaseOrder = purchaseOrderOptional.get();
			purchaseOrder.setStatus("Received");

			List<PurchaseOrderItems> orderItems = purchaseOrderItemRepository.findByPurchaseOrderId(purchaseOrderId);

			for (PurchaseOrderItems item : orderItems) {
				itemService.addStock(item.getItem().getId(), item.getQuantityOrdered());
			}

			return Optional.of(purchaseOrderRepository.save(purchaseOrder));

		} else {
			return Optional.empty();
		}
	}
}
