package droolsbook.bank.service;

import java.math.BigDecimal;
import java.util.List;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.model.LoanApprovalHolder;

public interface BankingService {
  void add(Customer customer);
  void save(Customer customer);
  
  void requestLoan(Loan loan, Customer customer);
  void approveLoan(LoanApprovalHolder holder);

  List<Customer> findAllCustomers();
  
  void transfer(Account sourceAccount,
      Account destinationAccount, BigDecimal sum);
  
  
  void claim(long taskId, String userId);
  void start(long taskId, String userId);
  void complete(long taskId, String userId);
}
