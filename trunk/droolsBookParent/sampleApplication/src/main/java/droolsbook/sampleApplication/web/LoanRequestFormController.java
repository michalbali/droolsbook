package droolsbook.sampleApplication.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import droolsbook.bank.model.BankingFactory;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.service.BankingService;

public class LoanRequestFormController extends
    SimpleFormController {

  @Autowired
  private BankingFactory bankingFactory;
  @Autowired
  private BankingService bankingService;

  @Override
  public ModelAndView onSubmit(Object command,
      BindException errors) throws ServletException {
    Loan loan = (Loan) command;
    Customer customer = null;// get from session; currently logged in user
    bankingService.requestLoan(loan, customer);
    return new ModelAndView(new RedirectView(getSuccessView()));
  }

  protected Object formBackingObject(HttpServletRequest request)
      throws ServletException {
    Loan loan = bankingFactory.createLoan();
    return loan;
  }

}
