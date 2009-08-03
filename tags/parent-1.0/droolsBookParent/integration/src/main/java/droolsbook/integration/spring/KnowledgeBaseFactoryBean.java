package droolsbook.integration.spring;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.Resource;

//note that builder configuration nor knowledge base configuration is supplied
//maybe easy to add as another constructor arguments? via util.properties?

//doesn't support complex resources types like decision table

// @extract-start 10 02
public class KnowledgeBaseFactoryBean implements FactoryBean {

  private KnowledgeBase knowledgeBase;

  /**
   * builds the knowledge base and caches it
   * @param resourceMap source resources (DRL, RF files ...)
   * @throws IOException in case of problems while reading 
   *          resources
   */
  public KnowledgeBaseFactoryBean(
      Map<Resource, ResourceType> resourceMap)
      throws IOException {
    KnowledgeBuilder builder = KnowledgeBuilderFactory
        .newKnowledgeBuilder();
    for (Entry<Resource, ResourceType> entry : resourceMap
        .entrySet()) {
      builder.add(ResourceFactory.newInputStreamResource(entry
          .getKey().getInputStream()), entry.getValue());
    }

    if (builder.hasErrors()) {
      throw new RuntimeException(builder.getErrors()
          .toString());
    }

    knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
    knowledgeBase.addKnowledgePackages(builder
        .getKnowledgePackages());
  }

  /**
   * returns cached knowledge base
   */
  @Override
  public Object getObject() throws Exception {
    return this.knowledgeBase;
  }

  /**
   * returns the KnowledgeBase class
   */
  @Override
  public Class<KnowledgeBase> getObjectType() {
    return KnowledgeBase.class;
  }

  /**
   * returns true since the knowledge base is a singleton
   */
  @Override
  public boolean isSingleton() {
    return true;
  }
}
// @extract-end
