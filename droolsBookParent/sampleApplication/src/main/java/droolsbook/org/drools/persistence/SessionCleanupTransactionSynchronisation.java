package droolsbook.org.drools.persistence;

import org.drools.runtime.StatefulKnowledgeSession;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

public class SessionCleanupTransactionSynchronisation extends
    TransactionSynchronizationAdapter {
  private StatefulKnowledgeSession session;
  private LocalHTWorkItemHandler hTHandler;

  public SessionCleanupTransactionSynchronisation(
      StatefulKnowledgeSession session, LocalHTWorkItemHandler hTHandler) {
    this.session = session;
    this.hTHandler = hTHandler;
  }

  @Override
  public void afterCompletion(int status) {
    // note: not interested in rollback
    try {
      hTHandler.dispose();
    } catch (Exception e) {
      e.printStackTrace();
    }
    session.dispose();
  }
}
// TODO: unregister?