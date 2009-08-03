package droolsbook.stateful.bank.service.impl;

import droolsbook.bank.service.impl.DefaultValidationReport;

public class StatefulDefaultValidationReport extends
    DefaultValidationReport {

  // @extract-start 05 17
  /**
   * clears this report
   */
  public void reset() {
    messagesMap.clear();
  }
  // @extract-end
  
}
