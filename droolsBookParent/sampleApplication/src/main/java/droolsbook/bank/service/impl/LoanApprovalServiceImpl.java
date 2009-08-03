package droolsbook.bank.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.persistence.session.SingleSessionCommandService;
import org.drools.process.command.Command;
import org.drools.process.command.RegisterWorkItemHandlerCommand;
import org.drools.process.command.SignalEventCommand;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionTemplate;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.model.LoanApprovalHolder;
import droolsbook.bank.service.LoanApprovalService;
import droolsbook.org.drools.persistence.KnowledgeSessionLookup;
import droolsbook.sampleApplication.drools.persistence.JPAWSHumanTaskHandler;

@Repository
public class LoanApprovalServiceImpl implements
    LoanApprovalService {
  
  private KnowledgeSessionLookup sessionLookup;

  private JPAWSHumanTaskHandler approveLoanHandler;
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
      
      approveLoanHandler.addSessionId(
        holder.getProcessInstanceId(), holder.getSessionId());

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
  
  public void setApproveLoanHandler(
      JPAWSHumanTaskHandler approveLoanHandler) {
    this.approveLoanHandler = approveLoanHandler;
  }

}
