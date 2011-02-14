package droolsbook.sampleApplication.server;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

import org.drools.SystemEventListenerFactory;
import org.drools.task.MockUserInfo;
import org.jbpm.task.User;
import org.jbpm.task.service.TaskService;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.mina.MinaTaskServer;

public class HumanTaskServer {

  MinaTaskServer server;
  
  //@PersistenceUnit
  //private EntityManagerFactory emf;
  
  public void init() throws Exception {
    
	EntityManagerFactory emf = Persistence
      .createEntityManagerFactory("org.jbpm.task");  
	  
	TaskService taskService = new TaskService(emf, SystemEventListenerFactory.getSystemEventListener());
    MockUserInfo userInfo = new MockUserInfo();
    taskService.setUserinfo(userInfo);

    TaskServiceSession taskSession = taskService
        .createSession();
    taskSession.addUser(new User("Administrator"));
    taskSession.addUser(new User("123"));
    taskSession.addUser(new User("456"));
    taskSession.addUser(new User("789"));

    server = new MinaTaskServer(taskService);
    Thread thread = new Thread(server);
    thread.start();
    Thread.sleep(500);
  }
  
  public void destroy() {
    server.stop();
  }
  
  //public void setEmf(EntityManagerFactory emf) {
//		this.emf = emf;
//	}

}
