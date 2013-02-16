package droolsbook.bank.service;

import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.model.LoanApprovalHolder;

public interface LoanApprovalService {

  LoanApprovalHolder requestLoan(Loan loan, Customer customer);
  
  void approveLoan(LoanApprovalHolder holder);
  
  void claim(long taskId, String userId);
  void start(long taskId, String userId);
  void complete(long taskId, String userId);
}
