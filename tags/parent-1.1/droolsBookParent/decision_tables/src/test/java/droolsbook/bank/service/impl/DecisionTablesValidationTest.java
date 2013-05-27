package droolsbook.bank.service.impl;

import java.io.StringReader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

import droolsbook.bank.service.BankingInquiryService;

/**
 * @author miba
 *
 */
public class DecisionTablesValidationTest extends ValidationTest {

  static KnowledgeBase knowledgeBase;
  static BankingInquiryService inquiryService;
  
  @BeforeClass
  public static void setUpClass() throws Exception {
    knowledgeBase = createKnowledgeBaseFromSpreadsheet();
    inquiryService = new BankingInquiryServiceImpl();
    reportFactory = new DefaultReportFactory();
    
    session = knowledgeBase.newStatelessKnowledgeSession();
    session.setGlobal("reportFactory", reportFactory);
    session.setGlobal("inquiryService", inquiryService);
  }

  private static KnowledgeBase createKnowledgeBaseFromSpreadsheet()
      throws Exception {
    KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
    dtconf.setInputType( DecisionTableInputType.XLS );
    knowledgeBuilder.add(ResourceFactory.newClassPathResource("validation.xls"), ResourceType.DTABLE, dtconf);
    
    if (knowledgeBuilder.hasErrors()) {
      throw new RuntimeException(knowledgeBuilder.getErrors().toString());
    }

    KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
    knowledgeBase.addKnowledgePackages(knowledgeBuilder.getKnowledgePackages());
    
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
