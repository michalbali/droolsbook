package droolsbook.bank.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.StatelessSession;
import org.drools.runtime.StatelessKnowledgeSession;

import droolsbook.bank.model.Customer;
import droolsbook.bank.service.BankingInquiryService;
import droolsbook.bank.service.BankingValidationService;
import droolsbook.bank.service.ReportFactory;
import droolsbook.bank.service.ValidationReport;

// @extract-start 03 66
public class BankingValidationServiceImpl implements
    BankingValidationService {

  private KnowledgeBase knowledgeBase;
  private ReportFactory reportFactory;
  private BankingInquiryService bankingInquiryService;
  
  /**
   * validates provided customer and returns validation report
   */
  public ValidationReport validate(Customer customer) {
    ValidationReport report = reportFactory
        .createValidationReport();
    StatelessKnowledgeSession session = knowledgeBase
        .newStatelessKnowledgeSession();
    session.setGlobal("validationReport", report);
    session.setGlobal("reportFactory", reportFactory);
    session
        .setGlobal("inquiryService", bankingInquiryService);
    session.execute(getFacts(customer));
    return report;
  }

  /**
   * @return facts that the rules will reason upon
   */
  private Collection<Object> getFacts(Customer customer) {
    ArrayList<Object> facts = new ArrayList<Object>();
    facts.add(customer);
    facts.add(customer.getAddress());
    facts.addAll(customer.getAccounts());
    return facts;
  }
  // @extract-end
  
  public void setKnowledgeBase(KnowledgeBase knowledgeBase) {
    this.knowledgeBase = knowledgeBase;
  }
  
  public void setReportFactory(ReportFactory reportFactory) {
    this.reportFactory = reportFactory;
  }
  
  public void setBankingInquiryService(
      BankingInquiryService bankingInquiryService) {
    this.bankingInquiryService = bankingInquiryService;
  }
  
}
