package droolsbook.stateful.utils;

import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.RuleContext;
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
   * @param kcontext RuleContext that is accessible from
   *  rule condition
   * @param context for the message
   */
  public static void error(RuleContext kcontext,
      Object... context) {
    KnowledgeRuntime knowledgeRuntime = kcontext
        .getKnowledgeRuntime();
    ReportFactory reportFactory = (ReportFactory) 
        knowledgeRuntime.getGlobal("reportFactory");

    kcontext.insertLogical(reportFactory.createMessage(
        Message.Type.ERROR, kcontext.getRule().getName(),
        context));
  }
  // @extract-end

  public static void warning(RuleContext kcontext,
      Object... context) {
    KnowledgeRuntime knowledgeRuntime = kcontext
        .getKnowledgeRuntime();
    ReportFactory reportFactory = (ReportFactory) knowledgeRuntime
        .getGlobal("reportFactory");

    kcontext.insertLogical(reportFactory.createMessage(
        Message.Type.WARNING, kcontext.getRule().getName(),
        context));
  }

}
