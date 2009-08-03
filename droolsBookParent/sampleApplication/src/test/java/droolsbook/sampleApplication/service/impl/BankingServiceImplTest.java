package droolsbook.sampleApplication.service.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.api.Expectation;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import droolsbook.bank.model.BankingFactory;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.DefaultBankingFactory;
import droolsbook.bank.service.BankingValidationService;
import droolsbook.bank.service.Message;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.Message.Type;
import droolsbook.bank.service.impl.BankingServiceImpl;
import droolsbook.bank.service.impl.CEPServiceImpl;
import droolsbook.sampleApplication.repository.CustomerRepository;

@RunWith(JMock.class)
public class BankingServiceImplTest {
  Mockery context = new JUnit4Mockery();
  BankingServiceImpl service;
  CustomerRepository customerRepository;
  BankingValidationService validationService;
  BankingFactory bankingFactory;
  
  @Before
  public void setUp() throws Exception {
    customerRepository = context.mock(CustomerRepository.class);
    validationService = context.mock(BankingValidationService.class);
    
    service = new BankingServiceImpl();
    service.setCustomerRepository(customerRepository);
    service.setValidationService(validationService);
    service.setCepService(new CEPServiceImpl());
    
    bankingFactory = new DefaultBankingFactory();
  }
  
  private void verifyValidate(final Customer customer) {
    final ValidationReport report = context.mock(ValidationReport.class);
    context.checking(new Expectations() {
      {
        one(validationService).validate(customer);
        will(returnValue(report));
        one(report).getMessagesByType(Message.Type.ERROR);
        will(returnValue(Collections.emptySet()));
      }
    });
  }

  @Test
  public void testAdd() {
    final Customer customer = bankingFactory.createCustomer();    
    verifyValidate(customer);    
    context.checking(new Expectations() {
      {
        one(customerRepository).addCustomer(customer);
      }
    });
    service.add(customer);

    //todo test of cese with exception
  }

  @Test
  public void testSave() {
    final Customer customer = bankingFactory.createCustomer();
    verifyValidate(customer);
    context.checking(new Expectations() {
      {
        one(customerRepository).updateCustomer(customer);
        will(returnValue(customer));
      }
    });
    service.save(customer);
  }

}
