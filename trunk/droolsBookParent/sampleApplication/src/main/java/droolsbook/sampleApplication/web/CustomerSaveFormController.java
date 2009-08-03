package droolsbook.sampleApplication.web;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.SimpleFormController;
import org.springframework.web.servlet.view.RedirectView;

import droolsbook.bank.model.BankingFactory;
import droolsbook.bank.model.Customer;
import droolsbook.bank.service.BankingService;
import droolsbook.bank.service.Message;
import droolsbook.bank.service.ValidationException;
import droolsbook.bank.service.ValidationReport;
import droolsbook.bank.service.Message.Type;

// @extract-start 08 03
public class CustomerSaveFormController extends
    SimpleFormController {
  @Autowired
  private BankingService bankingService;
  @Autowired
  private BankingFactory bankingFactory;

  public ModelAndView onSubmit(Object command,
      BindException errors) throws ServletException {
    Customer customer = (Customer) command;
    try {
      bankingService.add(customer);
      return new ModelAndView(new RedirectView(
          getSuccessView()));
    } catch (ValidationException e) {
      ValidationReport report = e.getValidationReport();
      Map model = errors.getModel();
      model.put("errors", report.getMessagesByType(
          Message.Type.ERROR));
      model.put("warnings", report
          .getMessagesByType(Message.Type.WARNING));
      return new ModelAndView(getFormView(), model);
    }
  }

  protected Object formBackingObject(HttpServletRequest request)
      throws ServletException {
    Customer customer = bankingFactory.createCustomer();
    return customer;
  }
}
// @extract-end
