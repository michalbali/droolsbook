package droolsbook.integration.spring;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import org.drools.KnowledgeBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class KnowledgeBaseFactoryBeanTest {

  @Before
  public void setUp() throws Exception {
  }
  
  @Test
  public void simple() throws Exception {
    ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext_kbf_1.xml");
    KnowledgeBase base = (KnowledgeBase) applicationContext.getBean("validationKnowledge");
    assertNotNull(base);
    assertNotNull(base.newStatefulKnowledgeSession());
    assertSame(base, applicationContext.getBean("validationKnowledge"));
    
    KnowledgeBase base2 = (KnowledgeBase) applicationContext.getBean("multipleKnowledge");
    assertNotNull(base2);
    assertNotNull(base2.newStatefulKnowledgeSession());
    
    assertNotSame(base, base2);
  }
  
}
