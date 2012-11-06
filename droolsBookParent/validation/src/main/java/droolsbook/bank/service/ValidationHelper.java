/**
 * 
 */
package droolsbook.bank.service;

import java.util.Date;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.RuleContext;
import org.joda.time.DateMidnight;
import org.joda.time.Years;

/**
 * @author miba
 * 
 */
// @extract-start 03 23
public class ValidationHelper {

  /**
   * adds an error message to the global validation report
   * @param kcontext RuleContext that is accessible from
   *  rule condition
   * @param context for the message
   */
  public static void error(RuleContext kcontext,
      Object... context) {
    KnowledgeRuntime knowledgeRuntime = kcontext
        .getKnowledgeRuntime();
    ValidationReport validationReport = (ValidationReport) 
        knowledgeRuntime.getGlobal("validationReport");
    ReportFactory reportFactory = (ReportFactory) 
        knowledgeRuntime.getGlobal("reportFactory");

    validationReport.addMessage(reportFactory.createMessage(
        Message.Type.ERROR, kcontext.getRule().getName(),
        context));
  }
  // @extract-end
  
  public static void warning(RuleContext kcontext,
      Object... context) {
    KnowledgeRuntime knowledgeRuntime = kcontext.getKnowledgeRuntime();
    ValidationReport validationReport = (ValidationReport) knowledgeRuntime
        .getGlobal("validationReport");
    ReportFactory reportFactory = (ReportFactory) knowledgeRuntime
        .getGlobal("reportFactory");

    validationReport.addMessage(reportFactory.createMessage(
        Message.Type.WARNING, kcontext.getRule().getName(),
        context));
  }

  // @extract-start 03 64
  /**
   * @return number of years between today and specified date 
   */
  public static int yearsPassedSince(Date date) {
    return Years.yearsBetween(new DateMidnight(date),
        new DateMidnight()).getYears();
  }
  // @extract-end

}
