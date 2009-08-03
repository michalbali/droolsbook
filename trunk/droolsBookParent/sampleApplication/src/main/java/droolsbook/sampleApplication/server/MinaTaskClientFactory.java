package droolsbook.sampleApplication.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.drools.SystemEventListenerFactory;
import org.drools.task.service.MinaTaskClient;
import org.drools.task.service.TaskClientHandler;

public class MinaTaskClientFactory {

  public static MinaTaskClient create() {
    MinaTaskClient client = new MinaTaskClient("client 1",
        new TaskClientHandler(SystemEventListenerFactory.getSystemEventListener()));
    NioSocketConnector connector = new NioSocketConnector();
    SocketAddress address = new InetSocketAddress("127.0.0.1",
        9123);
    client.connect(connector, address);
    return client;
  }
  
}
