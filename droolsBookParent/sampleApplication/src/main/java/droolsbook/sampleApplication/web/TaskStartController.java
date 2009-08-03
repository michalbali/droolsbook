package droolsbook.sampleApplication.web;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.task.query.TaskSummary;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.TaskClientHandler;
import org.drools.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.RedirectView;

public class TaskStartController implements Controller {

  @Autowired
  private MinaTaskClient client;
  
  @Override
  public ModelAndView handleRequest(
      HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    
    long taskId = Long.parseLong(request.getParameter("taskId"));
    
    
    BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
    client
        .start(taskId, "123", operationResponseHandler);
    operationResponseHandler.waitTillDone(5000);
    
    return new ModelAndView("redirect:taskList.htm");
  }
  
}
