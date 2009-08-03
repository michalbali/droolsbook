package droolsbook.bank.service;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;

public interface BankingInquiryService {
  Customer findCustomerByExample(Customer customerExample);
  boolean isAccountNumberUnique(Account accout);
}
