package droolsbook.decisiontables.bank.model;

import org.joda.time.DateMidnight;
import org.joda.time.Months;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class Account extends droolsbook.bank.model.Account {
  
  // @extract-start 04 35
  private DateMidnight startDate;
  private DateMidnight endDate;  
  
  /**
   * @return number of months between start and end date
   */
  public int getMonthsBetweenStartAndEndDate() {
    if (startDate == null || endDate == null) {
      return 0;
    }
    return Months.monthsBetween(startDate, endDate)
        .getMonths();
  }
  // @extract-end
  
  public DateMidnight getStartDate() {
    return startDate;
  }
  public void setStartDate(DateMidnight startDate) {
    this.startDate = startDate;
  }
  public DateMidnight getEndDate() {
    return endDate;
  }
  public void setEndDate(DateMidnight endDate) {
    this.endDate = endDate;
  }
  
  @Override
  public boolean equals(final Object other) {
    if (this == other)
      return true;
    if (!(other instanceof Account))
      return false;
    Account castOther = (Account) other;
    return new EqualsBuilder()
        .appendSuper(super.equals(other)).append(startDate,
            castOther.startDate).append(endDate,
            castOther.endDate).isEquals();
  }
  
  @Override
  public int hashCode() {
    return new HashCodeBuilder(2059427071, 485636201)
        .appendSuper(super.hashCode()).append(startDate)
        .append(endDate).toHashCode();
  }
  
  @Override
  public String toString() {
    return new ToStringBuilder(this).appendSuper(
        super.toString()).append("startDate", startDate)
        .append("endDate", endDate).toString();
  }
}
