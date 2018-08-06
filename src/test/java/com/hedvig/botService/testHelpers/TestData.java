package com.hedvig.botService.testHelpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedvig.botService.enteties.UserContext;

public class TestData {
  public static final String TOLVANSSON_SSN = "19121212-1212";
  public static final String TOLVANSSON_FIRSTNAME = "Tolvan";
  public static final String TOLVANSSON_LASTNAME = "Tolvansson";
  public static final String TOLVANSSON_EMAIL = "tolvan@tolvan.com";
  public static final String TOLVANSSON_MEMBER_ID = "1337";
  public static final String TOLVANSSON_PHONE_NUMBER = "0701212121";

  public static String toJson(Object o) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(o);
  }

  public static void addFirstnametoContext(UserContext userContext, String tolvanssonLastname) {
    userContext.getOnBoardingData().setFamilyName(tolvanssonLastname);
  }

  public static void addLastnameToContext(UserContext userContext, String tolvanssonFirstname) {
    userContext.getOnBoardingData().setFirstName(tolvanssonFirstname);
  }
}
