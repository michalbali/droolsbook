package droolsbook.sampleApplication.web;

import java.util.List;

import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import droolsbook.bank.model.User;

// @extract-start 08 04
@Controller
public class TaskListController  {
  @Autowired
  private TaskService localTaskService;
  @Autowired
  private WebSessionUtils webSessionUtils;

  @RequestMapping("/taskList.htm")
  public String taskList(Model model) {
    User user = webSessionUtils.getUser();
    List<TaskSummary> tasks = localTaskService
        .getTasksAssignedAsPotentialOwner(user.getUserId(),
            user.getLanguage());
    model.addAttribute("tasks", tasks);
    return "taskList";
  }
}
// @extract-end
