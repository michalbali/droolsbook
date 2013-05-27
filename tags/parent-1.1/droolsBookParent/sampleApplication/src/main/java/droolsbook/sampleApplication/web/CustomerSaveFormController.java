package droolsbook.sampleApplication.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import droolsbook.bank.model.BankingFactory;
import droolsbook.bank.model.Customer;
import droolsbook.bank.service.BankingService;
import droolsbook.bank.service.Message;
import droolsbook.bank.service.ValidationException;
import droolsbook.bank.service.ValidationReport;

// @extract-start 08 03
@Controller
@RequestMapping("/customerSave.htm")
public class CustomerSaveFormController {
  @Autowired
  private BankingService bankingService;
  @Autowired
  private BankingFactory bankingFactory;

  @RequestMapping(method=RequestMethod.POST)
  public String customerSave(
      @ModelAttribute Customer customer, Model model) {
    try {
      bankingService.add(customer);
      return "redirect:index.jsp";
    } catch (ValidationException e) {
      ValidationReport report = e.getValidationReport();
      model.addAttribute("errors", report.getMessagesByType(
          Message.Type.ERROR));
      model.addAttribute("warnings", report
          .getMessagesByType(Message.Type.WARNING));
      return "customerSave";
    }
  }
  
  @RequestMapping(method=RequestMethod.GET)
  public void newForm() {
  }
  
  @ModelAttribute("customer")
  public Customer createCustomer() {
    return bankingFactory.createCustomer();
  }
}
// @extract-end
