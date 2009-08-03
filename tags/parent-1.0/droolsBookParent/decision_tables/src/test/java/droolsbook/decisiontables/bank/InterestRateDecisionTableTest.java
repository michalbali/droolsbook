package droolsbook.decisiontables.bank;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DecisionTableFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.StatelessKnowledgeSession;
import org.joda.time.DateMidnight;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import droolsbook.decisiontables.bank.model.Account;

public class InterestRateDecisionTableTest {

  // @extract-start 04 36
  static StatelessKnowledgeSession session;
  Account account;
  static DateMidnight DATE;

  @BeforeClass
  public static void setUpClass() throws Exception {
    KnowledgeBase knowledgeBase = 
      createKnowledgeBaseFromSpreadsheet();
    session = knowledgeBase.newStatelessKnowledgeSession();
    DATE = new DateMidnight(2008, 1, 1);
  }

  @Before
  public void setUp() throws Exception {    
    account = new Account();
  }
  // @extract-end

  // @extract-start 04 39
  private static KnowledgeBase createKnowledgeBaseFromSpreadsheet()
      throws Exception {
    DecisionTableConfiguration dtconf =KnowledgeBuilderFactory
      .newDecisionTableConfiguration();
    dtconf.setInputType( DecisionTableInputType.XLS );
    //dtconf.setInputType( DecisionTableInputType.CSV );
    
    KnowledgeBuilder knowledgeBuilder = 
      KnowledgeBuilderFactory.newKnowledgeBuilder();    
    knowledgeBuilder.add(ResourceFactory.newClassPathResource(
        "interest calculation.xls"), ResourceType.DTABLE, 
        dtconf);
    //knowledgeBuilder.add(ResourceFactory
    //  .newClassPathResource("interest calculation.csv"), 
    //  ResourceType.DTABLE, dtconf);
    
    if (knowledgeBuilder.hasErrors()) {
      throw new RuntimeException(knowledgeBuilder.getErrors()
          .toString());
    }
    
    KnowledgeBase knowledgeBase = KnowledgeBaseFactory
      .newKnowledgeBase();
    knowledgeBase.addKnowledgePackages(
        knowledgeBuilder.getKnowledgePackages());
    return knowledgeBase;
  }
  // @extract-end

//todo note that the xls is behind the scenes converted into a drl; it is done by the 
//  DecisiontableFactory.loadFromInputStream method; 
//  you can use this drl and examine its contents if it looks as you're expecting
  private void method(DecisionTableConfiguration dtconf) throws Exception {
    // @extract-start 04 75
    String drlString = DecisionTableFactory
        .loadFromInputStream(ResourceFactory
            .newClassPathResource("interest calculation.xls")
            .getInputStream(), dtconf);
    // @extract-end
    System.out.println(drlString);

  }
  
  // @extract-start 04 37
  @Test
  public void deposit125EURfor40Days() throws Exception {
    account.setType(Account.Type.SAVINGS);
    account.setBalance(new BigDecimal("125.00"));
    account.setCurrency("EUR");
    account.setStartDate(DATE.minusDays(40));
    account.setEndDate(DATE);

    session.execute(account);

    assertEquals(new BigDecimal("3.00"), account
        .getInterestRate());
  }
  // @extract-end

  @Test
  public void deposit20USDfor10Days() throws Exception {
    account.setType(Account.Type.SAVINGS);
    account.setBalance(new BigDecimal("20.00"));
    account.setCurrency("USD");
    account.setStartDate(DATE);
    account.setEndDate(DATE.plusDays(10));

    session.execute(account);

    assertEquals(BigDecimal.ZERO.setScale(2), account
        .getInterestRate());
  }

  @Test
  public void deposit9000EURfor11months() throws Exception {
    account.setType(Account.Type.SAVINGS);
    account.setBalance(new BigDecimal("9000.00"));
    account.setCurrency("EUR");
    account.setStartDate(DATE.minusMonths(11));
    account.setEndDate(DATE);

    session.execute(account);

    assertEquals(new BigDecimal("3.75"), account
        .getInterestRate());
  }

  @Test
  public void noInterestRate() throws Exception {
    account.setType(Account.Type.SAVINGS);
    account.setBalance(new BigDecimal("1000000.00"));
    account.setCurrency("EUR");
    account.setStartDate(DATE.minusMonths(12));
    account.setEndDate(DATE);

    session.execute(account);

    assertNull(account.getInterestRate());
  }

  // @extract-start 04 38
  @Test
  public void defaultTransactionalRate() throws Exception {
    account.setType(Account.Type.TRANSACTIONAL);
    account.setCurrency("EUR");

    session.execute(account);

    assertEquals(new BigDecimal("0.01"), account
        .getInterestRate());
  }
  // @extract-end

  @Test
  public void defaultStudentRate() throws Exception {
    account.setType(Account.Type.STUDENT);
    account.setBalance(new BigDecimal("150.00"));
    account.setCurrency("EUR");

    session.execute(account);

    assertEquals(new BigDecimal("1.00"), account
        .getInterestRate());
  }

}
