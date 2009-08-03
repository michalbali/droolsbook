package droolsbook.bank.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.drools.WorkingMemory;
import org.drools.event.DefaultRuleFlowEventListener;
import org.drools.event.RuleFlowNodeTriggeredEvent;
import org.drools.event.process.DefaultProcessEventListener;
import org.drools.event.process.ProcessNodeTriggeredEvent;
import org.drools.runtime.process.NodeInstance;

public class TrackingProcessEventListener extends
    DefaultProcessEventListener {

  Collection<NodeInstance> nodesTriggeredCollection = new ArrayList<NodeInstance>();

  @Override
  public void beforeNodeTriggered(
      ProcessNodeTriggeredEvent event) {
    System.out.println(event.getNodeInstance().getNodeName() + " id=" + event.getNodeInstance().getNodeId());
    nodesTriggeredCollection.add(event.getNodeInstance());
  }

  
  public boolean isNodeTriggered(String processId, long nodeId) {
    for (NodeInstance triggeredNodeInstance : nodesTriggeredCollection) {
      if (triggeredNodeInstance.getProcessInstance()
          .getProcessId().equals(processId)
          && triggeredNodeInstance.getNodeId() == nodeId) {
        return true;
      }
    }
    return false;
  }
  
  public void reset() {
    nodesTriggeredCollection.clear();
  }

}
