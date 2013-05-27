package droolsbook.bank.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.conf.SequentialOption;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Address;
import droolsbook.bank.model.Customer;
import droolsbook.bank.service.BankingInquiryService;
import droolsbook.bank.service.Message;
import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.ValidationReport;

// @extract-start 03 67
public class ValidationTest {
  static StatelessKnowledgeSession session;
  static ReportFactory reportFactory;  
  
  @BeforeClass
  public static void setUpClass() throws Exception {
    KnowledgeBuilder builder = KnowledgeBuilderFactory
        .newKnowledgeBuilder();
    builder.add(ResourceFactory.newClassPathResource(
        "validation.drl"), ResourceType.DRL);
    if (builder.hasErrors()) {
      throw new RuntimeException(builder.getErrors()
          .toString());
    }
    
    KnowledgeBaseConfiguration configuration = 
      KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
    configuration.setOption(SequentialOption.YES);

    KnowledgeBase knowledgeBase = KnowledgeBaseFactory
        .newKnowledgeBase(configuration);
    knowledgeBase.addKnowledgePackages(builder
        .getKnowledgePackages());
    
    BankingInquiryService inquiryService = 
         new BankingInquiryServiceImpl();
    reportFactory = new DefaultReportFactory();
    
    session = knowledgeBase.newStatelessKnowledgeSession();
    session.setGlobal("reportFactory", reportFactory);
    session.setGlobal("inquiryService", inquiryService);
  }
  // @extract-end 
  
  @Before
  public void initialize() {
  }

  // @extract-start 03 68
  void assertReportContains(Message.Type type,
      String messageKey,Customer customer,Object... context) {
    ValidationReport report = 
        reportFactory.createValidationReport();
    List<Command> commands = new ArrayList<Command>();
    commands.add(CommandFactory.newSetGlobal(
        "validationReport", report));
    commands.add(CommandFactory
        .newInsertElements(getFacts(customer)));
    session.execute(CommandFactory
        .newBatchExecution(commands));

    assertTrue("Report doesn't contain message [" + messageKey
        + "]", report.contains(messageKey));
    Message message = getMessage(report, messageKey);
    assertEquals(Arrays.asList(context),
        message.getContextOrdered());
  }
  
  private Collection<Object> getFacts(Customer customer) {
    ArrayList<Object> facts = new ArrayList<Object>();
    facts.add(customer);
    facts.add(customer.getAddress());
    facts.addAll(customer.getAccounts());
    return facts;
  }
  // @extract-end

  Message getMessage(ValidationReport report, String key) {
    for (Message message : report.getMessages()) {
      if (message.getMessageKey().equals(key)) {
        return message;
      }
    }
    return null;
  }
  
  void assertNotReportContains(Message.Type type,
      String messageKey, Customer customer) {
    ValidationReport report = 
      reportFactory.createValidationReport();
    session.setGlobal("validationReport", report);
    session.execute(getFacts(customer));

    assertFalse(
        "Report contains message [" + messageKey + "]",
        report.contains(messageKey));
  }

  Customer createCustomerBasic() {
    Customer customer = new Customer();
    Account account = new Account();
    customer.addAccount(account);
    account.setOwner(customer);
    return customer;
  }

  // @extract-start 03 62
  @Test
  public void addressRequired() throws Exception {
    Customer customer = createCustomerBasic();
    assertNull(customer.getAddress());
    assertReportContains(Message.Type.WARNING,
        "addressRequired", customer);

    customer.setAddress(new Address());
    assertNotReportContains(Message.Type.WARNING,
        "addressRequired", customer);
  }

  // @extract-end

  @Test
  public void accountOwnerRequired() throws Exception {
    Customer customer = createCustomerBasic();
    Account account =customer.getAccounts().iterator().next();
    assertNotNull(account.getOwner());
    assertNotReportContains(Message.Type.ERROR,
        "accountOwnerRequired", customer);
  }

  @Test
  public void phoneNumberRequired() throws Exception {
    Customer customer = createCustomerBasic();
    assertNull(customer.getPhoneNumber());
    assertReportContains(Message.Type.ERROR,
        "phoneNumberRequired", customer);

    customer.setPhoneNumber("");
    assertReportContains(Message.Type.ERROR,
        "phoneNumberRequired", customer);

    customer.setPhoneNumber("111 222 333");
    assertNotReportContains(Message.Type.ERROR,
        "phoneNumberRequired", customer);
  }

  // @extract-start 03 69
  @Test
  public void accountBalanceAtLeast() throws Exception {
    Customer customer = createCustomerBasic();
    Account account =customer.getAccounts().iterator().next();
    assertEquals(BigDecimal.ZERO, account.getBalance());
    assertReportContains(Message.Type.WARNING,
        "accountBalanceAtLeast", customer, account);

    account.setBalance(new BigDecimal("54.00"));
    assertReportContains(Message.Type.WARNING,
        "accountBalanceAtLeast", customer, account);

    account.setBalance(new BigDecimal("122.34"));
    assertNotReportContains(Message.Type.WARNING,
        "accountBalanceAtLeast", customer);
  }

  // @extract-end

  // @extract-start 03 70
  @Test
  public void studentAccountCustomerAgeLessThan()
      throws Exception {
    DateMidnight NOW = new DateMidnight();
    Customer customer = createCustomerBasic();
    Account account =customer.getAccounts().iterator().next();
    customer.setDateOfBirth(NOW.minusYears(40).toDate());
    assertEquals(Account.Type.TRANSACTIONAL,
         account.getType());
    assertNotReportContains(Message.Type.ERROR,
        "studentAccountCustomerAgeLessThan", customer);

    account.setType(Account.Type.STUDENT);
    assertReportContains(Message.Type.ERROR,
        "studentAccountCustomerAgeLessThan",customer,account);

    customer.setDateOfBirth(NOW.minusYears(20).toDate());
    assertNotReportContains(Message.Type.ERROR,
        "studentAccountCustomerAgeLessThan", customer);
  }

  // @extract-end

  // @extract-start 03 71
  @Test
  public void accountNumberUnique() throws Exception {
    Customer customer = createCustomerBasic();
    Account account = customer.getAccounts().iterator()
        .next();
    session.setGlobal("inquiryService",
        new BankingInquiryServiceImpl() {
          @Override
          public boolean isAccountNumberUnique(
              Account accout) {
            return false;
          }
        });
    assertReportContains(Message.Type.ERROR,
        "accountNumberUnique", customer, account);
  }
  // @extract-end

}
