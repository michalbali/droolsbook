package droolsbook.cep;

import java.util.ArrayList;
import java.util.List;

import org.drools.WorkingMemory;
import org.drools.event.rule.AfterActivationFiredEvent;
import org.drools.event.rule.DefaultAgendaEventListener;

// @extract-start 06 16
public class TrackingAgendaEventListener extends
    DefaultAgendaEventListener {
  List<String> rulesFiredList = new ArrayList<String>();

  @Override
  public void afterActivationFired(
      AfterActivationFiredEvent event) {
    rulesFiredList.add(event.getActivation().getRule()
        .getName());
  }

  public boolean isRuleFired(String ruleName) {
    for (String firedRuleName : rulesFiredList) {
      if (firedRuleName.equals(ruleName)) {
        return true;
      }
    }
    return false;
  }

  public void reset() {
    rulesFiredList.clear();
  }
}
// @extract-end
