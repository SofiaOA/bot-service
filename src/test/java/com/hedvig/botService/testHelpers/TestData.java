package com.hedvig.botService.testHelpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedvig.botService.enteties.UserContext;
import java.time.LocalDate;

public class TestData {
  public static final String TOLVANSSON_SSN = "19121212-1212";
  public static final String TOLVANSSON_FIRSTNAME = "Tolvan";
  public static final String TOLVANSSON_LASTNAME = "Tolvansson";
  public static final String TOLVANSSON_EMAIL = "tolvan@tolvan.com";
  public static final String TOLVANSSON_MEMBER_ID = "1337";
  public static final String TOLVANSSON_PHONE_NUMBER = "0701212121";
  public static final String TOLVANSSON_PRODUCT_TYPE = "BRF";
  public static final LocalDate TOLVANSSON_BIRTH_DATE = LocalDate.of(1912, 12, 12);
  public static final int TOLVANSSON_FLOOR = 1;
  public static final String TOLVANSSON_STREET = "Testgatan 1";
  public static final String TOLVANSSON_CITY = "Teststaden";
  public static final String TOLVANSSON_ZIP = "12345";

  public static String toJson(Object o) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(o);
  }

  public static void addFamilynameToContext(UserContext userContext, String tolvanssonLastname) {
    userContext.getOnBoardingData().setFamilyName(tolvanssonLastname);
  }

  public static void addFirstnameToContext(UserContext userContext, String tolvanssonFirstname) {
    userContext.getOnBoardingData().setFirstName(tolvanssonFirstname);
  }

  public static void addSsnToContext(UserContext userContext, String ssn){
    userContext.getOnBoardingData().setSSN(ssn);
  }

  public static void addStreetToContext(UserContext userContext, String street){
    userContext.getOnBoardingData().setAddressStreet(street);
  }

  public static void addFloorToContext(UserContext userContext, int floor){
    userContext.getOnBoardingData().setFloor(floor);
  }

  public static void addBirthDateToContext(UserContext userContext, LocalDate birthDate) {
    userContext.getOnBoardingData().setBirthDate(birthDate);
  }

  public static void addZipCodeToContext(UserContext userContext, String zipCode) {
    userContext.getOnBoardingData().setAddressZipCode(zipCode);
  }

  public static void addCityToContext(UserContext userContext, String city) {
    userContext.getOnBoardingData().setAddressCity(city);
  }
}
