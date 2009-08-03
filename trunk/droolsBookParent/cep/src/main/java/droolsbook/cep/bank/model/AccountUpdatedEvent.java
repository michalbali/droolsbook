package droolsbook.cep.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import droolsbook.bank.model.Transaction;

public class AccountUpdatedEvent implements Serializable {

  public final Long accountNumber;
  public final BigDecimal balance;  
  public final UUID tranasctionUuid;

  public AccountUpdatedEvent(Long accountNumber,
      BigDecimal amount, BigDecimal balance,
      UUID tranasctionUuid) {
    super();
    this.accountNumber = accountNumber;
    this.balance = balance;
    this.tranasctionUuid = tranasctionUuid;
  }

  public Long getAccountNumber() {
    return accountNumber;
  }

  public BigDecimal getBalance() {
    return balance;
  }
  
  public UUID getTranasctionUuid() {
    return tranasctionUuid;
  }

  // do not override equals and hashCode, this class represents an active entity
  // e.g. each instance is unique

  private transient String toString;

  @Override
  public String toString() {
    if (toString == null) {
      toString = new ToStringBuilder(this).appendSuper(
          super.toString()).append("accountNumber",
          accountNumber).append("balance", balance).toString();
    }
    return toString;
  }
  
}
