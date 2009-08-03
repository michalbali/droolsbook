package droolsbook.integration.xstream;

import java.math.BigDecimal;

import org.joda.time.DateMidnight;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonHierarchicalStreamDriver;

import droolsbook.decisiontables.bank.model.Account;

public class Main {

  public static void main(String[] args) {
    XStream xstream = new XStream(new JsonHierarchicalStreamDriver());
    
    DateMidnight date = new DateMidnight(2008, 1, 1);
    
    Account account = new Account(); 
    account.setType(Account.Type.SAVINGS);
    account.setBalance(new BigDecimal("125.00"));
    account.setCurrency("EUR");
    account.setStartDate(date.minusDays(40));
    account.setEndDate(date);
    
    System.out.println(xstream.toXML(account));
    
  }
  
}
