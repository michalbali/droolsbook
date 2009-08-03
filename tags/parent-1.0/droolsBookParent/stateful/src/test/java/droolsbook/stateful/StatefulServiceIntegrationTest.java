package droolsbook.stateful;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.KnowledgeBase;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Address;
import droolsbook.bank.model.Customer;
import droolsbook.bank.service.BankingInquiryService;
import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.impl.BankingInquiryServiceImpl;
import droolsbook.bank.service.impl.DefaultReportFactory;
import droolsbook.stateful.impl.StatefulServiceImpl;
import droolsbook.utils.DroolsHelper;

//knowledgeBase = DroolsHelper.createKnowledgeBase("validation-stateful-nologicassert.drl");

// @extract-start 05 06
public class StatefulServiceIntegrationTest {
  StatefulServiceImpl statefulService;
  static KnowledgeBase knowledgeBase;

  @BeforeClass
  public static void setUpClass() throws Exception {
    knowledgeBase = DroolsHelper.createKnowledgeBase(
        "validation-stateful.drl");
  }

  @Before
  public void initialize() throws Exception {
    ReportFactory reportFactory = new DefaultReportFactory();
    BankingInquiryService inquiryService = 
        new BankingInquiryServiceImpl();

    statefulService = new StatefulServiceImpl(knowledgeBase,
        reportFactory, inquiryService);
  }

  @After
  public void terminate() {
    statefulService.terminate();
  }
  // @extract-end
  
  // @extract-start 05 07
  private Customer createValidCustomer() {
    Customer customer = new Customer();
    customer.setPhoneNumber("123 456 789");
    customer.setAddress(new Address());
    
    statefulService.insertOrUpdateRecursive(customer);
    ValidationReport report = statefulService.executeRules();
    assertEquals(0, report.getMessages().size());
    return customer;
  }
  // @extract-end

  /**
   * we want all messages every time, because imagine you are entering a form,
   * you submit it the stateful service validates it and sends back errors, lets
   * say you fix some of them and submits the form again, you want to see all
   * errors not only those associated to facts that were modified
   */ 
  @Test
  public void accountUpdatesPartial() throws Exception {
    Customer customer = createValidCustomer();
    statefulService.insertOrUpdateRecursive(customer);
        
    ValidationReport report = statefulService.executeRules();
    assertEquals(0, report.getMessages().size());
    
    Account account = new Account();
    account.setOwner(customer);
    customer.addAccount(account);
    statefulService.insertOrUpdate(customer);
    statefulService.insertOrUpdate(account);
    report = statefulService.executeRules();    
    assertEquals(2, report.getMessages().size());
    assertTrue(report.contains("accountNumberUnique"));
    assertTrue(report.contains("accountBalanceAtLeast"));    

    // modify account
    account.setOwner(null);
    statefulService.insertOrUpdate(account);
    report = statefulService.executeRules();
    
    assertEquals(
        "Report should contain all account related rules", 3,
        report.getMessages().size());
    assertTrue(report.contains("accountNumberUnique"));
    assertTrue(report.contains("accountOwnerRequired"));
    assertTrue(report.contains("accountBalanceAtLeast"));
  }
  
  // @extract-start 05 08
  @Test
  public void statefulValidation() throws Exception {
    Customer customer = createValidCustomer();

    customer.setPhoneNumber("");
    statefulService.insertOrUpdate(customer);
    ValidationReport report = statefulService.executeRules();
    assertEquals(1, report.getMessages().size());
    assertTrue(report.contains("phoneNumberRequired"));
    // @extract-end
    
    //customer.setPhoneNumber("147 258 369");
    //statefulService.insertOrUpdate(customer);
    
    // @extract-start 05 09
    Account account = new Account();
    account.setOwner(customer);
    customer.addAccount(account);
    statefulService.insertOrUpdate(customer);
    statefulService.insertOrUpdate(account);
    report = statefulService.executeRules();
    assertEquals(3, report.getMessages().size());
    assertTrue(report.contains("accountNumberUnique"));
    assertTrue(report.contains("accountBalanceAtLeast"));    
    assertTrue(report.contains("phoneNumberRequired"));
    // @extract-end
    
    // @extract-start 05 10
    account.setOwner(null);
    statefulService.insertOrUpdate(account);
    report = statefulService.executeRules();
    assertEquals(4, report.getMessages().size());
    assertTrue(report.contains("accountNumberUnique"));
    assertTrue(report.contains("accountOwnerRequired"));
    assertTrue(report.contains("accountBalanceAtLeast"));
    assertTrue(report.contains("phoneNumberRequired"));    
  }
  // @extract-end
  
  //TODO maybe some mock tests which will verify that the correct method is being called insert/update
  
  // @extract-start 05 16
  @Test
  public void testSerialization() throws Exception {
    Customer customer = createValidCustomer();    
    statefulService.insertOrUpdateRecursive(customer);
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baos);
    out.writeObject(statefulService);
    out.close();
    
    byte[] bArray = baos.toByteArray();
    ObjectInputStream in = new ObjectInputStream(
        new ByteArrayInputStream(bArray));
    statefulService = (StatefulServiceImpl) in.readObject();
    in.close();
    statefulService.insertOrUpdateRecursive(customer);
    
    ValidationReport report = statefulService.executeRules();
    assertEquals(0, report.getMessages().size());

    customer.setPhoneNumber(null);    
    statefulService.insertOrUpdate(customer);
    report = statefulService.executeRules();
    assertEquals(1, report.getMessages().size());
    assertTrue(report.contains("phoneNumberRequired"));
  }
  // @extract-end
  
  // the following test is broken, a JIRA has been filed
  // (https://jira.jboss.org/jira/browse/JBRULES-2048), Drools modify object
  // references even though they are stored to the same object output stream
  ///*
  @Test
  public void testSerialization2() throws Exception {
    Customer customer = createValidCustomer();    
    statefulService.insertOrUpdateRecursive(customer);
    
    //List<Object> childList = new ArrayList<Object>();
    //childList.add(customer);
    //childList.add(new ArrayList<Object>());
    //childList.add(customer);
    
    List<Object> list = new ArrayList<Object>();    
    list.add(customer);
    list.add(statefulService);
    //list.add(customer);
    //list.add(childList);
    //change the order ant it doesn't work ??
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baos);
    out.writeObject(list);
    out.close();
    
    byte[] bArray = baos.toByteArray();
    ObjectInputStream in = new ObjectInputStream(
        new ByteArrayInputStream(bArray));
    List<Object> nList = (List<Object>) in.readObject();
    in.close();
    customer = (Customer) nList.get(0);
    statefulService = (StatefulServiceImpl) nList.get(1);
    
    //customer = (Customer) ((List)nList.get(3)).get(0);
    //System.out.println(((List)nList.get(3)).get(0));
    
    statefulService.insertOrUpdateRecursive(customer); //this is broken !!
    
    ValidationReport report = statefulService.executeRules();
    assertEquals(0, report.getMessages().size());

    customer.setPhoneNumber(null);    
    statefulService.insertOrUpdate(customer);
    report = statefulService.executeRules();
    assertEquals(1, report.getMessages().size());
    assertTrue(report.contains("phoneNumberRequired"));
  }
  //*/
  
  
  /*
  @Test
  public void testRuleBaseSerialization() throws Exception {    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DroolsObjectOutputStream out = new DroolsObjectOutputStream(baos);
    out.writeObject(ruleBase);    
    out.close();    
    
    byte[] bArray = baos.toByteArray();
    DroolsObjectInputStream in = new DroolsObjectInputStream(new ByteArrayInputStream(bArray));
    ruleBase = (RuleBase)in.readObject();
    in.close();
  }
  
  @Test
  public void testRuleSessionSerialization() throws Exception {
    Marshaller marshaller = new DefaultMarshaller(); 
    
    StatefulSession statefulSession = ruleBase.newStatefulSession();
    statefulSession.setGlobal("validationReport", new DefaultValidationReport());
    statefulSession.insert(new Customer());
    assertNotNull(statefulSession.getGlobal("validationReport"));
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baos);
    ruleBase.writeStatefulSession(statefulSession, out,
        marshaller);
    out.writeObject(statefulSession.getGlobalResolver());
    out.close();    
    
    statefulSession.dispose();
    
    byte[] bArray = baos.toByteArray();
    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bArray));
    statefulSession = ruleBase.readStatefulSession(in, true,
        marshaller);
    statefulSession.setGlobalResolver((GlobalResolver) in.readObject());
    in.close();
    
    assertNotNull(statefulSession.getGlobal("validationReport"));
  }
  */
  
  
  @Test
  public void testIdentityRuleSessionSerialization() throws Exception {
    StatefulKnowledgeSession statefulSession = knowledgeBase.newStatefulKnowledgeSession();
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ObjectOutputStream out = new ObjectOutputStream(baos);
    
    Marshaller marshaller;
    
    {
    marshaller = createIdentityMarshaller(knowledgeBase);
    marshaller.marshall(out, statefulSession);
    }
    
    byte[] bArray = baos.toByteArray();
    ObjectInputStream in = new ObjectInputStream(
        new ByteArrayInputStream(bArray));
    
    marshaller = createIdentityMarshaller(knowledgeBase);
    statefulSession = marshaller.unmarshall(in);
    
    assertNotNull(statefulSession);
  }
  
  private Marshaller createIdentityMarshaller(
      KnowledgeBase knowledgeBase) {    
    ObjectMarshallingStrategyAcceptor acceptor = 
        MarshallerFactory.newClassFilterAcceptor( 
        new String[] { "*.*" } );
    ObjectMarshallingStrategy strategy = MarshallerFactory
        .newIdentityMarshallingStrategy( acceptor );
    Marshaller marshaller = MarshallerFactory.newMarshaller( 
        knowledgeBase, new ObjectMarshallingStrategy[] { 
        strategy } );
    return marshaller;
  }

}
