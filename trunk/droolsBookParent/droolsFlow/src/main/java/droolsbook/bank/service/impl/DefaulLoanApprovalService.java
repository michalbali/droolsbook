package droolsbook.bank.service.impl;

import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.service.BankingService;
import droolsbook.bank.service.LoanApprovalService;

public class DefaulLoanApprovalService /*implements LoanApprovalService*/ {

  // @extract-start 07 06
  KnowledgeBase knowledgeBase;
  Account loanSourceAccount;
  
  /**
   * runs the  loan approval process for a specified 
   * customer's loan
   */
  public void approveLoan(Loan loan, Customer customer) {
    StatefulKnowledgeSession session = knowledgeBase
        .newStatefulKnowledgeSession();    
    try {
      //TODO: register workitem/human task handlers
      Map<String, Object> parameterMap = 
        new HashMap<String, Object>();
      parameterMap.put("loanSourceAccount",loanSourceAccount);
      parameterMap.put("customer", customer);
      parameterMap.put("loan", loan);      
      session.insert(loan);
      session.insert(customer);
      ProcessInstance processInstance = 
        session.startProcess("loanApproval", parameterMap);
      session.insert(processInstance);
      session.fireAllRules();
    } finally {
      session.dispose();
    }
  }
  // @extract-end
  
  public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
    this.knowledgeBase = knowledgeBase;
  }
  
  public void setLoanSourceAccount(Account loanSourceAccount) {
    this.loanSourceAccount = loanSourceAccount;
  }

}
