package droolsbook.sampleApplication.web;

import org.jbpm.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import droolsbook.bank.model.User;

@Controller
public class TaskClaimController {
  @Autowired
  private TaskService localTaskService;
  @Autowired
  private WebSessionUtils webSessionUtils;

  // @extract-start 08 05
  /**
   * claims specified task for the current user
   */
  @RequestMapping("/taskClaim.htm")
  public String taskClaim(Long taskId) {
    User user = webSessionUtils.getUser();
    localTaskService.claim(taskId, user.getUserId());
    return "redirect:taskList.htm";
  }
  // @extract-end

}
