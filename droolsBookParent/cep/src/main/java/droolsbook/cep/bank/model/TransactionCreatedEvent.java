/**
 * 
 */
package droolsbook.cep.bank.model;

import java.math.BigDecimal;
import java.util.UUID;

public class TransactionCreatedEvent extends TransactionEvent {
  public TransactionCreatedEvent(BigDecimal amount,
      Long fromAccountNumber, Long toAccountNumber,
      UUID tranasctionUuid) {
    super(amount, fromAccountNumber, toAccountNumber, tranasctionUuid);
  } 
}