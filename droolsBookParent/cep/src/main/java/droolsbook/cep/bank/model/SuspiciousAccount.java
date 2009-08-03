package droolsbook.cep.bank.model;

import java.io.Serializable;
import org.apache.commons.lang.builder.ToStringBuilder;

// @extract-start 06 15
/**
 * marks an account as suspicious
 */
public class SuspiciousAccount implements Serializable {
  public enum SuspiciousAccountSeverity {
    MINOR, MAJOR
  }

  private final Long accountNumber;
  private final SuspiciousAccountSeverity severity;

  public SuspiciousAccount(Long accountNumber,
      SuspiciousAccountSeverity severity) {
    this.accountNumber = accountNumber;
    this.severity = severity;
  }

  private transient String toString;

  @Override
  public String toString() {
    if (toString == null) {
      toString = new ToStringBuilder(this).appendSuper(
          super.toString()).append("accountNumber",
          accountNumber).append("severity", severity)
          .toString();
    }
    return toString;
  }

  // @extract-end

  public Long getAccountNumber() {
    return accountNumber;
  }

  public SuspiciousAccountSeverity getSeverity() {
    return severity;
  }
}