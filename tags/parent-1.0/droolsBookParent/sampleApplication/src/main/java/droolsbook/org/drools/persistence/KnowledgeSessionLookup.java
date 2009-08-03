package droolsbook.org.drools.persistence;

import org.drools.runtime.StatefulKnowledgeSession;

// @extract-start 08 10
/**
 * knows how to create a new or lookup existing knowledge 
 * session
 */
public interface KnowledgeSessionLookup {
  /**
   * creates a new session 
   */
  StatefulKnowledgeSession newSession();
  
  /**
   * loads an existing session 
   */
  StatefulKnowledgeSession loadSession(int sessionId);
}
// @extract-end