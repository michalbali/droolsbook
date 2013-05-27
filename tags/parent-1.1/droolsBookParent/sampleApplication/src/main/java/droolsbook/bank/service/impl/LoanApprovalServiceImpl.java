package droolsbook.bank.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.drools.command.runtime.process.SignalEventCommand;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.springframework.stereotype.Repository;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.model.LoanApprovalHolder;
import droolsbook.bank.service.LoanApprovalService;
import droolsbook.org.drools.persistence.JPAKnowledgeSessionTemplate;
import droolsbook.org.drools.persistence.KnowledgeSessionCallback;

@Repository
public class LoanApprovalServiceImpl implements
    LoanApprovalService {
  
  private JPAKnowledgeSessionTemplate sessionTemplate;

  private Account loanSourceAccount;
  
  private TaskService localTaskService;
  
  @PersistenceContext
  EntityManager em;
  
  //session.execute(new DebugCommand());

  // @extract-start 08 14
  public LoanApprovalHolder requestLoan(final Loan loan,
      final Customer customer) {
    final LoanApprovalHolder holder =new LoanApprovalHolder();
    sessionTemplate
      .doWithNewSession(new KnowledgeSessionCallback() {
        @Override
        public void execute(StatefulKnowledgeSession session){
          Map<String, Object> parameterMap = 
              new HashMap<String, Object>();
          parameterMap.put("loanSourceAccount",
              loanSourceAccount);
          parameterMap.put("customer", customer);
          parameterMap.put("loan", loan);
          session.insert(loan);
          session.insert(customer);
          ProcessInstance processInstance = session
              .startProcess("loanApproval", parameterMap);

          holder.setCustomer(customer);
          holder.setSessionId(session.getId());
          holder.setProcessInstanceId(processInstance
              .getId());
          em.persist(holder);

          session.insert(processInstance);
          session.fireAllRules();
        }
      });
    return holder;
  }
  // @extract-end

  public LoanApprovalServiceImpl() {
    loanSourceAccount = new Account();
  }

  //session.execute(new DebugCommand());
  
  // @extract-start 08 13
  public void approveLoan(final LoanApprovalHolder holder) {
    sessionTemplate.doWithLoadedSession(holder.getSessionId(),
      new KnowledgeSessionCallback() {
        @Override
        public void execute(StatefulKnowledgeSession session){
          SignalEventCommand command=new SignalEventCommand();
          command.setProcessInstanceId(holder
              .getProcessInstanceId());
          command.setEventType("LoanApprovedEvent");
          command.setEvent(true);
          session.execute(command);
        }
      });
  }
  // @extract-end
  
  @Override
  public void claim(long taskId, String userId) {
    localTaskService.claim(taskId, userId);
  }
  
  

  @Override
  public void complete(final long taskId,final String userId){
    Task task = localTaskService.getTask(taskId);
    sessionTemplate.doWithLoadedSession(task.getTaskData()
      .getProcessSessionId(),
      new KnowledgeSessionCallback() {
        @Override
        public void execute(StatefulKnowledgeSession session){
          localTaskService.complete(taskId, userId, null);
        }
      });
  }

  @Override
  public void start(long taskId, String userId) {
    localTaskService.start(taskId, userId);
    
  }
  
  public void setSessionTemplate(
      JPAKnowledgeSessionTemplate sessionTemplate) {
    this.sessionTemplate = sessionTemplate;
  }
  
  public void setLocalTaskService(TaskService localTaskService) {
    this.localTaskService = localTaskService;
  }

}
