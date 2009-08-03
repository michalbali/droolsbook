package droolsbook.cep.bank.model;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;

public class Account extends droolsbook.bank.model.Account {

  public enum Status {
    PENDING, ACTIVE, TERMINATED, BLOCKED
  }
  
  private Status status;

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other)
      return true;
    if (!(other instanceof Account))
      return false;
    Account castOther = (Account) other;
    return new EqualsBuilder()
        .appendSuper(super.equals(other)).append(status,
            castOther.status).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(-1439427131, -51927269)
        .appendSuper(super.hashCode()).append(status)
        .toHashCode();
  }
  
  
  
}
