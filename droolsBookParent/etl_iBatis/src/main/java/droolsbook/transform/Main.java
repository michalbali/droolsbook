package droolsbook.transform;

import java.io.IOException;
import java.io.Reader;
import java.sql.SQLException;
import java.util.List;

import org.drools.StatelessSession;

import com.ibatis.common.resources.Resources;
import com.ibatis.sqlmap.client.SqlMapClient;
import com.ibatis.sqlmap.client.SqlMapClientBuilder;

/**
 */
public class Main {

  public static final void main(String[] args)
      throws SQLException, IOException {

    // @extract-start 03 01
    Reader reader = Resources
        .getResourceAsReader("SqlMapConfig.xml");
    SqlMapClient sqlMapClient = SqlMapClientBuilder
        .buildSqlMapClient(reader);
    reader.close();
    // @extract-end

    // @extract-start 03 02
    List customers = sqlMapClient
        .queryForList("findAllCustomers");

    List addresses = sqlMapClient.queryForList(
        "findAddressByCustomerId", new Long(654258));
    // @extract-end
    
    System.out.println("customers:" + customers);
    System.out.println("addresses:" + addresses);

    addresses = sqlMapClient.queryForList(
        "findAddressByCustomerId", new Long(1));
    System.out.println("addresses:" + addresses);

    List accounts = sqlMapClient.queryForList(
        "findAccountByCustomerId", new Long(654258));
    System.out.println("accounts:" + accounts);
  }

}
