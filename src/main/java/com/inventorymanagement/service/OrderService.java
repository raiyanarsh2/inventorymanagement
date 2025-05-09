package com.inventorymanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventorymanagement.DTO.OrderDTO;
import com.inventorymanagement.entity.Order;
import com.inventorymanagement.repository.OrderItemRepository;
import com.inventorymanagement.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private ModelMapper modelMapper;

	private OrderRepository orderRepository;
	private OrderItemRepository orderItemRepository;

	public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
		this.orderRepository = orderRepository;
		this.orderItemRepository = orderItemRepository;
	}

	@Transactional
	public Order createOrder(Order order) {
		if (order.getOrderItems() != null) {
			order.getOrderItems().forEach(orderItem -> {
				orderItem.setOrder(order);
			});
		}
		return orderRepository.save(order);
	}

	public Optional<OrderDTO> findOrderById(Long id) {
		Optional<Order> orderOptional = orderRepository.findById(id);

		return orderOptional.map(order -> modelMapper.map(order, OrderDTO.class));
	}

	public List<OrderDTO> findAllOrders() {
		List<Order> orderData = orderRepository.findAll();

		return orderData.stream().map(order -> modelMapper.map(order, OrderDTO.class)).collect(Collectors.toList());
	}

	@Transactional
	public Optional<Order> updateOrder(Long id, Order updatedOrder) {
		Optional<Order> existingOrderOptional = orderRepository.findById(id);
		if (existingOrderOptional.isPresent()) {
			Order existingOrder = existingOrderOptional.get();
			existingOrder.setOrderDate(updatedOrder.getOrderDate());
			existingOrder.setTotalAmount(updatedOrder.getTotalAmount());
			if (updatedOrder.getCustomer() != null) {
				existingOrder.setCustomer(updatedOrder.getCustomer());
			}

			if (existingOrder.getOrderItems() != null) {
				orderItemRepository.deleteAll(existingOrder.getOrderItems());
				existingOrder.getOrderItems().clear();

				if (updatedOrder.getOrderItems() != null) {
					updatedOrder.getOrderItems().forEach(orderItem -> {
						orderItem.setOrder(existingOrder);
						existingOrder.getOrderItems().add(orderItem);
					});
				}
			} else {
				if (updatedOrder.getOrderItems() != null) {
					updatedOrder.getOrderItems().forEach(orderItem -> {
						orderItem.setOrder(existingOrder);
						existingOrder.getOrderItems().add(orderItem);
					});
				}
			}

			return Optional.of(orderRepository.save(existingOrder));
		}
		return Optional.empty();
	}

	@Transactional
	public boolean cancelOrder(Long id) {
		Optional<Order> orderOptional = orderRepository.findById(id);
		if (orderOptional.isPresent()) {
			Order orderToCancel = orderOptional.get();
			orderRepository.delete(orderToCancel);
			return true;
		}
		return false;
	}
}
