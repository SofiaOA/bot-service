package com.hedvig.botService.services.events;

public class UnderwritingLimitExcededEvent {

  @java.beans.ConstructorProperties({"memberId", "phoneNumber", "firstName", "lastName", "kind"})
  public UnderwritingLimitExcededEvent(String memberId, String phoneNumber, String firstName,
    String lastName, UnderwritingType kind) {
    this.memberId = memberId;
    this.phoneNumber = phoneNumber;
    this.firstName = firstName;
    this.lastName = lastName;
    this.kind = kind;
  }

  public String getMemberId() {
    return this.memberId;
  }

  public String getPhoneNumber() {
    return this.phoneNumber;
  }

  public String getFirstName() {
    return this.firstName;
  }

  public String getLastName() {
    return this.lastName;
  }

  public UnderwritingType getKind() {
    return this.kind;
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    }
    if (!(o instanceof UnderwritingLimitExcededEvent)) {
      return false;
    }
    final UnderwritingLimitExcededEvent other = (UnderwritingLimitExcededEvent) o;
    final Object this$memberId = this.getMemberId();
    final Object other$memberId = other.getMemberId();
    if (this$memberId == null ? other$memberId != null : !this$memberId.equals(other$memberId)) {
      return false;
    }
    final Object this$phoneNumber = this.getPhoneNumber();
    final Object other$phoneNumber = other.getPhoneNumber();
    if (this$phoneNumber == null ? other$phoneNumber != null
      : !this$phoneNumber.equals(other$phoneNumber)) {
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
    final Object this$kind = this.getKind();
    final Object other$kind = other.getKind();
    if (this$kind == null ? other$kind != null : !this$kind.equals(other$kind)) {
      return false;
    }
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $memberId = this.getMemberId();
    result = result * PRIME + ($memberId == null ? 43 : $memberId.hashCode());
    final Object $phoneNumber = this.getPhoneNumber();
    result = result * PRIME + ($phoneNumber == null ? 43 : $phoneNumber.hashCode());
    final Object $firstName = this.getFirstName();
    result = result * PRIME + ($firstName == null ? 43 : $firstName.hashCode());
    final Object $lastName = this.getLastName();
    result = result * PRIME + ($lastName == null ? 43 : $lastName.hashCode());
    final Object $kind = this.getKind();
    result = result * PRIME + ($kind == null ? 43 : $kind.hashCode());
    return result;
  }

  public String toString() {
    return "UnderwritingLimitExcededEvent(memberId=" + this.getMemberId() + ", phoneNumber=" + this
      .getPhoneNumber() + ", firstName=" + this.getFirstName() + ", lastName=" + this
      .getLastName() + ", kind=" + this.getKind() + ")";
  }

  public enum UnderwritingType {
    HouseingSize,
    HouseholdSize
  }

  String memberId;
  String phoneNumber;
  String firstName;
  String lastName;
  UnderwritingType kind;
}
