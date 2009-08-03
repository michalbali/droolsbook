package droolsbook.dsl.simple;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatelessSession;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

import droolsbook.bank.model.Customer;

public class SimpleDSLTest {

  StatelessKnowledgeSession session;
  
  @Before
  public void setUp() throws Exception {
    KnowledgeBase knowledgeBase = createKnowledgeBaseFromDSL();
    session = knowledgeBase.newStatelessKnowledgeSession();
  }

  // @extract-start 04 02
  private KnowledgeBase createKnowledgeBaseFromDSL()
      throws Exception {
    KnowledgeBuilder builder = 
        KnowledgeBuilderFactory.newKnowledgeBuilder();
    builder.add(ResourceFactory.newClassPathResource(
        "simple.dsl"), ResourceType.DSL);
    builder.add(ResourceFactory.newClassPathResource(
        "simple.dslr"), ResourceType.DSLR);
    if (builder.hasErrors()) {
      throw new RuntimeException(builder.getErrors()
          .toString());
    }    
    
    KnowledgeBase knowledgeBase = KnowledgeBaseFactory
        .newKnowledgeBase();
    knowledgeBase.addKnowledgePackages(
        builder.getKnowledgePackages());
    return knowledgeBase;
  }
  // @extract-end
  
  @Test
  public void simple() throws Exception {
    Customer customer = new Customer();
    customer.setFirstName("Daaa");
    session.execute(customer);
    
    //verify that there is no message
    
    customer.setFirstName("David");
    session.execute(customer);
    
    //verify that there is message
  }
  
}
