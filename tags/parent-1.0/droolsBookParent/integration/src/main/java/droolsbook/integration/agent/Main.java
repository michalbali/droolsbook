package droolsbook.integration.agent;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.agent.KnowledgeAgent;
import org.drools.agent.KnowledgeAgentConfiguration;
import org.drools.agent.KnowledgeAgentFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceChangeScannerConfiguration;
import org.drools.io.ResourceFactory;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// Set the interval on the ResourceChangeScannerService if you are to use it and default of 60s is not desirable.
		 ResourceChangeScannerConfiguration sconf = ResourceFactory.getResourceChangeScannerService().newResourceChangeScannerConfiguration();
		 sconf.setProperty( "drools.resource.scanner.interval",
		                   "10" ); // set the disk scanning interval to 30s, default is 60s
		 ResourceFactory.getResourceChangeScannerService().configure( sconf );

		 KnowledgeBuilder builder = KnowledgeBuilderFactory
		  .newKnowledgeBuilder();
		builder.add(ResourceFactory.newClassPathResource(
		  "validation.drl"), ResourceType.DRL);

		if (builder.hasErrors()) {
		  throw new RuntimeException(builder.getErrors().toString());
		}

		KnowledgeBase knowledgeBase = KnowledgeBaseFactory
		  .newKnowledgeBase();
		knowledgeBase.addKnowledgePackages(
		  builder.getKnowledgePackages());		 
		 
		// @extract-start 10 01
ResourceFactory.getResourceChangeScannerService().start(); 
ResourceFactory.getResourceChangeNotifierService().start();    

KnowledgeAgentConfiguration conf = KnowledgeAgentFactory
  .newKnowledgeAgentConfiguration();
conf.setProperty("drools.agent.scanDirectories", "true");

final KnowledgeAgent agent = KnowledgeAgentFactory
  .newKnowledgeAgent("validation agent", knowledgeBase, conf);
		// @extract-end
		
		System.out.println("hello + " + agent.getKnowledgeBase());
		
	}

}
