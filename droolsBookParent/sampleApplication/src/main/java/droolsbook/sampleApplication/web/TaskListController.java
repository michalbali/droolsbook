package droolsbook.sampleApplication.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

import droolsbook.bank.model.User;

// @extract-start 08 04
public class TaskListController extends AbstractController {
  @Autowired
  private TaskService client;
  @Autowired
  private WebSessionUtils webSessionUtils;

  @Override
  protected ModelAndView handleRequestInternal(
      HttpServletRequest request,HttpServletResponse response)
      throws Exception {
    User user = webSessionUtils.getUser();
    List<TaskSummary> tasks = client.getTasksAssignedAsPotentialOwner(user.getUserId(),
        user.getLanguage());

    Map<String, Object> model = new HashMap<String, Object>();
    model.put("tasks", tasks);

    return new ModelAndView("taskList", "model", model);
  }
}
// @extract-end
