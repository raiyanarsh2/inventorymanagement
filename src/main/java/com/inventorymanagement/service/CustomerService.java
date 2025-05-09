package com.inventorymanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.repository.CustomerRepository;

@Service
public class CustomerService {

	@Autowired
	private CustomerRepository customerRepository;

	public Customer saveCustomer(Customer customer) {
		return customerRepository.save(customer);
	}

	public Optional<Customer> findCustomerById(Long id) {
		return customerRepository.findById(id);
	}

	public List<Customer> findAllCustomers() {
		return customerRepository.findAll();
	}

	public void deleteCustomer(Long id) {
		customerRepository.deleteById(id);
	}

}