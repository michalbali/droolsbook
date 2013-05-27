/**
 * 
 */
package droolsbook.utils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;

/**
 * @author miba
 * 
 */
public class DroolsHelper {

  public static KnowledgeBase createKnowledgeBase(
      String ruleFile) throws DroolsParserException,
      IOException {
    return createKnowledgeBase(null, ruleFile);
  }
  
  public static KnowledgeBase createKnowledgeBase(
      KnowledgeBaseConfiguration config,      
      String ruleFile)
      throws DroolsParserException, IOException {
    return createKnowledgeBase(config, null, ruleFile);
  }
  
  public static KnowledgeBase createKnowledgeBase(
      KnowledgeBaseConfiguration config,
      KnowledgeBuilderConfiguration knowledgeBuilderConfig,
      String ruleFile) throws DroolsParserException,
      IOException {
    KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory
        .newKnowledgeBuilder(knowledgeBuilderConfig);
    knowledgeBuilder.add(ResourceFactory
        .newClassPathResource(ruleFile), ResourceType.DRL);

    if (knowledgeBuilder.hasErrors()) {
      throw new RuntimeException(knowledgeBuilder.getErrors()
          .toString());
    }

    KnowledgeBase knowledgeBase = KnowledgeBaseFactory
        .newKnowledgeBase(config);
    knowledgeBase.addKnowledgePackages(knowledgeBuilder
        .getKnowledgePackages());
    return knowledgeBase;
  }
  
}
