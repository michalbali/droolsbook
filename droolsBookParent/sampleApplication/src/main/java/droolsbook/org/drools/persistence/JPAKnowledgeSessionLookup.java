package droolsbook.org.drools.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;

import org.drools.KnowledgeBase;
import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

// @extract-start 08 11
/**
 * works with persistable knowledge sessions
 */
public class JPAKnowledgeSessionLookup implements
    KnowledgeSessionLookup {

  @PersistenceUnit//(unitName="droolsEntityManagerFactory")
  private EntityManagerFactory emf;

  private KnowledgeBase knowledgeBase;
  private Environment environment;
  
  private WorkItemHandler emailHandler;
  private WorkItemHandler transferFundsHandler;
  private WorkItemHandler humanTaskHandler;  

  public void init() {
    environment = EnvironmentFactory.newEnvironment();
    environment.set(EnvironmentName.ENTITY_MANAGER_FACTORY,
        emf);
    environment.set(
        EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
        new ObjectMarshallingStrategy[] { MarshallerFactory
            .newSerializeMarshallingStrategy() });
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
        humanTaskHandler);
    manager.registerWorkItemHandler("Email", emailHandler);
    manager.registerWorkItemHandler("Transfer Funds", 
        transferFundsHandler);
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
  
  public void setHumanTaskHandler(
      WorkItemHandler humanTaskHandler) {
    this.humanTaskHandler = humanTaskHandler;
  }

}
