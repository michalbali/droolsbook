package droolsbook.org.drools.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.drools.KnowledgeBase;
import org.drools.container.spring.beans.persistence.DroolsSpringTransactionManager;
import org.drools.event.DebugProcessEventListener;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.persistence.JpaProcessPersistenceContext;
import org.jbpm.persistence.MapProcessPersistenceContextManager;
import org.jbpm.task.TaskService;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

// @extract-start 08 11
/**
 * works with persistable knowledge sessions
 */
public class JPAKnowledgeSessionLookup implements
    KnowledgeSessionLookup {

  @PersistenceContext
  private EntityManager em;
  private AbstractPlatformTransactionManager transactionManager;

  private KnowledgeBase knowledgeBase;
  private Environment environment;
  
  private WorkItemHandler emailHandler;
  private WorkItemHandler transferFundsHandler;
  
  private TaskService taskService;

  public void init() {
    environment = EnvironmentFactory.newEnvironment();
    environment.set(EnvironmentName.TRANSACTION_MANAGER,
        new DroolsSpringTransactionManager(transactionManager));
    environment.set(EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
        new MapProcessPersistenceContextManager(new LocalJpaProcessPersistenceContext(em)));
    environment.set(
        EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
        new ObjectMarshallingStrategy[] { MarshallerFactory
            .newSerializeMarshallingStrategy() });
  }
  
  private static class LocalJpaProcessPersistenceContext extends JpaProcessPersistenceContext {

    public LocalJpaProcessPersistenceContext(EntityManager em) {
      super(em);
    }
    
    @Override
    public void joinTransaction() {
      //ignore this call for non JTA environment
    }
  }

  public StatefulKnowledgeSession newSession() {
    StatefulKnowledgeSession session = JPAKnowledgeService
        .newStatefulKnowledgeSession(knowledgeBase, null,
            environment);
    registerWorkItemHandlers(session);
    return session;
  }

  public StatefulKnowledgeSession loadSession(int sessionId) {
    StatefulKnowledgeSession session = JPAKnowledgeService
        .loadStatefulKnowledgeSession(sessionId,
            knowledgeBase, null, environment);
    registerWorkItemHandlers(session);
    return session;
  }

  /**
   * helper method for registering work item handlers 
   * (they are not persisted) 
   */
  private void registerWorkItemHandlers(
      StatefulKnowledgeSession session) {
    WorkItemManager manager = session.getWorkItemManager();
    manager.registerWorkItemHandler("Human Task",
        new CustomLocalHTWorkItemHandler(taskService, session, this));
    manager.registerWorkItemHandler("Email", emailHandler);
    manager.registerWorkItemHandler("Transfer Funds", 
        transferFundsHandler);
    
    session.addEventListener(new DebugProcessEventListener() {
      @Override
      public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
        System.out.println("-->" + event.getNodeInstance().getNodeName());
      }
    });
  }
  // @extract-end

  public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
    this.knowledgeBase = knowledgeBase;
  }
  
  public void setEmailHandler(WorkItemHandler emailHandler) {
    this.emailHandler = emailHandler;
  }
  
  public void setTransferFundsHandler(
      WorkItemHandler transferFundsHandler) {
    this.transferFundsHandler = transferFundsHandler;
  }
  
  public void setTransactionManager(AbstractPlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }
  
  public void setTaskService(TaskService taskService) {
    this.taskService = taskService;
  }

}
