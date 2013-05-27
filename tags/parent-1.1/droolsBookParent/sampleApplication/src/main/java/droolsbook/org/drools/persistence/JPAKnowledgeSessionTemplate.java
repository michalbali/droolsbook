package droolsbook.org.drools.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.drools.KnowledgeBase;
import org.drools.container.spring.beans.persistence.DroolsSpringTransactionManager;
import org.drools.event.DebugProcessEventListener;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.persistence.jpa.JPAKnowledgeService;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.persistence.JpaProcessPersistenceContext;
import org.jbpm.persistence.MapProcessPersistenceContextManager;
import org.jbpm.process.workitem.wsht.LocalHTWorkItemHandler;
import org.jbpm.task.TaskService;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionTemplate;


// @extract-start 08 11
/**
 * performs actions with new or persisted knowledge sessions
 */
public class JPAKnowledgeSessionTemplate {
  @PersistenceContext
  private EntityManager em;
  private AbstractPlatformTransactionManager 
    transactionManager;

  private KnowledgeBase knowledgeBase;
  private Environment environment;

  private WorkItemHandler emailHandler;
  private WorkItemHandler transferFundsHandler;
  private TaskService localTaskService;

  public void init() {
    environment = EnvironmentFactory.newEnvironment();
    environment
        .set(EnvironmentName.TRANSACTION_MANAGER,
            new DroolsSpringTransactionManager(
                transactionManager));
    environment.set(
        EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
        new MapProcessPersistenceContextManager(
            new LocalJpaProcessPersistenceContext(em)));
    environment.set(
        EnvironmentName.OBJECT_MARSHALLING_STRATEGIES,
        new ObjectMarshallingStrategy[] { MarshallerFactory
            .newSerializeMarshallingStrategy() });
  }

  /**
   * performs action on a new persistable knowledge session
   * @param action to perform
   */
  public void doWithNewSession(KnowledgeSessionCallback 
      action) {
    StatefulKnowledgeSession session = JPAKnowledgeService
        .newStatefulKnowledgeSession(knowledgeBase, null,
            environment);
    execute(action, session);
  }

  /**
   * performs action on existing persisted knowledge session
   * @param sessionId prikary key of persisted session
   * @param action to perform
   */
  public void doWithLoadedSession(int sessionId,
      KnowledgeSessionCallback action) {
    StatefulKnowledgeSession session = JPAKnowledgeService
        .loadStatefulKnowledgeSession(sessionId,
            knowledgeBase, null, environment);
    execute(action, session);
  }
  
  private void execute(KnowledgeSessionCallback action,
      StatefulKnowledgeSession session) {
    LocalHTWorkItemHandler hTHandler = 
        new LocalHTWorkItemHandler(localTaskService, session, 
            true);
    try {
      registerWorkItemHandlers(session, hTHandler);
      action.execute(session);
    } finally {
      TransactionSynchronizationManager
          .registerSynchronization(new 
              SessionCleanupTransactionSynchronisation(
              session, hTHandler));
    }
  }

  /**
   * helper method for registering work item handlers 
   * (they are not persisted)
   */
  private void registerWorkItemHandlers(
      StatefulKnowledgeSession session,
      LocalHTWorkItemHandler hTHandler) {
    WorkItemManager manager = session.getWorkItemManager();
    hTHandler.connect();
    manager.registerWorkItemHandler("Human Task", hTHandler);
    manager.registerWorkItemHandler("Email", emailHandler);
    manager.registerWorkItemHandler("Transfer Funds",
        transferFundsHandler);
    // @extract-end

    session.addEventListener(new DebugProcessEventListener() {
      @Override
      public void afterNodeTriggered(
          ProcessNodeTriggeredEvent event) {
        System.out.println("-->"
            + event.getNodeInstance().getNodeName());
      }
    });
  }

private static class LocalJpaProcessPersistenceContext
    extends JpaProcessPersistenceContext {
  public LocalJpaProcessPersistenceContext(EntityManager em) {
    super(em);
  }

  @Override
  public void joinTransaction() {
    // ignore this call for non JTA environment
  }
}

  public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
    this.knowledgeBase = knowledgeBase;
  }

  public void setEmailHandler(WorkItemHandler emailHandler) {
    this.emailHandler = emailHandler;
  }

  public void setTransferFundsHandler(
      WorkItemHandler transferFundsHandler) {
    this.transferFundsHandler = transferFundsHandler;
  }

  public void setTransactionManager(
      AbstractPlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  public void setTaskService(TaskService taskService) {
    this.localTaskService = taskService;
  }

}

/*
 * LocalJpaProcessPersistenceContext is needed because of:
 * java.lang.IllegalStateException: Not allowed to join transaction on shared
 * EntityManager - use Spring transactions or EJB CMT instead
 * org.springframework
 * .orm.jpa.SharedEntityManagerCreator$SharedEntityManagerInvocationHandler
 * .invoke(SharedEntityManagerCreator.java:204) $Proxy40.joinTransaction(Unknown
 * Source) org.drools.persistence.jpa.JpaPersistenceContext.joinTransaction(
 * JpaPersistenceContext.java:37)
 * org.drools.persistence.SingleSessionCommandService
 * .<init>(SingleSessionCommandService.java:152)
 * sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
 * sun.reflect
 * .NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl
 * .java:57) sun.reflect.DelegatingConstructorAccessorImpl.newInstance(
 * DelegatingConstructorAccessorImpl.java:45)
 * java.lang.reflect.Constructor.newInstance(Constructor.java:532)
 * org.drools.persistence
 * .jpa.KnowledgeStoreServiceImpl.buildCommandService(KnowledgeStoreServiceImpl
 * .java:128)
 * org.drools.persistence.jpa.KnowledgeStoreServiceImpl.newStatefulKnowledgeSession
 * (KnowledgeStoreServiceImpl.java:66)
 * org.drools.persistence.jpa.JPAKnowledgeService
 * .newStatefulKnowledgeSession(JPAKnowledgeService.java:122)
 * droolsbook.org.drools
 * .persistence.JPAKnowledgeSessionLookup.newSession(JPAKnowledgeSessionLookup
 * .java:70) droolsbook.bank.service.impl.LoanApprovalServiceImpl.requestLoan(
 * LoanApprovalServiceImpl.java:42)
 * droolsbook.bank.service.impl.BankingServiceImpl
 * .requestLoan(BankingServiceImpl.java:62)
 * sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
 * sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
 * sun
 * .reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java
 * :43) java.lang.reflect.Method.invoke(Method.java:616)
 * org.springframework.aop.
 * support.AopUtils.invokeJoinpointUsingReflection(AopUtils.java:317)
 * org.springframework.aop.framework.ReflectiveMethodInvocation.invokeJoinpoint(
 * ReflectiveMethodInvocation.java:183)
 * org.springframework.aop.framework.ReflectiveMethodInvocation
 * .proceed(ReflectiveMethodInvocation.java:150)
 * org.springframework.transaction.
 * interceptor.TransactionInterceptor.invoke(TransactionInterceptor.java:110)
 * org.springframework.aop.framework.ReflectiveMethodInvocation.proceed(
 * ReflectiveMethodInvocation.java:172)
 * org.springframework.aop.interceptor.ExposeInvocationInterceptor
 * .invoke(ExposeInvocationInterceptor.java:91)
 * org.springframework.aop.framework
 * .ReflectiveMethodInvocation.proceed(ReflectiveMethodInvocation.java:172)
 * org.springframework
 * .aop.framework.JdkDynamicAopProxy.invoke(JdkDynamicAopProxy.java:204)
 * $Proxy46.requestLoan(Unknown Source)
 * droolsbook.sampleApplication.web.LoanRequestFormController
 * .loanRequest(LoanRequestFormController.java:27)
 * sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
 * sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
 * sun
 * .reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java
 * :43) java.lang.reflect.Method.invoke(Method.java:616)
 * org.springframework.web.
 * bind.annotation.support.HandlerMethodInvoker.invokeHandlerMethod
 * (HandlerMethodInvoker.java:176)
 * org.springframework.web.servlet.mvc.annotation
 * .AnnotationMethodHandlerAdapter.
 * invokeHandlerMethod(AnnotationMethodHandlerAdapter.java:440)
 * org.springframework
 * .web.servlet.mvc.annotation.AnnotationMethodHandlerAdapter.
 * handle(AnnotationMethodHandlerAdapter.java:428)
 * org.springframework.web.servlet
 * .DispatcherServlet.doDispatch(DispatcherServlet.java:925)
 * org.springframework.
 * web.servlet.DispatcherServlet.doService(DispatcherServlet.java:856)
 * org.springframework
 * .web.servlet.FrameworkServlet.processRequest(FrameworkServlet.java:920)
 * org.springframework
 * .web.servlet.FrameworkServlet.doPost(FrameworkServlet.java:827)
 * javax.servlet.http.HttpServlet.service(HttpServlet.java:641)
 * org.springframework
 * .web.servlet.FrameworkServlet.service(FrameworkServlet.java:801)
 * javax.servlet.http.HttpServlet.service(HttpServlet.java:722)
 */
