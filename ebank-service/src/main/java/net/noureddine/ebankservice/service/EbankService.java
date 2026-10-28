package net.noureddine.ebankservice.service;

import net.noureddine.ebankservice.entities.BankAccount;
import net.noureddine.ebankservice.feign.CustomerRestClient;
import net.noureddine.ebankservice.model.Customer;
import net.noureddine.ebankservice.repository.BankAccountRepository;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class EbankService {
    private BankAccountRepository accountRepository;
    private CustomerRestClient customerRestClient;

    public EbankService(CustomerRestClient customerRestClient, BankAccountRepository accountRepository) {
        this.customerRestClient = customerRestClient;
        this.accountRepository = accountRepository;
    }
    @McpTool(description = "Get all bank accounts")
    public List<BankAccount> getAllBankAccounts(){
        return accountRepository.findAll();
    }

    @McpTool(description = "Get a bank account by id")
    public BankAccount getBankAccountById( @McpToolParam(description = "the bank account id") String id){
       BankAccount bankAccount= accountRepository.findById(id)
                .orElseThrow(()->new RuntimeException("Account not found"));
       bankAccount.setCustomer(
               customerRestClient.getCustomerById(bankAccount.getCustomerId()));
       return bankAccount;
    }

    @McpTool(description = "Save a new bank account")
public BankAccount save(@McpToolParam(description = "the bank account to save(balance,type,customerId)") BankAccount bankAccount){
        try {
            Customer customer =customerRestClient.getCustomerById(bankAccount.getCustomerId());
            bankAccount.setId(UUID.randomUUID().toString());
            bankAccount.setCreatedAt(new Date());
            return accountRepository.save(bankAccount);
        }
        catch (Exception e){
            throw  new RuntimeException(e.getMessage());
        }

}



}
