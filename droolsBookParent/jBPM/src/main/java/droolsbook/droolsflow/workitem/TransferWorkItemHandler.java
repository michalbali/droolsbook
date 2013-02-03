package droolsbook.droolsflow.workitem;

import java.math.BigDecimal;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Loan;
import droolsbook.bank.service.BankingService;

// @extract-start 07 04
/**
 * work item handler responsible for transferring amount from 
 * one account to another using bankingService.transfer method
 * input parameters: 'Source Account', 'Destination Account' 
 * and 'Amount'
 */
public class TransferWorkItemHandler implements
    WorkItemHandler {
  BankingService bankingService;

  public void executeWorkItem(WorkItem workItem,
      WorkItemManager manager) {
    Account sourceAccount = (Account) workItem
        .getParameter("Source Account");
    Account destinationAccount = (Account) workItem
        .getParameter("Destination Account");
    BigDecimal sum = (BigDecimal) workItem
        .getParameter("Amount");

    try {
      bankingService.transfer(sourceAccount,
          destinationAccount, sum);
      manager.completeWorkItem(workItem.getId(), null);
    } catch (Exception e) {
      e.printStackTrace();
      manager.abortWorkItem(workItem.getId());
    }
  }

  /**
   * does nothing as this work item cannot be aborted
   */
  public void abortWorkItem(WorkItem workItem,
      WorkItemManager manager) {
  }
  // @extract-end

  public void setBankingService(
      BankingService bankingService) {
    this.bankingService = bankingService;
  }
}
