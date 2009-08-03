package droolsbook.bank.service;

import java.util.List;

// @extract-start 03 51
/**
 * represents one error/warning validation message
 */
public interface Message {
  public enum Type {
    ERROR, WARNING
  }

  /**
   * @return type of this message
   */
  Type getType();

  /**
   * @return key of this message 
   */
  String getMessageKey();
  
  /**
   * objects in the context must be ordered from the least 
   * specific to most specific
   * @return list of objects in this message's context
   */
  List<Object> getContextOrdered();
}
// @extract-end
