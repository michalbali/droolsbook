package droolsbook.sampleApplication.repository.jpa;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.springframework.stereotype.Repository;

import droolsbook.bank.model.Customer;
import droolsbook.sampleApplication.repository.CustomerRepository;

// @extract-start 08 01
@Repository
public class JPACustomerRepository implements
    CustomerRepository {

  @PersistenceContext(unitName="entityManagerFactory")
  private EntityManager em;

  public Customer findCustomerByUuid(String customerUuid) {
    return em.find(Customer.class, customerUuid);
  }

  public List<Customer> findCustomerByName(String firstName,
      String lastName) {
    return em
        .createQuery(
            "from Customer as c where c.firstName = :first" + 
            " and c.lastName = :last")
        .setParameter("first", firstName).setParameter("last",
            lastName).getResultList();
  }

  /**
   * stores new customer
   */
  public void addCustomer(Customer customer) {
    em.persist(customer);
  }

  /**
   * stores existing customer
   */
  public Customer updateCustomer(Customer customer) {
    return em.merge(customer);
  }
  // @extract-end
  
  @Override
  public List<Customer> findAllCustomers() {
    return em.createQuery("from Customer as c")
        .getResultList();
  }

}
