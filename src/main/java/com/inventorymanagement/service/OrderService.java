package com.inventorymanagement.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventorymanagement.DTO.OrderDTO;
import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.entity.Item;
import com.inventorymanagement.entity.Order;
import com.inventorymanagement.entity.OrderItem;
import com.inventorymanagement.repository.CustomerRepository;
import com.inventorymanagement.repository.ItemRepository;
import com.inventorymanagement.repository.OrderItemRepository;
import com.inventorymanagement.repository.OrderRepository;

@Service
public class OrderService {

	@Autowired
	private ModelMapper modelMapper;

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;

    public OrderService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CustomerRepository customerRepository,
            ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.itemRepository = itemRepository;
    }

    @Transactional
    public Order createOrder(Order order) {
        // Validate customer
        Long customerId = order.getCustomer().getId();
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found with ID: " + customerId));

        double totalAmount = 0.0;

        for (OrderItem orderItem : order.getOrderItems()) {
            Long itemId = orderItem.getItem().getId();
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Item not found with ID: " + itemId));

            // Optional: Check for stock
            if (orderItem.getQuantity() > item.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for item: " + item.getName());
            }

            // Optional: Reduce stock
            // item.setQuantity(item.getQuantity() - orderItem.getQuantity());
            // itemRepository.save(item);
            orderItem.setPriceAtOrder(item.getPrice());
            totalAmount += item.getPrice() * orderItem.getQuantity();
            orderItem.setOrder(order);
        }

        order.setTotalAmount(totalAmount);
        order.setCustomer(customer);

        return orderRepository.save(order);
    }

    public Optional<OrderDTO> findOrderById(Long id) {
        return orderRepository.findById(id)
                .map(order -> modelMapper.map(order, OrderDTO.class));
    }

    public List<OrderDTO> findAllOrders() {
        return orderRepository.findAll().stream()
                .map(order -> modelMapper.map(order, OrderDTO.class))
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<Order> updateOrder(Long id, Order updatedOrder) {
        Optional<Order> existingOrderOptional = orderRepository.findById(id);
        if (existingOrderOptional.isEmpty()) {
            return Optional.empty();
        }

        Order existingOrder = existingOrderOptional.get();

        existingOrder.setOrderDate(updatedOrder.getOrderDate());

        if (updatedOrder.getCustomer() != null) {
            Customer customer = customerRepository.findById(updatedOrder.getCustomer().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found"));
            existingOrder.setCustomer(customer);
        }

        orderItemRepository.deleteAll(existingOrder.getOrderItems());
        existingOrder.getOrderItems().clear();

        double totalAmount = 0.0;

        for (OrderItem orderItem : updatedOrder.getOrderItems()) {
            Item item = itemRepository.findById(orderItem.getItem().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Item not found"));
            orderItem.setPriceAtOrder(item.getPrice());
            totalAmount += item.getPrice() * orderItem.getQuantity();
            orderItem.setOrder(existingOrder);
            existingOrder.getOrderItems().add(orderItem);
        }

        existingOrder.setTotalAmount(totalAmount);

        return Optional.of(orderRepository.save(existingOrder));
    }

    @Transactional
    public boolean cancelOrder(Long id) {
        return orderRepository.findById(id).map(order -> {
            orderRepository.delete(order);
            return true;
        }).orElse(false);
    }
}
