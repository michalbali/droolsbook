package droolsbook.sampleApplication.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.task.query.TaskSummary;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import droolsbook.bank.model.User;

// @extract-start 08 04
public class TaskListController extends AbstractController {
  @Autowired
  private MinaTaskClient client;
  @Autowired
  private WebSessionUtils webSessionUtils;

  @Override
  protected ModelAndView handleRequestInternal(
      HttpServletRequest request,HttpServletResponse response)
      throws Exception {
    BlockingTaskSummaryResponseHandler responseHandler = 
      new BlockingTaskSummaryResponseHandler();
    User user = webSessionUtils.getUser();
    client.getTasksAssignedAsPotentialOwner(user.getUserId(),
        user.getLanguage(), responseHandler);
    List<TaskSummary> tasks = responseHandler.getResults();

    Map<String, Object> model = new HashMap<String, Object>();
    model.put("tasks", tasks);

    return new ModelAndView("taskList", "model", model);
  }
}
// @extract-end
