package droolsbook.etl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.impl.DefaultReportFactory;
import droolsbook.transform.service.LegacyBankService;
import droolsbook.utils.MyDebugProcessEventListener;

public class ETLRuleFlowTest {

  LegacyBankService legacyBankService;
  ReportFactory reportFactory;
  ValidationReport validationReport;
  
  // @extract-start 04 76
  static KnowledgeBase knowledgeBase;  
  StatefulKnowledgeSession session;

  @BeforeClass
  public static void setUpClass() throws Exception {
    knowledgeBase = createKnowledgeBaseFromRuleFlow();
  }

  @Before
  public void initialize() throws Exception {
    session = knowledgeBase.newStatefulKnowledgeSession();
 // @extract-end
    session.setGlobal("legacyService",
        new MockLegacyBankService());

    reportFactory = new DefaultReportFactory();
    validationReport = reportFactory.createValidationReport();

    session.setGlobal("reportFactory", reportFactory);
    session.setGlobal("validationReport", validationReport);

    session.addEventListener(new MyDebugProcessEventListener());
    session.addEventListener(new DebugAgendaEventListener());
  }

  // @extract-start 04 74
  @After
  public void terminate() {
    session.dispose();
  }
  // @extract-end

  // @extract-start 04 70
  static KnowledgeBase createKnowledgeBaseFromRuleFlow()
      throws Exception {
    KnowledgeBuilder builder = KnowledgeBuilderFactory
      .newKnowledgeBuilder();
    builder.add(ResourceFactory.newClassPathResource(
        "dataTransformation-ruleflow.drl"), ResourceType.DRL);
    builder.add(ResourceFactory.newClassPathResource(
        "dataTransformation.rf"), ResourceType.DRF);
    if (builder.hasErrors()) {
      throw new RuntimeException(builder.getErrors()
          .toString());
    }
    
    KnowledgeBase knowledgeBase = KnowledgeBaseFactory
        .newKnowledgeBase();
    knowledgeBase.addKnowledgePackages(builder
        .getKnowledgePackages());
    return knowledgeBase;
  }
  // @extract-end

  // FIXME refactor this, it is duplicated
  /*
  void reportContextContains(Object object) {
    assertEquals(1, validationReport.getMessages().size());
    List<Object> messageContext = validationReport
        .getMessages().iterator().next().getContextOrdered();
    assertEquals(1, messageContext.size());
    assertSame(object, messageContext.iterator().next());
  }
  */

  // @extract-start 04 72
  @Test
  public void unknownCountryUnknown() throws Exception {
    Map addressMap = new HashMap();
    addressMap.put("_type_", "Address");
    addressMap.put("country", "no country");

    session.insert(addressMap);    
    session.startProcess("dataTransformation");
    session.fireAllRules();

    assertTrue(validationReport.contains("unknownCountry"));
  }
  // @extract-end

  // @extract-start 04 73
  @Test
  public void unknownCountryKnown() throws Exception {
    Map addressMap = new HashMap();
    addressMap.put("_type_", "Address");
    addressMap.put("country", "Ireland");

    session.startProcess("dataTransformation");
    session.insert(addressMap);
    session.fireAllRules();

    assertFalse(validationReport.contains("unknownCountry"));
  }
  // @extract-end
  
}
