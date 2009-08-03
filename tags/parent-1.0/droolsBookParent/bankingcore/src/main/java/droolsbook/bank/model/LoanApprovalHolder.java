package droolsbook.bank.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "app_loan_holder")
public class LoanApprovalHolder implements Serializable {
  @Id private int sessionId;
  private long processInstanceId;
  
  @ManyToOne
  private Customer customer;

  public int getSessionId() {
    return sessionId;
  }

  public void setSessionId(int sessionId) {
    this.sessionId = sessionId;
  }

  public long getProcessInstanceId() {
    return processInstanceId;
  }

  public void setProcessInstanceId(long processInstanceId) {
    this.processInstanceId = processInstanceId;
  }

  public Customer getCustomer() {
    return customer;
  }

  public void setCustomer(Customer customer) {
    this.customer = customer;
  }
  
  
}
