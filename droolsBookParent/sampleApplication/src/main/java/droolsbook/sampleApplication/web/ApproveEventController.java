package droolsbook.sampleApplication.web;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import droolsbook.bank.model.LoanApprovalHolder;
import droolsbook.bank.service.BankingService;

public class ApproveEventController extends AbstractController {
  @Autowired
  private BankingService bankingService;

  // @extract-start 08 06
  /**
   * sends 'loan approved' event to specific process
   */
  @PersistenceContext(unitName="entityManagerFactory")
  EntityManager em;
  
  @Override
  protected ModelAndView handleRequestInternal(
      HttpServletRequest request,HttpServletResponse response)
      throws Exception {
    String sessionId = request.getParameter("sessionId");
    LoanApprovalHolder pendingLoanApprovalHolder = em.find(
        LoanApprovalHolder.class, Integer.valueOf(sessionId));
    bankingService.approveLoan(pendingLoanApprovalHolder);
    return new ModelAndView("redirect:index.jsp");
  }
  // @extract-end
}
