package droolsbook.transform.service.impl;

import java.sql.SQLException;
import java.util.List;

import com.ibatis.sqlmap.client.SqlMapClient;

import droolsbook.transform.service.LegacyBankService;

public class IBatisLegacyBankService implements
    LegacyBankService {

  private SqlMapClient sqlMapClient;
  
  public IBatisLegacyBankService(SqlMapClient sqlMapClient) {
    this.sqlMapClient = sqlMapClient;
  }
  
  public List findAccountByCustomerId(Long customerId) {
    try {
      System.out.println("Executing findAccountByCustomerId with customerId=" + customerId);
      return sqlMapClient.queryForList("findAccountByCustomerId", customerId);
    } catch (SQLException e) {      
      throw new RuntimeException(e);
    }
  }

  public List findAddressByCustomerId(Long customerId) {
    try {
      System.out.println("Executing findAddressByCustomerId with customerId=" + customerId);
      return sqlMapClient.queryForList("findAddressByCustomerId", customerId);
    } catch (SQLException e) {      
      throw new RuntimeException(e);
    }
  }

  public List findAllCustomers() {
    try {
      System.out.println("Executing findAllCustomers");
      return sqlMapClient.queryForList("findAllCustomers");
    } catch (SQLException e) {      
      throw new RuntimeException(e);
    }
  }

}
