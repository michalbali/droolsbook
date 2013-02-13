package droolsbook.bank.service.impl;

import org.drools.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;

public class SessionCleanupTransactionSynchronisation extends
    TransactionSynchronizationAdapter {
  protected final Logger logger = LoggerFactory
      .getLogger(getClass());
  private StatefulKnowledgeSession session;
  private String sourceName;

  public SessionCleanupTransactionSynchronisation(
      StatefulKnowledgeSession session, String sourceName) {
    this.session = session;
    this.sourceName = sourceName;
  }

  @Override
  public void afterCompletion(int status) {
    // note: not interested in rollback
    logger.debug("disposing session: {}, sourceName: {}",
        session, sourceName);
    session.dispose();
  }
}
// TODO: unregister?