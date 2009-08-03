package droolsbook.cep.bank.model;

import java.util.UUID;

public class SuspiciousTransaction {

  public enum SuspiciousTransactionSeverity {
    MINOR, MAJOR
  }
  
  private final UUID transactionUuid;  
  private final SuspiciousTransactionSeverity severity;

  public SuspiciousTransaction(UUID transactionUuid, SuspiciousTransactionSeverity severity) {
    this.transactionUuid = transactionUuid;
    this.severity = severity;
  }
  
}
