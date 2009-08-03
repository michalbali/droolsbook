package droolsbook.etl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.conf.SequentialOption;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.junit.BeforeClass;
import org.junit.Ignore;

import droolsbook.bank.service.impl.DefaultReportFactory;
import droolsbook.utils.DroolsHelper;
import droolsbook.utils.MyDebugAgendaEventListener;
import droolsbook.utils.MyDebugWorkingMemoryEventListener;

public class DslMockRulesTest extends DataTransformationTest {

  @BeforeClass
  public static void setUpClass() throws Exception {
    knowledgeBase = createKnowledgeBase();
    session = knowledgeBase.newStatelessKnowledgeSession();
    session.setGlobal("legacyService",
        new MockLegacyBankService());

    reportFactory = new DefaultReportFactory();

    session.setGlobal("reportFactory", reportFactory);

    session.addEventListener(new MyDebugWorkingMemoryEventListener());
    session.addEventListener(new MyDebugAgendaEventListener());
  }
  
  private static KnowledgeBase createKnowledgeBase() throws Exception {
    KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    builder.add(ResourceFactory.newClassPathResource("etl-iBatis.dsl"), ResourceType.DSL);
    builder.add(ResourceFactory.newClassPathResource("etl-iBatis.dslr"), ResourceType.DSLR);
    
    KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
    knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());
    return knowledgeBase;
  }
  
  @Ignore("not implemented yet")
  @Override  
  public void currencyConversionToEUR() throws Exception {
  }

}
