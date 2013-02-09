package droolsbook.sampleApplication.web;

import org.jbpm.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class TaskStartController {

  @Autowired
  private TaskService client;
  
  @RequestMapping("/taskStart.htm")
  public String taskStart(Long taskId) {
    client.start(taskId, "123");
    return "redirect:taskList.htm";
  }
  
}
