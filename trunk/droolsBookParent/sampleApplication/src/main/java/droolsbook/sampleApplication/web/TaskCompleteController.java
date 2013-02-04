package droolsbook.sampleApplication.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.task.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class TaskCompleteController implements Controller {

  @Autowired
  private TaskService client;
  
  @Override
  public ModelAndView handleRequest(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    
    long taskId = Long.parseLong(request.getParameter("taskId"));
    
    client.complete(taskId, "123", null);
    
    return new ModelAndView("redirect:taskList.htm");
  }
  
}
