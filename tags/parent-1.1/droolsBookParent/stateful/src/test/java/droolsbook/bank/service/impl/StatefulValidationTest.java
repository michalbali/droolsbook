package droolsbook.bank.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.drools.KnowledgeBase;
import org.drools.QueryResult;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.StatelessSessionResult;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.service.BankingInquiryService;
import droolsbook.bank.service.Message;
import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.Message.Type;
import droolsbook.bank.service.impl.BankingInquiryServiceImpl;
import droolsbook.bank.service.impl.DefaultReportFactory;
import droolsbook.bank.service.impl.ValidationTest;
import droolsbook.stateful.bank.service.impl.StatefulDefaultValidationReport;
import droolsbook.utils.DroolsHelper;

/**
 * for testing logical assertions that they don't break the functionality
 * it uses stateless session,
 * 
 * @author miba
 *
 */
public class StatefulValidationTest extends ValidationTest {

  static KnowledgeBase knowledgeBase;
  static BankingInquiryService inquiryService;
  static ValidationReport validationReport;
  
  @BeforeClass
  public static void setUpClass() throws Exception {
    knowledgeBase = DroolsHelper
        .createKnowledgeBase("validation-stateful.drl");
    inquiryService = new BankingInquiryServiceImpl();
    reportFactory = new DefaultReportFactory();
    
    validationReport = new StatefulDefaultValidationReport();
    
    session = knowledgeBase.newStatelessKnowledgeSession();
    session.setGlobal("validationReport",
        validationReport);
    session.setGlobal("reportFactory", reportFactory);
    session
        .setGlobal("inquiryService", inquiryService);
  }

  @After
  public void terminate() {
  }

  ValidationReport validate(Customer customer) {
    
    session.execute(getFacts(customer));
    
    /*
    //create new jsut to be sure
    validationReport = new StatefulDefaultValidationReport();
    
    StatelessSessionResult result = session.executeWithResults(getFacts(customer));    
    QueryResults queryResults = result.getQueryResults("getAllMessages");
    for (Iterator queryResultsIter = queryResults.iterator(); queryResultsIter
        .hasNext();) {
      QueryResult queryResult = (QueryResult) queryResultsIter.next();
      Message message = (Message) queryResult.get("$message");
      validationReport.addMessage(message);
    }
    */
    
    return validationReport;
  }
  
  Collection<Object> getFacts(Customer customer) {
    ArrayList<Object> facts = new ArrayList<Object>();
    facts.add(customer);
    facts.add(customer.getAddress());
    facts.addAll(customer.getAccounts());
    return facts;
  }

  @Override
  void assertReportContains(Message.Type type,
      String messageKey, Customer customer, Object... context) {
    ValidationReport validationReport = validate(customer);

    assertTrue("Report doesn't contain message [" + messageKey
        + "]", validationReport.contains(messageKey));
    Message message = getMessage(validationReport, messageKey);
    assertEquals(Arrays.asList(context), 
        message.getContextOrdered());
  }

  @Override
  void assertNotReportContains(Message.Type type, String messageKey,
      Customer customer) {
    ValidationReport validationReport = validate(customer);    

    assertFalse(
        "Report contains message [" + messageKey + "]",
        validationReport.contains(messageKey));
  }

}
