package droolsbook.stateful.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Iterator;

import org.drools.KnowledgeBase;
import org.drools.common.DroolsObjectInputStream;
import org.drools.common.DroolsObjectOutputStream;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.Marshaller;
import org.drools.marshalling.MarshallerFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyAcceptor;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;

import droolsbook.bank.model.Account;
import droolsbook.bank.model.Customer;
import droolsbook.bank.service.BankingInquiryService;
import droolsbook.bank.service.Message;
import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.impl.BankingInquiryServiceImpl;
import droolsbook.bank.service.impl.DefaultReportFactory;
import droolsbook.stateful.StatefulService;
import droolsbook.stateful.bank.service.impl.StatefulDefaultValidationReport;

/**
 * can be stored in HTTP session -> is Serializable
 * 
 * @author miba
 * 
 */
// @extract-start 05 02
public class StatefulServiceImpl implements StatefulService,
    Serializable {  
  private transient KnowledgeBase knowledgeBase;
  private transient StatefulKnowledgeSession statefulSession;
  private transient ReportFactory reportFactory;  

  public StatefulServiceImpl(KnowledgeBase knowledgeBase,
      ReportFactory reportFactory,
      BankingInquiryService inquiryService) {
    this.reportFactory = reportFactory;
    this.knowledgeBase = knowledgeBase;
    statefulSession = createKnowledgeSession(inquiryService);
  }
  // @extract-end

  // @extract-start 05 22
  private StatefulKnowledgeSession createKnowledgeSession(
      BankingInquiryService inquiryService) {
    StatefulKnowledgeSession session = knowledgeBase
      .newStatefulKnowledgeSession();
    session.setGlobal("reportFactory", reportFactory);
    session.setGlobal("inquiryService", inquiryService);
    return session;
  }
  // @extract-end
  
  // @extract-start 05 03
  public void insertOrUpdate(Object fact) {
    if (fact == null) {
      return;
    }

    FactHandle factHandle = statefulSession
        .getFactHandle(fact);

    if (factHandle == null) {
      statefulSession.insert(fact);
    } else {
      statefulSession.update(factHandle, fact);
    }
  }

  public void insertOrUpdateRecursive(Customer customer) {
    insertOrUpdate(customer); 
    insertOrUpdate(customer.getAddress());
    if (customer.getAccounts() != null) {
      for (Account account : customer.getAccounts()) {
        insertOrUpdate(account);
      }
    }
  }
  // @extract-end
  
  /*
  // @extract-start 05 04
  public ValidationReport executeRules() {
    ValidationReport validationReport = 
        reportFactory.createValidationReport();
    statefulSession.setGlobal("validationReport",
        validationReport);    
    statefulSession.fireAllRules();
    return validationReport;
  }
  // @extract-end
  */

  public ValidationReport executeRules() {
    ///*
    ValidationReport validationReport = new StatefulDefaultValidationReport();
    statefulSession.setGlobal("validationReport",
        validationReport);
    //*/

    statefulSession.fireAllRules();
    
    /*
    ValidationReport validationReport = reportFactory
      .createValidationReport();
    QueryResults queryResults = 
        statefulSession.getQueryResults("getAllMessages");
    for (QueryResultsRow queryResultsRow : queryResults) {
      Message message = (Message) queryResultsRow.get(
        "$message");
      validationReport.addMessage(message);
    }
    */
    
    return validationReport;
  }
  
  // @extract-start 05 05
  public void terminate() {
    statefulSession.dispose();
  }
  // @extract-end

  // @extract-start 05 33
  private void writeObject(ObjectOutputStream out)
      throws IOException {
    out.defaultWriteObject();

    DroolsObjectOutputStream droolsOut = 
        new DroolsObjectOutputStream((OutputStream) out);
    droolsOut.writeObject(knowledgeBase);

    Marshaller marshaller = createSerializableMarshaller(
        knowledgeBase);
    marshaller.marshall(droolsOut, statefulSession);
  }
  // @extract-end

  // @extract-start 05 35
  private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    in.defaultReadObject();

    DroolsObjectInputStream droolsIn = 
        new DroolsObjectInputStream((InputStream) in);
    this.knowledgeBase = (KnowledgeBase)droolsIn.readObject();

    Marshaller marshaller = createSerializableMarshaller(
        knowledgeBase);
    statefulSession = marshaller.unmarshall(droolsIn);

    this.reportFactory = new DefaultReportFactory();    
    statefulSession.setGlobal("reportFactory", reportFactory);
    statefulSession.setGlobal("inquiryService",
        new BankingInquiryServiceImpl());
  }
  // @extract-end
  
  // @extract-start 05 34
  private Marshaller createSerializableMarshaller(
      KnowledgeBase knowledgeBase) {    
    ObjectMarshallingStrategyAcceptor acceptor = 
        MarshallerFactory.newClassFilterAcceptor( 
        new String[] { "*.*" } );
    ObjectMarshallingStrategy strategy = MarshallerFactory
        .newSerializeMarshallingStrategy( acceptor );
    Marshaller marshaller = MarshallerFactory.newMarshaller( 
        knowledgeBase, new ObjectMarshallingStrategy[] { 
        strategy } );
    return marshaller;
  }
  // @extract-end
  
  /*
  // @extract-start 05 23
  private void writeObject(ObjectOutputStream out)
      throws IOException {
    out.defaultWriteObject();

    DroolsObjectOutputStream droolsOut = 
        new DroolsObjectOutputStream((OutputStream) out);
    droolsOut.writeObject(knowledgeBase);
  }
  // @extract-end

  // @extract-start 05 24
  private void readObject(ObjectInputStream in)
      throws IOException, ClassNotFoundException {
    in.defaultReadObject();

    DroolsObjectInputStream droolsIn = 
        new DroolsObjectInputStream((InputStream) in);
    this.knowledgeBase = (KnowledgeBase)droolsIn.readObject();

    this.reportFactory = new DefaultReportFactory();
    statefulSession = createKnowledgeSession(
        new BankingInquiryServiceImpl());
  }
  // @extract-end
  */
}
