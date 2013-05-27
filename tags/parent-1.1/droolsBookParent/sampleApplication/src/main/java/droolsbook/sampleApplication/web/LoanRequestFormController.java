package droolsbook.sampleApplication.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import droolsbook.bank.model.BankingFactory;
import droolsbook.bank.model.Loan;
import droolsbook.bank.service.BankingService;

@Controller
@RequestMapping("/loanRequest.htm")
public class LoanRequestFormController {
  @Autowired
  private BankingFactory bankingFactory;
  @Autowired
  private BankingService bankingService;
  @Autowired
  private WebSessionUtils webSessionUtils;

  @RequestMapping(method = RequestMethod.POST)
  public String loanRequest(@ModelAttribute Loan loanRequest,
      Model model) {
    bankingService.requestLoan(loanRequest,
        webSessionUtils.getCustomer());
    return "redirect:customerList.htm";
  }

  @RequestMapping(method = RequestMethod.GET)
  public void newForm() {
  }

  @ModelAttribute("loanRequest")
  public Loan createLoanRequest() {
    return bankingFactory.createLoan();
  }
}
