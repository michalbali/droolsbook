package droolsbook.etl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.ObjectFilter;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.command.runtime.rule.FireAllRulesCommand;
import org.drools.command.runtime.rule.GetObjectsCommand;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatelessKnowledgeSession;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import droolsbook.transform.service.LegacyBankService;
import droolsbook.utils.DroolsHelper;
import droolsbook.utils.RuleNameEqualsAgendaFilter;

@RunWith(JMock.class)
public class FindMockRulesTest {

  LegacyBankService legacyBankService;
  StatelessKnowledgeSession session;
  Mockery context = new JUnit4Mockery();

  @Before
  public void initialize() throws Exception {
    KnowledgeBase knowledgeBase = DroolsHelper
        .createKnowledgeBase("etl-iBatis.drl");
    session = knowledgeBase.newStatelessKnowledgeSession();
    session.setGlobal("legacyService",
        new MockLegacyBankService());

    //session
    //    .addEventListener(new DebugWorkingMemoryEventListener());
  }

  @Test
  public void findCustomer() throws Exception {
    final Map<String, Object> customerMap = new HashMap<String, Object>();

    final LegacyBankService service = context
        .mock(LegacyBankService.class);
    session.setGlobal("legacyService", service);

    context.checking(new Expectations() {
      {
        one(service).findAllCustomers();
        will(returnValue(Arrays
            .asList(new Object[] { customerMap })));
        one(service).findAddressByCustomerId(null);
        will(returnValue(null));
        one(service).findAccountByCustomerId(null);
        will(returnValue(null));
      }
    });

        
    List<Command<?>> commands = new ArrayList<Command<?>>();
    commands.add(new FireAllRulesCommand(
        new RuleNameEqualsAgendaFilter("findAllCustomers")));
    GetObjectsCommand getObjectsCommand = new GetObjectsCommand(
        new ObjectFilter() {
          public boolean accept(Object object) {
            return object instanceof Map
                && ((Map) object).get("_type_").equals(
                    "Customer");
          }
        });
    getObjectsCommand.setOutIdentifier("objects");
    commands.add(getObjectsCommand);
    ExecutionResults results = session.execute(
        CommandFactory.newBatchExecution(commands));

    assertEquals("Customer", customerMap.get("_type_"));
    Iterator<?> customerIterator = ((List<?>) results.getValue("objects")).iterator();
    assertEquals(customerMap, customerIterator.next());
    assertFalse(customerIterator.hasNext());
  }

}
