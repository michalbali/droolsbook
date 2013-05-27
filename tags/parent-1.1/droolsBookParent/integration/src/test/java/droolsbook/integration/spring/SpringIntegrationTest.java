package droolsbook.integration.spring;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.drools.KnowledgeBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import droolsbook.bank.service.ValidationReport;

@ContextConfiguration
public class SpringIntegrationTest extends AbstractJUnit4SpringContextTests {

  @Autowired
  private KnowledgeBase base; 
  
  @Autowired
  private StatefulKnowledgeSession session;  
  
  @Autowired
  private StatefulKnowledgeSession session2;
  
  @Before
  public void setUp() throws Exception {
  }
  
  @Test
  public void simple() throws Exception {
    assertNotNull(base);
    assertNotNull(base.newStatefulKnowledgeSession());
    
    assertNotNull(session);
    for (Object obj : session.getObjects()) {
      System.out.println(obj);
    }
    ValidationReport report = (ValidationReport) session.getGlobal("validationReport");
    System.out.println(report.getMessages());
    
    assertSame(session, session2);
  }
  
}
