package droolsbook.stateful;

import droolsbook.bank.model.Customer;
import droolsbook.bank.service.ValidationReport;

// @extract-start 05 01
public interface StatefulService {
  /**
   * registers new objects with this service or notifies this
   * service that an object has been modified
   */
  void insertOrUpdate(Object object);

  /**
   * same as insertOrUpdate(Object object); plus this method 
   * calls insertOrUpdate on child objects
   */
  void insertOrUpdateRecursive(Customer customer);

  /**
   * executes validation rules and returns report 
   */
  ValidationReport executeRules();
  
  /**
   * releases all resources held by this service
   */
  void terminate();
}
// @extract-end