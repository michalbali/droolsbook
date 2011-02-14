package droolsbook.sampleApplication.server;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.h2.server.web.WebServer;
import org.h2.tools.Server;

public class H2DBServer {

  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  private Server webServer;
  
  public void init() throws SQLException {
    logger.debug("Initializing");
    webServer = Server.createWebServer().start();
  }

  public void destroy() {
    System.out.println("term");
    webServer.stop();
  }
  
  public static void main(String[] args) throws Exception {
    H2DBServer server = new H2DBServer();
    server.init();
    System.in.read();
    server.destroy();
  }

}
