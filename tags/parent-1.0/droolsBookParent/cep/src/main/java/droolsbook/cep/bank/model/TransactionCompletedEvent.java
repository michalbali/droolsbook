/**
 * 
 */
package droolsbook.cep.bank.model;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionCompletedEvent extends TransactionEvent {
  public TransactionCompletedEvent(BigDecimal amount,
      Long fromAccountNumber, Long toAccountNumber,
      UUID tranasctionUuid) {
    super(amount, fromAccountNumber, toAccountNumber, tranasctionUuid);
  }
  
  public TransactionCompletedEvent(BigDecimal amount,
      Long fromAccountNumber) {
    super(amount, fromAccountNumber, null, null);
  } 
}