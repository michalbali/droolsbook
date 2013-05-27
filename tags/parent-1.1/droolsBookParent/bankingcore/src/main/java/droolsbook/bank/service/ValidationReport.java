/**
 * 
 */
package droolsbook.bank.service;

import java.util.Set;

// @extract-start 03 52
/**
 * represents the result of the validation process
 */
public interface ValidationReport {
  /**
   * @return all messages in this report
   */
  Set<Message> getMessages();

  /**
   * @return all messages of specified type in this report
   */
  Set<Message> getMessagesByType(Message.Type type);

  /**
   * @return true if this report contains message with 
   *  specified key, false otherwise
   */
  boolean contains(String messageKey);
  
  /**
   * adds specified message to this report
   */
  boolean addMessage(Message message);
}
// @extract-end
