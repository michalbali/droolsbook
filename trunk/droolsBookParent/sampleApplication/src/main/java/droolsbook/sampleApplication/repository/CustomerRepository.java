package droolsbook.sampleApplication.repository;

import java.util.List;
import java.util.UUID;

import droolsbook.bank.model.Customer;

public interface CustomerRepository {

  Customer findCustomerByUuid(String customerUuid);
  
  List<Customer> findAllCustomers();
  
  List<Customer> findCustomerByName(String firstName,
      String lastName);
  
  void addCustomer(final Customer customer);
  
  Customer updateCustomer(final Customer customer);
  
}
