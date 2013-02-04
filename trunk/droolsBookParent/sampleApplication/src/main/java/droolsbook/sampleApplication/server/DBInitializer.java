package droolsbook.sampleApplication.server;

import org.drools.task.MockUserInfo;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;

public class DBInitializer {

  private TaskService taskService;
  
  public void init() throws Exception {
        
    MockUserInfo userInfo = new MockUserInfo();
    taskService.setUserinfo(userInfo);

    TaskServiceSession taskSession = taskService
        .createSession();
    taskSession.addUser(new User("Administrator"));
    taskSession.addUser(new User("123"));
    taskSession.addUser(new User("456"));
    taskSession.addUser(new User("789"));
  }
  
  public void setTaskService(TaskService taskService) {
    this.taskService = taskService;
  }
  
}
