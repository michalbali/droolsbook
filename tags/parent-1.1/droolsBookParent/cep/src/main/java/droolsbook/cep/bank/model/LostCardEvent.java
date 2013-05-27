package droolsbook.cep.bank.model;

public class LostCardEvent {

  public final Long accountNumber;

  public LostCardEvent(Long accountNumber) {
    this.accountNumber = accountNumber;
  }
  
  public Long getAccountNumber() {
    return accountNumber;
  }
  
}
