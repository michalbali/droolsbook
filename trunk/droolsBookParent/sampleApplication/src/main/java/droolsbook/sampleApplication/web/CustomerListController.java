package droolsbook.sampleApplication.web;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import droolsbook.bank.service.BankingService;

public class CustomerListController implements Controller {
  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private BankingService bankingService;

  public ModelAndView handleRequest(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {

    String now = (new Date()).toString();
    logger.info("Returning customerList view with " + now);

    Map<String, Object> model = new HashMap<String, Object>();
    model.put("now", now);
    model.put("customers", bankingService.findAllCustomers());

    return new ModelAndView("customerList", "model", model);
  }

}
