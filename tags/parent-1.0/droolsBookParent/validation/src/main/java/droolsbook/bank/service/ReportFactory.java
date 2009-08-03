/**
 * 
 */
package droolsbook.bank.service;


/**
 * @author miba
 * 
 */
// @extract-start 03 53
public interface ReportFactory {
  ValidationReport createValidationReport();

  Message createMessage(Message.Type type, String messageKey, 
      Object... context);
}
// @extract-end
