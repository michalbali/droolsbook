package droolsbook.cep.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import droolsbook.bank.model.Transaction;

public abstract class TransactionEvent implements Serializable {
  
  public final Long fromAccountNumber;
  public final Long toAccountNumber;
  public final BigDecimal amount;
  public final UUID transactionUuid;
  
  public TransactionEvent(BigDecimal amount,
      Long fromAccountNumber, Long toAccountNumber,
      UUID tranasctionUuid) {
    this.amount = amount;
    this.fromAccountNumber = fromAccountNumber;
    this.toAccountNumber = toAccountNumber;
    this.transactionUuid = tranasctionUuid;
  }
  
  public Long getFromAccountNumber() {
    return fromAccountNumber;
  }

  public Long getToAccountNumber() {
    return toAccountNumber;
  }

  public BigDecimal getAmount() {
    return amount;
  }

  public UUID getTransactionUuid() {
    return transactionUuid;
  }

  //do not override equals and hashCode, this class represents an active entity
  // e.g. each instance is unique

  private transient String toString;

  @Override
  public String toString() {
    if (toString == null) {
      toString = new ToStringBuilder(this).appendSuper(
          super.toString()).append("fromAccountNumber",
          fromAccountNumber).append("toAccountNumber",
          toAccountNumber).append("amount", amount).append(
          "tranasctionUuid", transactionUuid).toString();
    }
    return toString;
  }

}