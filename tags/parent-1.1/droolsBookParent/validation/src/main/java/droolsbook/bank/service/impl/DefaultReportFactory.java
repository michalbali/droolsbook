/**
 * 
 */
package droolsbook.bank.service.impl;

import java.util.Arrays;

import droolsbook.bank.service.Message;
import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.Message.Type;

/**
 * @author miba
 * 
 */
// @extract-start 03 55
public class DefaultReportFactory implements ReportFactory {
  public Message createMessage(Message.Type type, 
      String messageKey, Object... context) {
    return new DefaultMessage(type, messageKey, Arrays
        .asList(context));
  }
  
  public ValidationReport createValidationReport() {
    return new DefaultValidationReport();
  }
}
// @extract-end
