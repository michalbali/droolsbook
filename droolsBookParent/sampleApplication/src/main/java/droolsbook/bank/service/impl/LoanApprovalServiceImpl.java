package droolsbook.bank.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

  private Account loanSourceAccount;
  
  @PersistenceContext
  EntityManager em;
  
  //session.execute(new DebugCommand());

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
      TransactionSynchronizationManager.registerSynchronization(new SessionCleanupTransactionSynchronisation(session, "requestLoan"));
    }
    return holder;
  }
  // @extract-end

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
      TransactionSynchronizationManager.registerSynchronization(new SessionCleanupTransactionSynchronisation(session, "approveLoan"));
    }
  }
  // @extract-end
  
  public void setSessionLookup(
      KnowledgeSessionLookup sessionLookup) {
    this.sessionLookup = sessionLookup;
  }
  
  public static class SessionCleanupTransactionSynchronisation extends TransactionSynchronizationAdapter {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private StatefulKnowledgeSession session;
    private String sourceName;
    public SessionCleanupTransactionSynchronisation(
        StatefulKnowledgeSession session, String sourceName) {
      this.session = session;
      this.sourceName = sourceName;
    }
    @Override
    public void afterCompletion(int status) {
      //note: not interested in rollback
      logger.debug("disposing session: {}, sourceName: {}", session, sourceName);
      session.dispose();
      
      //TODO: unregister?
    }
    
  }

}
