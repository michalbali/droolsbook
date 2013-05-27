package droolsbook.org.drools.persistence;

import org.drools.runtime.StatefulKnowledgeSession;

/**
 * callback interface for knowledge session logic. Used with
 * JPAKnowledgeSessionTemplate's doWith* methods.
 */
public interface KnowledgeSessionCallback {

  /**
   * Gets called by the JPAKnowledgeSessionTemplate with a 
   * persistable session 
   * @param session 
   */
  public void execute(StatefulKnowledgeSession session);

}
