package droolsbook.bank.service;

import droolsbook.bank.model.Customer;

// @extract-start 03 65
/**
 * service for validating the banking domain
 */
public interface BankingValidationService {
  /**
   * validates given customer and returns validation report
   */
  ValidationReport validate(Customer customer);
}
// @extract-end
