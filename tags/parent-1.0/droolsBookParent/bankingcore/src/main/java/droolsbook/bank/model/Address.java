/**
 * 
 */
package droolsbook.bank.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author miba
 * 
 */
@Entity
@Table(name = "app_address")
public class Address implements Serializable {

  public enum Country {
    USA, China, Ireland, Slovakia
  };

  private String addressLine1;
  private String addressLine2;
  private String postalCode;
  private String city;
  private Country country;
  @Id private String uuid;
  
  public String getUuid() {
    return uuid;
  }
  
  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public String getAddressLine1() {
    return addressLine1;
  }

  public void setAddressLine1(String addressLine1) {
    this.addressLine1 = addressLine1;
  }

  public String getAddressLine2() {
    return addressLine2;
  }

  public void setAddressLine2(String addressLine2) {
    this.addressLine2 = addressLine2;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }

  @Override
  public boolean equals(final Object other) {
    if (this == other)
      return true;
    if (!(other instanceof Address))
      return false;
    Address castOther = (Address) other;
    return new EqualsBuilder().append(addressLine1,
        castOther.addressLine1).append(addressLine2,
        castOther.addressLine2).append(postalCode,
        castOther.postalCode).append(city, castOther.city)
        .append(country, castOther.country).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(69555679, 678798999).append(
        addressLine1).append(addressLine2).append(postalCode)
        .append(city).append(country).toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("addressLine1",
        addressLine1).append("addressLine2", addressLine2)
        .append("postalCode", postalCode).append("city", city)
        .append("country", country).toString();
  }

}
