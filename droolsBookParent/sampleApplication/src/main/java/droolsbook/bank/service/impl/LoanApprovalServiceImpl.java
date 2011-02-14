package droolsbook.bank.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.process.workitem.wsht.WSHumanTaskHandler;
import org.springframework.stereotype.Repository;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.model.LoanApprovalHolder;
import droolsbook.bank.service.LoanApprovalService;
import droolsbook.org.drools.persistence.KnowledgeSessionLookup;

@Repository
public class LoanApprovalServiceImpl implements
    LoanApprovalService {
  
  private KnowledgeSessionLookup sessionLookup;

  private WSHumanTaskHandler approveLoanHandler;
  private Account loanSourceAccount;
  
  @PersistenceContext(unitName="entityManagerFactory")
  EntityManager em;
  
  //session.execute(new DebugCommand());
  
  /*
  // @extract-start 08 14
  public LoanApprovalHolder requestLoan(final Loan loan,
      final Customer customer) {
    LoanApprovalHolder holder = new LoanApprovalHolder();
    StatefulKnowledgeSession session = sessionLookup
        .newSession();
    try {
      Map<String, Object> parameterMap = 
        new HashMap<String, Object>();
      parameterMap.put("loanSourceAccount",loanSourceAccount);
      parameterMap.put("customer", customer);
      parameterMap.put("loan", loan);
      session.insert(loan);
      session.insert(customer);
      ProcessInstance processInstance = session.startProcess(
          "loanApproval", parameterMap);

      holder.setCustomer(customer);
      holder.setSessionId(session.getId());
      holder.setProcessInstanceId(processInstance.getId());
      em.persist(holder);
      
      session.insert(processInstance);
      session.fireAllRules();
    } finally {
      session.dispose();
    }
    return holder;
  }
  // @extract-end
   */
  
  public LoanApprovalHolder requestLoan(final Loan loan,
      final Customer customer) {
    LoanApprovalHolder holder = new LoanApprovalHolder();
    StatefulKnowledgeSession session = sessionLookup
        .newSession();
    try {
      Map<String, Object> parameterMap = 
        new HashMap<String, Object>();
      parameterMap.put("loanSourceAccount",loanSourceAccount);
      parameterMap.put("customer", customer);
      parameterMap.put("loan", loan);
      session.insert(loan);
      session.insert(customer);
      ProcessInstance processInstance = session.startProcess(
          "loanApproval", parameterMap);

      holder.setCustomer(customer);
      holder.setSessionId(session.getId());
      holder.setProcessInstanceId(processInstance.getId());
      em.persist(holder);
      
      //approveLoanHandler.addSessionId(
      //  holder.getProcessInstanceId(), holder.getSessionId());

      // session.insert(processInstance);
      session.fireAllRules();
    } finally {
      session.dispose();
    }
    return holder;
  }

  public LoanApprovalServiceImpl() {
    loanSourceAccount = new Account();
  }

  //session.execute(new DebugCommand());
  
  // @extract-start 08 13
  public void approveLoan(LoanApprovalHolder holder) {
    StatefulKnowledgeSession session =
      sessionLookup.loadSession(holder.getSessionId());
    try {
      SignalEventCommand command = new SignalEventCommand();
      command.setProcessInstanceId(
        holder.getProcessInstanceId());
      command.setEventType("LoanApprovedEvent");
      command.setEvent(true);
      session.execute(command);
    } finally {
      session.dispose();
    }
  }
  // @extract-end
  
  public void setSessionLookup(
      KnowledgeSessionLookup sessionLookup) {
    this.sessionLookup = sessionLookup;
  }
  
  //public void setApproveLoanHandler(
  //    JPAWSHumanTaskHandler approveLoanHandler) {
  //  this.approveLoanHandler = approveLoanHandler;
  //}

}
