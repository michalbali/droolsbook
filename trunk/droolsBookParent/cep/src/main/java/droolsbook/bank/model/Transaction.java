package droolsbook.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Transaction implements Serializable {

  public enum Status {
    INIT, PENDING, COMPLETED, CANCELLED, DENIED
  }

  private UUID uuid;
  private Account accountFrom;
  private Account accountTo;
  private Date date;
  private BigDecimal amount;
  private String currency;
  private Status status = Status.INIT;
  private String description;

  public UUID getUuid() {
    return uuid;
  }
  
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
  
  public Account getAccountFrom() {
    return accountFrom;
  }

  public void setAccountFrom(Account accountFrom) {
    this.accountFrom = accountFrom;
  }

  public Account getAccountTo() {
    return accountTo;
  }

  public void setAccountTo(Account accountTo) {
    this.accountTo = accountTo;
  }

  public Date getDate() {
    return date;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public BigDecimal getValue() {
    return amount;
  }

  public void setAmount(BigDecimal value) {
    this.amount = value;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other)
      return true;
    if (!(other instanceof Transaction))
      return false;
    Transaction castOther = (Transaction) other;
    return new EqualsBuilder().append(accountFrom, castOther.accountFrom)
        .append(accountTo, castOther.accountTo).append(date, castOther.date)
        .append(amount, castOther.amount).append(status,
            castOther.status).append(description,
            castOther.description).append(currency,
            castOther.currency).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(188983213, 1209766869).append(
        accountFrom).append(accountTo).append(date).append(amount).append(
        status).append(description).append(currency)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("accountFrom", accountFrom)
        .append("accountTo", accountTo).append("date", date).append("value",
            amount).append("status", status).append(
            "description", description).append("currency",
            currency).toString();
  }

}
