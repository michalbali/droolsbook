package droolsbook.bank.model;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.lang.builder.ToStringBuilder;


public class Loan implements Serializable {

  private BigDecimal amount;
  private Integer durationYears;
  private Account destinationAccount;
  
  public BigDecimal getAmount() {
    return amount;
  }
  public void setAmount(BigDecimal amount) {
    this.amount = amount;
  }
  public Integer getDurationYears() {
    return durationYears;
  }
  public void setDurationYears(Integer durationYears) {
    this.durationYears = durationYears;
  }
  public Account getDestinationAccount() {
    return destinationAccount;
  }
  public void setDestinationAccount(Account destinationAccount) {
    this.destinationAccount = destinationAccount;
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).appendSuper(
        super.toString()).append("amount", amount).append(
        "durationYears", durationYears).append(
        "destinationAccount", destinationAccount).toString();
  }
  
  
  
}
