package droolsbook.bank.service.impl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.conf.SequentialOption;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.junit.BeforeClass;
import org.junit.Ignore;

import droolsbook.bank.service.BankingInquiryService;
import droolsbook.bank.service.impl.ValidationTest;

public class DslValidationTest extends ValidationTest {

  @BeforeClass
  public static void setUpClass() throws Exception {
    KnowledgeBase knowledgeBase = createSequentialKnowledgeBase();

    BankingInquiryService inquiryService = new BankingInquiryServiceImpl();
    reportFactory = new DefaultReportFactory();

    session = knowledgeBase.newStatelessKnowledgeSession();
    session.setGlobal("reportFactory", reportFactory);
    session.setGlobal("inquiryService", inquiryService);
  }

  static KnowledgeBase createSequentialKnowledgeBase()
      throws DroolsParserException, IOException, Exception {
    KnowledgeBuilder builder = KnowledgeBuilderFactory
        .newKnowledgeBuilder();
    builder.add(ResourceFactory
        .newClassPathResource("validation.dsl"),
        ResourceType.DSL);
    builder.add(ResourceFactory
        .newClassPathResource("validation.dslr"),
        ResourceType.DSLR);

    KnowledgeBaseConfiguration configuration = KnowledgeBaseFactory
        .newKnowledgeBaseConfiguration();
    configuration.setOption(SequentialOption.YES);

    KnowledgeBase knowledgeBase = KnowledgeBaseFactory
        .newKnowledgeBase(configuration);
    knowledgeBase.addKnowledgePackages(builder
        .getKnowledgePackages());
    return knowledgeBase;
  }

  @Ignore("Not implemented yet")
  @Override
  public void accountBalanceAtLeast() throws Exception {
  }

  @Ignore("Not implemented yet")
  @Override
  public void studentAccountCustomerAgeLessThan()
      throws Exception {
  }

  @Ignore("Not implemented yet")
  @Override
  public void accountNumberUnique() throws Exception {
  }

}
