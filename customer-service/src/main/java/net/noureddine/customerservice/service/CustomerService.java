package net.noureddine.customerservice.service;

import net.noureddine.customerservice.entities.Customer;
import net.noureddine.customerservice.repository.CustomerRepository;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

@McpTool(description = "Get All customers")
    public List<Customer> getAllCustomers(){
       return customerRepository.findAll();
    }

    @McpTool(description = "Find a customer by id")
    public Customer findCustomerById(@McpToolParam(description = "the customer id") Long id){
        return customerRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Customer Not found"));
    }

    @McpTool(description = "save a new customer")
    public Customer saveCustomer(@McpToolParam(description = "the customer to save(name,email)") Customer customer){
        return customerRepository.save(customer);
    }
}
