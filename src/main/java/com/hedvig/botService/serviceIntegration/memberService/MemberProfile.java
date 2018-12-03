package com.hedvig.botService.serviceIntegration.memberService;

import java.time.LocalDate;
import java.util.Optional;

public class MemberProfile {

  private final String memberId;
  private final String ssn;

  private final String firstName;
  private final String lastName;

  private final Optional<MemberAddress> address;

  private final String email;
  private final String phoneNumber;
  private final String country;

  private final LocalDate birthDate;

  @java.beans.ConstructorProperties({"memberId", "ssn", "firstName", "lastName", "address", "email",
    "phoneNumber", "country", "birthDate"})
  public MemberProfile(String memberId, String ssn, String firstName, String lastName,
    Optional<MemberAddress> address, String email, String phoneNumber, String country,
    LocalDate birthDate) {
    this.memberId = memberId;
    this.ssn = ssn;
    this.firstName = firstName;
    this.lastName = lastName;
    this.address = address;
    this.email = email;
    this.phoneNumber = phoneNumber;
    this.country = country;
    this.birthDate = birthDate;
  }

  public String getMemberId() {
    return this.memberId;
  }

  public String getSsn() {
    return this.ssn;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public Optional<MemberAddress> getAddress() {
    return this.address;
  }

  public String getEmail() {
    return this.email;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getCountry() {
    return this.country;
  }

  public LocalDate getBirthDate() {
    return this.birthDate;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof MemberProfile)) {
      return false;
    }
    final MemberProfile other = (MemberProfile) o;
    final Object this$memberId = this.getMemberId();
    final Object other$memberId = other.getMemberId();
    if (this$memberId == null ? other$memberId != null : !this$memberId.equals(other$memberId)) {
      return false;
    }
    final Object this$ssn = this.getSsn();
    final Object other$ssn = other.getSsn();
    if (this$ssn == null ? other$ssn != null : !this$ssn.equals(other$ssn)) {
      return false;
    }
    final Object this$firstName = this.getFirstName();
    final Object other$firstName = other.getFirstName();
    if (this$firstName == null ? other$firstName != null
      : !this$firstName.equals(other$firstName)) {
      return false;
    }
    final Object this$lastName = this.getLastName();
    final Object other$lastName = other.getLastName();
    if (this$lastName == null ? other$lastName != null : !this$lastName.equals(other$lastName)) {
      return false;
    }
    final Object this$address = this.getAddress();
    final Object other$address = other.getAddress();
    if (this$address == null ? other$address != null : !this$address.equals(other$address)) {
      return false;
    }
    final Object this$email = this.getEmail();
    final Object other$email = other.getEmail();
    if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
      return false;
    }
    final Object this$phoneNumber = this.getPhoneNumber();
    final Object other$phoneNumber = other.getPhoneNumber();
    if (this$phoneNumber == null ? other$phoneNumber != null
      : !this$phoneNumber.equals(other$phoneNumber)) {
      return false;
    }
    final Object this$country = this.getCountry();
    final Object other$country = other.getCountry();
    if (this$country == null ? other$country != null : !this$country.equals(other$country)) {
      return false;
    }
    final Object this$birthDate = this.getBirthDate();
    final Object other$birthDate = other.getBirthDate();
    if (this$birthDate == null ? other$birthDate != null
      : !this$birthDate.equals(other$birthDate)) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $memberId = this.getMemberId();
    result = result * PRIME + ($memberId == null ? 43 : $memberId.hashCode());
    final Object $ssn = this.getSsn();
    result = result * PRIME + ($ssn == null ? 43 : $ssn.hashCode());
    final Object $firstName = this.getFirstName();
    result = result * PRIME + ($firstName == null ? 43 : $firstName.hashCode());
    final Object $lastName = this.getLastName();
    result = result * PRIME + ($lastName == null ? 43 : $lastName.hashCode());
    final Object $address = this.getAddress();
    result = result * PRIME + ($address == null ? 43 : $address.hashCode());
    final Object $email = this.getEmail();
    result = result * PRIME + ($email == null ? 43 : $email.hashCode());
    final Object $phoneNumber = this.getPhoneNumber();
    result = result * PRIME + ($phoneNumber == null ? 43 : $phoneNumber.hashCode());
    final Object $country = this.getCountry();
    result = result * PRIME + ($country == null ? 43 : $country.hashCode());
    final Object $birthDate = this.getBirthDate();
    result = result * PRIME + ($birthDate == null ? 43 : $birthDate.hashCode());
    return result;
  }

  public String toString() {
    return "MemberProfile(memberId=" + this.getMemberId() + ", ssn=" + this.getSsn()
      + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", address="
      + this.getAddress() + ", email=" + this.getEmail() + ", phoneNumber=" + this
      .getPhoneNumber() + ", country=" + this.getCountry() + ", birthDate=" + this.getBirthDate()
      + ")";
  }
}
