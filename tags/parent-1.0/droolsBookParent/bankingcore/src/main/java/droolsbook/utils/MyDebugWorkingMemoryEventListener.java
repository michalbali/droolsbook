package droolsbook.utils;

import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DebugAgendaEventListener;
import org.drools.event.rule.DebugWorkingMemoryEventListener;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.event.rule.ObjectRetractedEvent;
import org.drools.event.rule.ObjectUpdatedEvent;

public class MyDebugWorkingMemoryEventListener extends
    DebugWorkingMemoryEventListener {

  @Override
  public void objectInserted(ObjectInsertedEvent event) {
    System.err.println(event + " " +event.getFactHandle() + " " + event.getPropagationContext());
  }
  @Override
  public void objectRetracted(ObjectRetractedEvent event) {
    System.err.println(event + " " +event.getFactHandle() + " " + event.getPropagationContext());
  }
  
  @Override
  public void objectUpdated(ObjectUpdatedEvent event) {
    System.err.println(event + " " +event.getFactHandle() + " " + event.getPropagationContext());
  }
  
}
