// @extract-start 03 03
package droolsbook.transform.service;

import java.util.List;
import java.util.Map;

public interface LegacyBankService {

  /**
   * @return all customers
   */
  List<Map<String, Object>> findAllCustomers();

  /**
   * @return addresses for specified customer id
   */
  List<Map<String, Object>> findAddressByCustomerId(
      Long customerId);

  /**
   * @return accounts for specified customer id
   */
  List<Map<String, Object>> findAccountByCustomerId(
      Long customerId);

}
// @extract-end
