package droolsbook.evaluator.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.StatelessSession;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.conf.EvaluatorOption;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import droolsbook.bank.model.Customer;
import droolsbook.cep.TrackingAgendaEventListener;
import droolsbook.cep.bank.model.Account;
import droolsbook.evaluator.InstanceEqualsEvaluatorDefinition;
import droolsbook.utils.DroolsHelper;

public class InstanceEqualsEvaluatorDefinitionTest {

  static KnowledgeBase knowledgeBase;
  StatelessKnowledgeSession session;
  TrackingAgendaEventListener trackingAgendaEventListener;

  // @extract-start 06 22
  @BeforeClass
  public static void setUpClass() throws Exception {
    KnowledgeBuilderConfiguration builderConf =
      KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
    builderConf.setOption(EvaluatorOption.get(
        "instanceEquals",
        new InstanceEqualsEvaluatorDefinition()));

    knowledgeBase = DroolsHelper.createKnowledgeBase(null,
        builderConf, "custom_operator.drl");
  }
  // @extract-end

  @Before
  public void initialize() throws Exception {
    trackingAgendaEventListener = new TrackingAgendaEventListener();

    session = knowledgeBase.newStatelessKnowledgeSession();
    session.addEventListener(trackingAgendaEventListener);
  }

  @After
  public void terminate() {

  }

  // @extract-start 06 21
  @Test
  public void instancesEqualsBeta() throws Exception {
    Customer customer = new Customer();
    Account account = new Account();

    session.execute(Arrays.asList(customer, account));
    assertNotFired("accountHasCustomer");

    account.setOwner(new Customer());
    session.execute(Arrays.asList(customer, account));
    assertNotFired("accountHasCustomer");

    account.setOwner(customer);
    session.execute(Arrays.asList(customer, account));
    assertFired("accountHasCustomer");
  }

  // @extract-end

  private void assertNotFired(String ruleName) {
    assertFalse(trackingAgendaEventListener
        .isRuleFired(ruleName));
  }

  private void assertFired(String ruleName) {
    assertTrue(trackingAgendaEventListener
        .isRuleFired(ruleName));
  }

  @Test
  public void instancesEqualsBetaNot() throws Exception {
    Customer customer = new Customer();
    Account account = new Account();

    session.execute(Arrays.asList(customer, account));
    assertFired("accountHasCustomerNot");

    account.setOwner(customer);
    trackingAgendaEventListener.reset();
    session.execute(Arrays.asList(customer, account));
    assertNotFired("accountHasCustomerNot");

    account.setOwner(new Customer());
    session.execute(Arrays.asList(customer, account));
    assertFired("accountHasCustomerNot");
  }

  @Test
  public void instancesEqualsAlpha() throws Exception {
    Customer customer = new Customer();
    CustomersHolder holder = new CustomersHolder();
    holder.setCustomer1(null);
    holder.setCustomer2(null);

    trackingAgendaEventListener.reset();
    session.execute(Arrays.asList(holder));
    assertFired("twoFieldsOnOneFact");
    assertNotFired("twoFieldsOnOneFactNot");

    holder.setCustomer1(customer);
    trackingAgendaEventListener.reset();
    session.execute(Arrays.asList(holder));
    assertNotFired("twoFieldsOnOneFact");
    assertFired("twoFieldsOnOneFactNot");

    holder.setCustomer2(new Customer());
    trackingAgendaEventListener.reset();
    session.execute(Arrays.asList(holder));
    assertNotFired("twoFieldsOnOneFact");
    assertFired("twoFieldsOnOneFactNot");

    holder.setCustomer2(customer);
    trackingAgendaEventListener.reset();
    session.execute(Arrays.asList(holder));
    assertFired("twoFieldsOnOneFact");
    assertNotFired("twoFieldsOnOneFactNot");
  }

}
