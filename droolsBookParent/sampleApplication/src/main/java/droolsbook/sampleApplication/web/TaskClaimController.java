package droolsbook.sampleApplication.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import droolsbook.bank.model.User;


public class TaskClaimController extends AbstractController {
  @Autowired
  private TaskService client;
  @Autowired
  private WebSessionUtils webSessionUtils;

  // @extract-start 08 05
  /**
   * claims specified task for the current user
   */
  @Override
  protected ModelAndView handleRequestInternal(
      HttpServletRequest request,HttpServletResponse response)
      throws Exception {
    long taskId = Long.parseLong(request
        .getParameter("taskId"));
    User user = webSessionUtils.getUser();
    client.claim(taskId, user.getUserId());
    return new ModelAndView("redirect:taskList.htm");
  }
  // @extract-end

}
