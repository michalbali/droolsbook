/**
 * 
 */
package droolsbook.bank.service.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import droolsbook.bank.service.Message;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.Message.Type;

/**
 * @author miba
 * 
 */
// @extract-start 03 56
public class DefaultValidationReport implements
    ValidationReport, Serializable {
  protected Map<Message.Type, Set<Message>> messagesMap = 
    new HashMap<Message.Type, Set<Message>>();

  public Set<Message> getMessages() {
    Set<Message> messagesAll = new HashSet<Message>();
    for (Collection<Message> messages : messagesMap.values()){
      messagesAll.addAll(messages);
    }
    return messagesAll;
  }

  public Set<Message> getMessagesByType(Message.Type type) {
    if (type == null)
      return Collections.emptySet();
    Set<Message> messages = messagesMap.get(type);
    if (messages == null)
      return Collections.emptySet();
    else
      return messages;
  }

  public boolean contains(String messageKey) {
    for (Message message : getMessages()) {
      if (messageKey.equals(message.getMessageKey())) {
        return true;
      }
    }
    return false;
  }

  public boolean addMessage(Message message) {
    if (message == null)
      return false;
    Set<Message> messages =messagesMap.get(message.getType());
    if (messages == null) {
      messages = new HashSet<Message>();
      messagesMap.put(message.getType(), messages);
    }
    return messages.add(message);
  }
}
// @extract-end
