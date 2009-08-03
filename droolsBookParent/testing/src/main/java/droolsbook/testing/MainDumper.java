package droolsbook.testing;

import java.io.File;

import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.conf.DumpDirOption;

public class MainDumper {

  public static void main(String[] args) {
    
KnowledgeBuilderConfiguration configuration = 
  KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
configuration.setOption(DumpDirOption.get(
    new File("target/dumpDir")));
    
  }
  
}
