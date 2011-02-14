package droolsbook.sampleApplication.web;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import droolsbook.bank.model.LoanApprovalHolder;
import droolsbook.bank.service.BankingService;
import droolsbook.bank.service.impl.BankingServiceImpl;

public class ListPendingTasksController extends
    AbstractController {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private BankingService bankingService;

  @PersistenceContext(unitName="entityManagerFactory")
  EntityManager em;

  @Override
  protected ModelAndView handleRequestInternal(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    // in reality this should lists all tasks that were completed but not yet
    // approved
    // ones that are rejected are not displayed ...

    for (LoanApprovalHolder holder : (List<LoanApprovalHolder>) em
        .createQuery("from LoanApprovalHolder as lah")
        .getResultList()) {
      logger.info("sessionId:" + holder.getSessionId()
          + " processInstanceId:"
          + holder.getProcessInstanceId() + " customer:"
          + holder.getCustomer());
    }

    return new ModelAndView("redirect:customerList.htm");
  }

}
