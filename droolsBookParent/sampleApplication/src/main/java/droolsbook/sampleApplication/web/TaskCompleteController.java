package droolsbook.sampleApplication.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.task.service.TaskClient;
import org.drools.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class TaskCompleteController implements Controller {

  @Autowired
  private TaskClient client;
  
  @Override
  public ModelAndView handleRequest(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    
    long taskId = Long.parseLong(request.getParameter("taskId"));
    
    
    BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
    client.complete(taskId, "123", null,
        operationResponseHandler);
    operationResponseHandler.waitTillDone(5000);
    
    return new ModelAndView("redirect:taskList.htm");
  }
  
}
