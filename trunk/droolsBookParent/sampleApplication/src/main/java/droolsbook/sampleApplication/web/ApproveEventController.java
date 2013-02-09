package droolsbook.sampleApplication.web;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import droolsbook.bank.model.LoanApprovalHolder;
import droolsbook.bank.service.BankingService;

@Controller
public class ApproveEventController {
  @Autowired
  private BankingService bankingService;

  // @extract-start 08 06
  /**
   * sends 'loan approved' event to specific process
   */
  @PersistenceContext
  EntityManager em;
  
  @RequestMapping("/approveEvent.htm")
  public String approveEvent(Integer sessionId,
      Model model) {
    LoanApprovalHolder pendingLoanApprovalHolder = em.find(
        LoanApprovalHolder.class, sessionId);
    bankingService.approveLoan(pendingLoanApprovalHolder);
    return "redirect:index.jsp";
  }
  // @extract-end
}
