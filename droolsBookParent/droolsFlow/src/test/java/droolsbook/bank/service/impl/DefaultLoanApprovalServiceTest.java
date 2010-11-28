package droolsbook.bank.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBaseConfiguration;
import org.drools.SystemEventListenerFactory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.AbstractRuleBase;
import org.drools.io.ResourceFactory;
import org.drools.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.drools.process.workitem.wsht.WSHumanTaskHandler;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.drools.task.MockUserInfo;
import org.drools.task.Status;
import org.drools.task.User;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.TaskClient;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.TaskService;
import org.drools.task.service.TaskServiceSession;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskClientHandler;
import org.drools.task.service.mina.MinaTaskServer;
import org.drools.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.drools.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.service.BankingService;
import droolsbook.droolsflow.model.DefaultMessage;
import droolsbook.droolsflow.model.Rating;
import droolsbook.droolsflow.workitem.TransferWorkItemHandler;

@RunWith(JMock.class)
public class DefaultLoanApprovalServiceTest {

  private static final String PROCESS_RATING_CALCULATION = "ratingCalculation";
  private static final String PROCESS_LOAN_APPROVAL = "loanApproval";
  static KnowledgeBase knowledgeBase;
  StatefulKnowledgeSession session;
  Loan loan;
  Customer customer;
  ProcessInstance processInstance;
  BankingService bankingService;
  
  Mockery mockery;
  Account loanSourceAccount;

  // @extract-start 07 03
  static final long NODE_FAULT_NOT_VALID = 21;
  static final long NODE_SPLIT_VALIDATED = 20;
  // @extract-end
  final long NODE_SPLIT_AMOUNT_TO_BORROW = 4;
  // final long NODE_WORK_ITEM_EMAIL = 25;
  final long NODE_FAULT_LOW_RATING = 19;
  final long NODE_SUBFLOW_RATING_CALCULATION = 7;
  final long NODE_SPLIT_RATING = 8;
  final long NODE_JOIN_RATING = 5;
  final long NODE_JOIN_PROCESS_LOAN = 23;
  final long NODE_GROUP_VALIDATE_LOAN = 14;
  final long NODE_WORK_ITEM_TRANSFER = 26;
  final long NODE_HUMAN_TASK_PROCESS_LOAN = 12;

  final long NODE_GROUP_CALCULATE_RATING = 4;
  final long NODE_GROUP_CALCULATE_INCOMES = 2;
  final long NODE_GROUP_CALCULATE_EXPENSES = 3;
  final long NODE_GROUP_CALCULATE_REPAYMENTS = 8;

  TrackingProcessEventListener trackingProcessEventListener;

  WorkingMemoryFileLogger fileLogger;
  
  @BeforeClass
  public static void setUpClass() throws Exception {
    KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
    builder.add(ResourceFactory.newClassPathResource("loanApproval.drl"), ResourceType.DRL);
    builder.add(ResourceFactory.newClassPathResource("loanApproval.rf"), ResourceType.DRF);
    builder.add(ResourceFactory.newClassPathResource("ratingCalculation.drl"), ResourceType.DRL);
    builder.add(ResourceFactory.newClassPathResource("ratingCalculation.rf"), ResourceType.DRF);
    
    if (builder.hasErrors()) {
      throw new RuntimeException(builder.getErrors().toString());
    }

    //it actually doesn't matter for runtime if we set this porperty or not
    //it is better to set it through a configuration file because the Eclipse ruleflow editor can pick it up
    KnowledgeBaseConfiguration configuration = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
    //configuration.setProperty("drools.workDefinitions", "WorkDefinitions.conf BankingWorkDefinitions.conf");
    
    knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase(configuration);
    knowledgeBase.addKnowledgePackages(builder.getKnowledgePackages());
  }

  // @extract-start 07 01
  @Before
  public void setUp() throws Exception {
    session = knowledgeBase.newStatefulKnowledgeSession();
    
    trackingProcessEventListener = 
      new TrackingProcessEventListener();
    session.addEventListener(trackingProcessEventListener);
    session.getWorkItemManager().registerWorkItemHandler(
        "Email", new SystemOutWorkItemHandler());

    loanSourceAccount = new Account();
    
    customer = new Customer();
    customer.setFirstName("Bob");
    customer.setLastName("Green");
    customer.setEmail("bob.green@mail.com");
    Account account = new Account();
    account.setNumber(123456789l);
    customer.addAccount(account);
    account.setOwner(customer);

    loan = new Loan();
    loan.setDestinationAccount(account);
    loan.setAmount(BigDecimal.valueOf(4000.0));
    loan.setDurationYears(2);
    // @extract-end
    
    mockery = new JUnit4Mockery();
    bankingService = mockery.mock(BankingService.class);

    WorkItemHandler handler = new SystemOutWorkItemHandler();
    session.getWorkItemManager().registerWorkItemHandler(
        "Human Task", handler);

    WorkItemHandler transferFundsHandler = new SystemOutWorkItemHandler();
    session.getWorkItemManager().registerWorkItemHandler(
        "Transfer Funds", transferFundsHandler);
    
    //fileLogger = new WorkingMemoryFileLogger(session);
  }

  // @extract-start 07 15
  private void startProcess() {
    Map<String, Object> parameterMap = 
        new HashMap<String, Object>();
    parameterMap.put("loanSourceAccount", loanSourceAccount);
    parameterMap.put("customer", customer);
    parameterMap.put("loan", loan);
    processInstance = session.startProcess(
        PROCESS_LOAN_APPROVAL, parameterMap);
    session.insert(processInstance);
    session.fireAllRules();
    
		// important note: Be careful when starting a process instance and then
		// inserting the process instance into the session, if there are rules
		// that react to a process instance then the 'startProcess' may not
		// trigger them; what seems to be happening is that when the process is
		// started it runs until it comes to a wait state or a ruleflow group
		// that has some activated rules, then it waits for the fireAllRules
		// call. 
  }
  // @extract-end
  
  @After
  public void onShutDown() {
    session.dispose();
    if (fileLogger != null) {
    	fileLogger.writeToDisk();
    }
  }
  
  private void setUpLowAmount() {
    assertTrue(loan.getAmount().doubleValue() < 5000);
    session.insert(loan);
  }

  private void setUpHighAmount() {
    loan.setAmount(BigDecimal.valueOf(19000));
    session.insert(loan);
  }

  private void setUpHighRating() {
  }

  private void setUpLowRating() {
    Rating rating = new Rating();
    rating.setRating(10);
    session.insert(rating);
  }
  
  private void approveLoan() {
    processInstance.signalEvent("LoanApprovedEvent", null);
  }  

  @Ignore("for now")
  @Test
  public void workItemSetup() {
    RuleBaseConfiguration ruleBaseConfiguration = ((AbstractRuleBase) knowledgeBase)
        .getConfiguration();
    //System.out.println(ruleBaseConfiguration
    //    .getWorkItemHandlers());
    //System.out.println(ruleBaseConfiguration
    //    .getProcessWorkDefinitions());
  }

  @Test
  public void testTest() throws Exception {
    session.getWorkItemManager().registerWorkItemHandler(
        "Email", new WorkItemHandler() {
          public void executeWorkItem(WorkItem workItem,
              WorkItemManager manager) {
            System.out.println("Sending email:");
            System.out.println("From: "
                + workItem.getParameter("From"));
            System.out.println("To: "
                + workItem.getParameter("To"));
            System.out.println("Subject: "
                + workItem.getParameter("Subject"));
            System.out.println("Body: ");
            System.out.println(workItem.getParameter("Body"));
            manager.completeWorkItem(workItem.getId(), null);
          }

          public void abortWorkItem(WorkItem workItem,
              WorkItemManager manager) {
          }
        });
    //ReportFactory factory = new DefaultReportFactory();
    //ValidationReport report = factory.createValidationReport();
    //session.setGlobal("validationReport", report);
    session.insert(new DefaultMessage());
    startProcess();
  }

  @Test
  public void validateLoan() {
    startProcess();
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_GROUP_VALIDATE_LOAN));
  }

  @Test
  public void validatedSplit() {
    startProcess();
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_SPLIT_VALIDATED));
  }

  // @extract-start 07 02
  @Test
  public void notValid() {
    session.insert(new DefaultMessage());
    startProcess();
    
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_FAULT_NOT_VALID));
    assertEquals(ProcessInstance.STATE_ABORTED,
        processInstance.getState());
  }
  // @extract-end

  @Test
  public void amountToBorrow() {
    startProcess();
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_SPLIT_AMOUNT_TO_BORROW));
  }

  // @extract-start 07 07
  @Test
  public void amountToBorrowLow() {
    setUpLowAmount();
    startProcess();
    
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_JOIN_RATING));
    assertFalse(trackingProcessEventListener
        .isNodeTriggered(PROCESS_LOAN_APPROVAL,
            NODE_SUBFLOW_RATING_CALCULATION));
  }
  // @extract-end

  // @extract-start 07 05
  @Test
  public void amountToBorrowHighRatingCalculation() {
    setUpHighAmount();
    startProcess();
    assertTrue(trackingProcessEventListener
        .isNodeTriggered(PROCESS_LOAN_APPROVAL,
            NODE_SUBFLOW_RATING_CALCULATION));
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_RATING_CALCULATION,
        NODE_GROUP_CALCULATE_RATING));
    WorkflowProcessInstance process = 
        (WorkflowProcessInstance) processInstance;
    assertEquals(1500, 
        process.getVariable("customerLoanRating"));
  }
  // @extract-end

  @Test
  public void ratingCalculation() {
    processInstance = session.startProcess(PROCESS_RATING_CALCULATION); // no parameters
    session.fireAllRules();
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_RATING_CALCULATION,
        NODE_GROUP_CALCULATE_INCOMES));
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_RATING_CALCULATION,
        NODE_GROUP_CALCULATE_REPAYMENTS));
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_RATING_CALCULATION,
        NODE_GROUP_CALCULATE_EXPENSES));
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_RATING_CALCULATION,
        NODE_GROUP_CALCULATE_RATING));
    assertEquals(ProcessInstance.STATE_COMPLETED,
        processInstance.getState());
    assertNotNull(session.getObjects(
        new ClassObjectFilter(Rating.class)).iterator()
        .next());
  }

  @Test
  public void ratingSplitNodeOther() {
    setUpLowRating();
    setUpHighAmount();
    startProcess();
    
    assertFalse(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_JOIN_RATING));
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_FAULT_LOW_RATING));
  }

  // @extract-start 07 10
  @Test
  public void ratingSplitNodeAccept() {
    setUpHighAmount();
    setUpHighRating();
    startProcess();    
    
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_SPLIT_RATING));
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_JOIN_RATING));
  }
  // @extract-end

  // @extract-start 07 11  
  @Test
  public void processLoan() throws Exception {
    EntityManagerFactory emf = Persistence
        .createEntityManagerFactory("org.drools.task");

    TaskService taskService = new TaskService(emf, 
        SystemEventListenerFactory.getSystemEventListener());
    MockUserInfo userInfo = new MockUserInfo();
    taskService.setUserinfo(userInfo);
    
    TaskServiceSession taskSession = taskService
        .createSession();
    taskSession.addUser(new User("Administrator"));
    taskSession.addUser(new User("123"));
    taskSession.addUser(new User("456"));
    taskSession.addUser(new User("789"));

    MinaTaskServer server = new MinaTaskServer(taskService);
    Thread thread = new Thread(server);
    thread.start();
    
    //wait until the server becomes active
    int timeoutCounter = 0;
    while ((server.getIoAcceptor() == null || !server
        .getIoAcceptor().isActive())
        && timeoutCounter++ < 1000) {
      Thread.sleep(10);
    }
    assertTrue(server.getIoAcceptor().isActive());

    WorkItemHandler htHandler = new WSHumanTaskHandler();
    session.getWorkItemManager().registerWorkItemHandler(
        "Human Task", htHandler);
    setUpLowAmount();
    startProcess();
    // @extract-end

    // @extract-start 07 12
    TaskClient client = new TaskClient(new MinaTaskClientConnector("client 1",
    		new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
    boolean isConnected = client.connect("127.0.0.1", 9123);
    assertTrue(isConnected);

    BlockingTaskSummaryResponseHandler summaryHandler = 
        new BlockingTaskSummaryResponseHandler();
    timeoutCounter = 0;
    List<TaskSummary> tasks;
    do {
      client.getTasksAssignedAsPotentialOwner("123", "en-UK",
          summaryHandler);
      tasks = summaryHandler.getResults();
      //wait until the task is registered with the server or timeout expires
      if (timeoutCounter != 0) {
        Thread.sleep(10);
      }
    } while (tasks.isEmpty() && timeoutCounter++ < 1000);
    assertEquals(1, tasks.size());
    TaskSummary task = tasks.get(0);
    assertEquals("Process Loan", task.getName());
    assertEquals(3, task.getPriority());    
    assertEquals(Status.Ready, task.getStatus());
    // @extract-end

    // @extract-start 07 13
    BlockingTaskOperationResponseHandler operationHandler = 
        new BlockingTaskOperationResponseHandler();
    client.claim(task.getId(), "123", operationHandler);
    operationHandler.waitTillDone(5000);

    operationHandler = 
        new BlockingTaskOperationResponseHandler();
    client.start(task.getId(), "123", operationHandler);
    operationHandler.waitTillDone(5000);

    operationHandler = 
        new BlockingTaskOperationResponseHandler();
    client.complete(task.getId(), "123", null,
        operationHandler);
    operationHandler.waitTillDone(5000);
    
    //wait until the process resumes or timeout expires
    timeoutCounter = 0;
    while (!trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_JOIN_PROCESS_LOAN)
        && timeoutCounter++ < 1000) {
      Thread.sleep(10);
    } 
    
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_JOIN_PROCESS_LOAN));
    
    client.disconnect();
    server.stop();
  }
  // @extract-end

  // @extract-start 07 14
  @Test
  public void approveEventJoin() {
    setUpLowAmount();
    startProcess();
    assertEquals(ProcessInstance.STATE_ACTIVE, processInstance
        .getState());
    assertFalse(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_WORK_ITEM_TRANSFER));
    approveLoan();
    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_WORK_ITEM_TRANSFER));

    assertEquals(ProcessInstance.STATE_COMPLETED,
        processInstance.getState());
  }
  // @extract-end

  // @extract-start 07 09
  @Test
  public void transferFunds() {
    mockery.checking(new Expectations() {
      {
        one(bankingService).transfer(loanSourceAccount,
            loan.getDestinationAccount(), loan.getAmount());
      }
    });

    setUpTransferWorkItem();
    setUpLowAmount();
    startProcess();
    approveLoan();

    assertTrue(trackingProcessEventListener.isNodeTriggered(
        PROCESS_LOAN_APPROVAL, NODE_WORK_ITEM_TRANSFER));
  }
  // @extract-end

  private void setUpTransferWorkItem() {
    // @extract-start 07 08
    TransferWorkItemHandler transferHandler = 
        new TransferWorkItemHandler();
    transferHandler.setBankingService(bankingService);
    session.getWorkItemManager().registerWorkItemHandler(
        "Transfer Funds", transferHandler);
    // @extract-end
  }

  // integration test
  @Ignore("this is a abstract example")
  @Test
  public void defaultBankingServiceTest() throws Exception {
    bankingService.requestLoan(loan, customer);
  }

}
