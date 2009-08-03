package droolsbook.bank.service;

import droolsbook.bank.model.Event;

public interface CEPService {

  void notify(Event event);
  
}
