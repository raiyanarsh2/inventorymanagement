package com.inventorymanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.inventorymanagement.DTO.LoginRequest;
import com.inventorymanagement.entity.Customer;
import com.inventorymanagement.service.CustomerService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "http://localhost:3000")
public class CustomerController {

	@Autowired
	private CustomerService customerService;

	@PostMapping("/register")
	public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
		Customer createdCustomer = customerService.saveCustomer(customer);
		return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<Customer> loginCustomer(@RequestBody LoginRequest loginRequest) {

		Optional<Customer> customerOptional = customerService.findCustomerById(loginRequest.getId());

		if (customerOptional.isPresent()) {
			Customer customer = customerOptional.get();

			if (customer.getEmail().equals(loginRequest.getPassword())) {
				
//				customer.setPassword(null);

				return ResponseEntity.ok(customer);
			} else {
				
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}
		} else {
			
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); 
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<Customer> getCustomerById(@PathVariable Long id) {
		Optional<Customer> customer = customerService.findCustomerById(id);
		return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	}

	@GetMapping
	public ResponseEntity<List<Customer>> getAllCustomers() {
		List<Customer> customers = customerService.findAllCustomers();
		return new ResponseEntity<>(customers, HttpStatus.OK);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
		customerService.deleteCustomer(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}