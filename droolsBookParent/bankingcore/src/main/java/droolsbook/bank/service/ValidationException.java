package droolsbook.bank.service;

import org.apache.commons.lang.builder.ToStringBuilder;

public class ValidationException extends RuntimeException {

  private ValidationReport validationReport;
  
  public ValidationException(ValidationReport report) {
    this.validationReport = report;
  }
  
  public ValidationReport getValidationReport() {
    return validationReport;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).appendSuper(
        super.toString()).append("validationReport",
        validationReport).toString();
  }

}
