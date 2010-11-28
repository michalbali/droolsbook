package droolsbook.sampleApplication.server;

import org.drools.SystemEventListenerFactory;
import org.drools.task.service.TaskClient;
import org.drools.task.service.mina.MinaTaskClientConnector;
import org.drools.task.service.mina.MinaTaskClientHandler;

public class MinaTaskClientFactory {

  public static TaskClient create() {
	TaskClient client = new TaskClient(new MinaTaskClientConnector("client 1",
			new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener())));
    client.connect("127.0.0.1", 9123);
    return client;
  }
  
}
