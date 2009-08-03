package droolsbook.bank.service;

import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.model.LoanApprovalHolder;

public interface LoanApprovalService {

  LoanApprovalHolder requestLoan(Loan loan, Customer customer);
  
  void approveLoan(LoanApprovalHolder holder);
  
}
