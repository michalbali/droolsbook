package test;

import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class StatelessSessionTest {

  static KnowledgeBase knowledgeBase;
  StatelessKnowledgeSession session;
  TrackingAgendaEventListener trackingAgendaEventListener;

  @BeforeClass
  public static void setUpClass() throws Exception {
    KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
        .newKnowledgeBuilder();
    // knowledgeBuilder.add(ResourceFactory.newClassPathResource("stateless.drl"),
    // ResourceType.DRL);
    knowledgeBuilder.add(ResourceFactory.newReaderResource(new StringReader(
        "package some.test; rule dummyRule when then end ")), ResourceType.DRL);

    if (knowledgeBuilder.hasErrors()) {
      throw new RuntimeException(knowledgeBuilder.getErrors().toString());
    }

    knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
    knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
  }

  @Before
  public void initialize() throws Exception {
    trackingAgendaEventListener = new TrackingAgendaEventListener();

    session = knowledgeBase.newStatelessKnowledgeSession();
    session.addEventListener(trackingAgendaEventListener);
  }

  @Test
  @Ignore("Ignored because this test is just to show a bug in Droolsand is not part of the book's code")
  public void instancesEqualsAlpha() throws Exception {
    trackingAgendaEventListener.reset();
    execute(Collections.emptyList());
    assertTrue(trackingAgendaEventListener.isRuleFired("dummyRule"));

    trackingAgendaEventListener.reset();
    execute(Collections.emptyList());
    assertTrue(trackingAgendaEventListener.isRuleFired("dummyRule"));
  }

  private void execute(List<?> objects) {
     //session.addEventListener(trackingAgendaEventListener); // uncomment this
    // and
    // it will work, created https://issues.jboss.org/browse/JBRULES-3711
    System.out.println("pre " + session.getAgendaEventListeners());
    session.execute(objects);
    System.out.println("post " + session.getAgendaEventListeners());
    System.out.println("--------------------"
        + trackingAgendaEventListener.rulesFiredList);
  }

  private class TrackingAgendaEventListener extends DefaultAgendaEventListener {
    public List<String> rulesFiredList = new ArrayList<String>();

    @Override
    public void afterActivationFired(AfterActivationFiredEvent event) {
      rulesFiredList.add(event.getActivation().getRule().getName());
    }

    public boolean isRuleFired(String ruleName) {
      for (String firedRuleName : rulesFiredList) {
        if (firedRuleName.equals(ruleName)) {
          return true;
        }
      }
      return false;
    }

    public void reset() {
      rulesFiredList.clear();
    }
  }

}
