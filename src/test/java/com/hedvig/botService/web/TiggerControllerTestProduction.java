package com.hedvig.botService.web;

import com.hedvig.botService.BotServiceApplicationTests;
import com.hedvig.botService.services.triggerService.TriggerService;
import com.hedvig.botService.services.triggerService.dto.CreateDirectDebitMandateDTO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static com.hedvig.botService.testHelpers.TestData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TriggerController.class)
@ContextConfiguration(classes = BotServiceApplicationTests.class)
@ActiveProfiles("production")
public class TiggerControllerTestProduction {

  @Autowired MockMvc mockMvc;

  @MockBean TriggerService triggerService;

  @Test
  public void createDDM_returns404_WHEN_environmentEQProduction() throws Exception {

    CreateDirectDebitMandateDTO createDirectDebitMandateDTO =
        new CreateDirectDebitMandateDTO(
            TOLVANSSON_SSN, TOLVANSSON_FIRSTNAME, TOLVANSSON_LASTNAME, TOLVANSSON_EMAIL);

    mockMvc
        .perform(
            post("/hedvig/trigger/_/createDDM")
                .header("hedvig.token", TOLVANSSON_MEMBER_ID)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(toJson(createDirectDebitMandateDTO)))
        .andExpect(status().isNotFound());
  }
}
