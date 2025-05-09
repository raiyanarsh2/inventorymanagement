package com.inventorymanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.inventorymanagement.entity.PurchaseOrderItems;

@Repository
public interface PurchaseOrderItemRepository extends JpaRepository<PurchaseOrderItems, Long> {
	List<PurchaseOrderItems> findByPurchaseOrderId(Long purchaseOrderId);
}