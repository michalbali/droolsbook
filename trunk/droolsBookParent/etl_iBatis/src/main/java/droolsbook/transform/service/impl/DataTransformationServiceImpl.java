package droolsbook.transform.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.KnowledgeBase;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.process.command.FireAllRulesCommand;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Address;
import droolsbook.bank.model.Customer;
import droolsbook.bank.service.BankingService;
import droolsbook.bank.service.Message;
import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.Message.Type;
import droolsbook.transform.service.DataTransformationService;
import droolsbook.transform.service.LegacyBankService;
import droolsbook.utils.RuleNameEqualsAgendaFilter;

public class DataTransformationServiceImpl implements DataTransformationService {

  private LegacyBankService legacyBankService;
  private KnowledgeBase knowledgeBase;
  private ReportFactory reportFactory;
  private BankingService bankingService;
  private StatelessKnowledgeSession session;

  public void etl() {
    session = knowledgeBase.newStatelessKnowledgeSession();
    session.setGlobal("legacyService", legacyBankService);
    session.setGlobal("reportFactory", reportFactory);

    for (Object customerMap : legacyBankService
        .findAllCustomers()) {
      processCustomer((Map) customerMap);
    }
  }

  // @extract-start 03 12
  /**
   * transforms customerMap, creates and stores new customer
   */
  protected void processCustomer(Map customerMap) {
    ValidationReport validationReport = reportFactory
        .createValidationReport();

    List<Command<?>> commands = new ArrayList<Command<?>>();
    commands.add(CommandFactory.newSetGlobal(
        "validationReport", validationReport));
    commands.add(CommandFactory.newInsert(customerMap));
    commands.add(new FireAllRulesCommand(
        new RuleNameEqualsAgendaFilter("findAllCustomers")));
    commands.add(CommandFactory.newQuery(
        "address", "getAddressByCustomerId",
        new Object[] { customerMap }));
    commands.add(CommandFactory.newQuery(
        "accounts", "getAccountByCustomerId",
        new Object[] { customerMap }));
    ExecutionResults results = session.execute(
        CommandFactory.newBatchExecution(commands));
    
    if (!validationReport.getMessagesByType(Type.ERROR)
        .isEmpty()) {
      logError(validationReport
          .getMessagesByType(Type.ERROR));
      logWarning(validationReport
          .getMessagesByType(Type.WARNING));
    } else {
      logWarning(validationReport
          .getMessagesByType(Type.WARNING));
      Customer customer = buildCustomer(customerMap,
          results);
      bankingService.add(customer); // runs validation
    }
  }
  // @extract-end

  protected Customer buildCustomer(Map customerMap,
      ExecutionResults results) {
    Customer customer = new Customer();
    customer.setFirstName((String) customerMap
        .get("first_name"));
    customer.setLastName((String) customerMap
        .get("last_name"));
    // ..

    QueryResults addressQueryResults = (QueryResults) 
      results.getValue("address"); 
    if (addressQueryResults.size() > 0) {
      QueryResultsRow addressQueryResult = addressQueryResults
          .iterator().next();
      Map addressMap = (Map) addressQueryResult
          .get("$addressMap");

      Address address = new Address();
      address.setAddressLine1((String) addressMap
          .get("street"));
      address.setCountry((Address.Country) addressMap
          .get("country"));
      customer.setAddress(address);
    }

    // @extract-start 03 13
    QueryResults accountQueryResults = (QueryResults) 
      results.getValue("accounts");
    for (QueryResultsRow accountQueryResult : 
      accountQueryResults) {
      Map accountMap = (Map) accountQueryResult
          .get("$accountMap");

      Account account = new Account();
      account.setNumber((Long) accountMap.get("number"));
      account.setBalance((BigDecimal) accountMap
          .get("balance"));
      //..
      customer.addAccount(account);
      // @extract-end
    }
    return customer;
  }

  protected void logError(Set<Message> errors) {

  }

  protected void logWarning(Set<Message> warnings) {

  }

  public void setLegacyBankService(
      LegacyBankService legacyBankService) {
    this.legacyBankService = legacyBankService;
  }

  public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
    this.knowledgeBase = knowledgeBase;
  }

  public void setReportFactory(ReportFactory reportFactory) {
    this.reportFactory = reportFactory;
  }

  public void setBankingService(BankingService bankingService) {
    this.bankingService = bankingService;
  }

}
