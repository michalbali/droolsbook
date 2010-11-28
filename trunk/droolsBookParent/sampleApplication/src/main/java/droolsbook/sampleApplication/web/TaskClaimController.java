package droolsbook.sampleApplication.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.task.service.TaskClient;
import org.drools.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import droolsbook.bank.model.User;


public class TaskClaimController extends AbstractController {
  @Autowired
  private TaskClient client;
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
    BlockingTaskOperationResponseHandler 
      operationResponseHandler = 
        new BlockingTaskOperationResponseHandler();
    client.claim(taskId, user.getUserId(),
        operationResponseHandler);
    operationResponseHandler.waitTillDone(5000);

    return new ModelAndView("redirect:taskList.htm");
  }
  // @extract-end

}
