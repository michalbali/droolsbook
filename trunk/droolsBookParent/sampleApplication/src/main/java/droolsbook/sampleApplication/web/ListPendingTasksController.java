package droolsbook.sampleApplication.web;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import droolsbook.bank.model.LoanApprovalHolder;

@Controller
public class ListPendingTasksController {

  protected final Logger logger = LoggerFactory.getLogger(getClass());

  @PersistenceContext
  EntityManager em;

  @RequestMapping("/listPendingTasks.htm")
  public String listPendingTasks(Model model) {
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

    return "redirect:customerList.htm";
  }

}
