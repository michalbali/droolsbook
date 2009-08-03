package droolsbook.bank.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DefaultBankingFactory implements BankingFactory {

  @Override
  public Customer createCustomer() {
    Customer customer = new Customer();    
    customer.setUuid(UUID.randomUUID().toString());
    Set<Account> accounts = new HashSet<Account>(); 
    customer.setAccounts(accounts);
    return customer;
  }
  
  @Override
  public Address createAddress(Customer customer) {    
    Address address = new Address();
    address.setUuid(UUID.randomUUID().toString());
    customer.setAddress(address);
    return address;
  }
  
  @Override
  public Account createAccount(Customer customer) {
    Account account = new Account();
    account.setUuid(UUID.randomUUID().toString());
    account.setOwner(customer);
    customer.getAccounts().add(account);
    return account;
  }
  
  @Override
  public Loan createLoan() {
    Loan loan = new Loan();
    return loan;
  }

}
