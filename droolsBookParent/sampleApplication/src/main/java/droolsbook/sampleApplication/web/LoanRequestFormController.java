package droolsbook.sampleApplication.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import droolsbook.bank.model.BankingFactory;
import droolsbook.bank.model.Customer;
import droolsbook.bank.model.Loan;
import droolsbook.bank.service.BankingService;

@Controller
@RequestMapping("/loanRequest.htm")
public class LoanRequestFormController {

  @Autowired
  private BankingFactory bankingFactory;
  @Autowired
  private BankingService bankingService;

  @RequestMapping(method=RequestMethod.POST)
  public String loanRequest(@ModelAttribute Loan loanRequest, Model model) {
    Customer customer = null;// get from session; currently logged in user
    bankingService.requestLoan(loanRequest, customer);
    return "redirect:customerList.htm";
  }

  @ModelAttribute("loanRequest")
  public Loan createLoanRequest() {
    return bankingFactory.createLoan();
  }
  
  @RequestMapping(method=RequestMethod.GET)
  public void form() {
  }

}
