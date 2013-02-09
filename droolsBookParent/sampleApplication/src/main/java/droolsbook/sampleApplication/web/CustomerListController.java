package droolsbook.sampleApplication.web;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import droolsbook.bank.service.BankingService;

@Controller
public class CustomerListController {
  protected final Logger logger = LoggerFactory
      .getLogger(getClass());

  @Autowired
  private BankingService bankingService;

  @RequestMapping("/customerList.htm")
  public String customerList(Model model) {

    String now = (new Date()).toString();
    logger.info("Returning customerList view with " + now);

    model.addAttribute("now", now);
    model.addAttribute("customers",
        bankingService.findAllCustomers());

    return "customerList";
  }

}
