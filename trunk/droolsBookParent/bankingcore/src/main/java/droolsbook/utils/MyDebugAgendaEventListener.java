package droolsbook.utils;

import org.drools.event.rule.ActivationCancelledEvent;
import org.drools.event.rule.ActivationCreatedEvent;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.BeforeActivationFiredEvent;
import org.drools.event.rule.DebugAgendaEventListener;

public class MyDebugAgendaEventListener extends
    DebugAgendaEventListener {

  @Override
  public void activationCreated(ActivationCreatedEvent event) {
    System.err.println(event + " " +event.getActivation() );
  }
  
  @Override
  public void activationCancelled(
      ActivationCancelledEvent event) {
    System.err.println(event + " " +event.getActivation() + " " + event.getCause());
  }
  
  @Override
  public void beforeActivationFired(
      BeforeActivationFiredEvent event) {
    System.err.println(event + " " +event.getActivation());
  
  
  }
  
  @Override
  public void afterActivationFired(
      AfterActivationFiredEvent event) {
    System.err.println(event + " " +event.getActivation());
  }
  
}
