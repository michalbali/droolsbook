package droolsbook.bank.service.impl;

import java.math.BigDecimal;
import java.util.List;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.model.LoanApprovalHolder;
import droolsbook.bank.service.BankingService;
import droolsbook.bank.service.BankingValidationService;
import droolsbook.bank.service.CEPService;
import droolsbook.bank.service.LoanApprovalService;
import droolsbook.bank.service.ValidationException;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.Message.Type;
import droolsbook.sampleApplication.repository.CustomerRepository;

public class BankingServiceImpl implements BankingService {

  private BankingValidationService validationService; 
  private LoanApprovalService loanApprovalService;
  private CEPService cepService;
  private CustomerRepository customerRepository;

  //cepService.notify(new CustomerCreatedEvent(customer));
  //cepService.notify(new CustomerUpdatedEvent(customer));
  
  // @extract-start 08 02
  /**
   * validates and stores a new customer
   */
  public void add(Customer customer) {
    validate(customer);
    customerRepository.addCustomer(customer);
  }

  /**
   * validates and stores an existing customer
   */
  public void save(Customer customer) {
    validate(customer);
    customerRepository.updateCustomer(customer);
  }
  
  /**
   * validates customer, 
   * @throws ValidationException if there are any errors
   */
  private void validate(Customer customer) {
    ValidationReport report = validationService
        .validate(customer);
    if (!report.getMessagesByType(Type.ERROR).isEmpty()) {
      throw new ValidationException(report);
    }
  }
  // @extract-end
  
  // @extract-start 08 15
  @Override
  public void requestLoan(Loan loan, Customer customer) {
    loanApprovalService.requestLoan(loan, customer);
  }
  // @extract-end
  
  @Override
  public void approveLoan(LoanApprovalHolder holder) {
    loanApprovalService.approveLoan(holder);
  }
  
  public void transfer(Account sourceAccount, Account destinationAccount, BigDecimal sum) {
     if (sourceAccount == null || destinationAccount == null || sum == null) {
       throw new RuntimeException("parameters cannot be null");
     }
     //transfer...
   }
  
  @Override
  public List<Customer> findAllCustomers() {
    return customerRepository.findAllCustomers();
  }
  
  public void setCustomerRepository(
      CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
  }
  
  public void setValidationService(
      BankingValidationService validationService) {
    this.validationService = validationService;
  }
  
  public void setLoanApprovalService(
      LoanApprovalService loanApprovalService) {
    this.loanApprovalService = loanApprovalService;
  }
  
  public void setCepService(CEPService cepService) {
    this.cepService = cepService;
  }

}
