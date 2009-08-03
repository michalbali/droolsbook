package droolsbook.stateful.utils;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.spi.KnowledgeHelper;

import droolsbook.bank.service.Message;
import droolsbook.bank.service.ReportFactory;

/**
 * @author miba
 * 
 */
// @extract-start 05 11
public class ValidationHelper {
  /**
   * inserts new logical assertion - a message
   * @param drools KnowledgeHelper that is accessible from
   *  rule condition
   * @param context for the message
   */
  public static void error(KnowledgeHelper drools,
      Object... context) {
    KnowledgeRuntime knowledgeRuntime = drools
        .getKnowledgeRuntime();
    ReportFactory reportFactory = (ReportFactory) 
        knowledgeRuntime.getGlobal("reportFactory");

    drools.insertLogical(reportFactory.createMessage(
        Message.Type.ERROR, drools.getRule().getName(),
        context));
  }
  // @extract-end

  public static void warning(KnowledgeHelper drools,
      Object... context) {
    KnowledgeRuntime knowledgeRuntime = drools
        .getKnowledgeRuntime();
    ReportFactory reportFactory = (ReportFactory) knowledgeRuntime
        .getGlobal("reportFactory");

    drools.insertLogical(reportFactory.createMessage(
        Message.Type.WARNING, drools.getRule().getName(),
        context));
  }

}
