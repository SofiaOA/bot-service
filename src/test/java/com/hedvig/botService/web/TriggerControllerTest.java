package com.hedvig.botService.web;

import com.hedvig.botService.BotServiceApplicationTests;
import com.hedvig.botService.session.triggerService.TriggerService;
import com.hedvig.botService.session.exceptions.UnathorizedException;
import com.hedvig.botService.testHelpers.TestData;
import com.hedvig.botService.session.triggerService.dto.CreateDirectDebitMandateDTO;
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

import java.util.UUID;

import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_MEMBER_ID;
import static com.hedvig.botService.testHelpers.TestData.toJson;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TriggerController.class)
@ActiveProfiles("development")
@ContextConfiguration(classes=BotServiceApplicationTests.class)
public class TriggerControllerTest {

    @MockBean
    private TriggerService triggerService;

    @Autowired
    private MockMvc mockMvc;


    @Test
    public void returns_401_if_trigger_does_not_belong_to_member() throws Exception {
        final UUID triggerId = UUID.randomUUID();


        //noinspection unchecked
        given(triggerService.getTriggerUrl(triggerId, TestData.TOLVANSSON_MEMBER_ID)).willThrow(UnathorizedException.class);

        mockMvc
                .perform(
                        post("/hedvig/trigger/" + triggerId.toString())
                                .header("hedvig.token", TestData.TOLVANSSON_MEMBER_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void trigger_returns_trigger_url() throws Exception {
        final UUID triggerId = UUID.randomUUID();
        final String triggerURL = "http://localhost:8080";


        given(triggerService.getTriggerUrl(triggerId, TestData.TOLVANSSON_MEMBER_ID)).willReturn(triggerURL);

        mockMvc
                .perform(
                        post("/hedvig/trigger/" + triggerId.toString())
                                .header("hedvig.token", TestData.TOLVANSSON_MEMBER_ID))
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.url").value(triggerURL))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }

    @Test
    public void no_trigger_return_404() throws Exception {
        final UUID triggerId = UUID.randomUUID();

        given(triggerService.getTriggerUrl(triggerId, TestData.TOLVANSSON_MEMBER_ID)).willReturn(null);

        mockMvc
                .perform(
                        post("/hedvig/trigger/" + triggerId.toString())
                                .header("hedvig.token", TestData.TOLVANSSON_MEMBER_ID))
                .andExpect(status().isNotFound());
    }

    @Test
    public void triggerId_isNotValid_UUID() throws Exception {
        final UUID triggerId = UUID.randomUUID();

        given(triggerService.getTriggerUrl(triggerId, TestData.TOLVANSSON_MEMBER_ID)).willReturn(null);

        mockMvc
                .perform(
                        post("/hedvig/trigger/" + "blablabla")
                                .header("hedvig.token", TestData.TOLVANSSON_MEMBER_ID)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createTriggerWorksInDevelopmentProfile() throws Exception {
        final UUID triggerId = UUID.randomUUID();

        CreateDirectDebitMandateDTO createDirectDebitMandateDTO = new CreateDirectDebitMandateDTO(
                TestData.TOLVANSSON_SSN,
                TestData.TOLVANSSON_FIRSTNAME,
                TestData.TOLVANSSON_LASTNAME,
                TestData.TOLVANSSON_EMAIL
        );

        given(triggerService.createDirectDebitMandate(createDirectDebitMandateDTO, TOLVANSSON_MEMBER_ID)).willReturn(triggerId);

        mockMvc.perform(
                post("/hedvig/trigger/_/createDDM").
                        header("hedvig.token", TestData.TOLVANSSON_MEMBER_ID).
                        accept(MediaType.APPLICATION_JSON_UTF8).
                        contentType(MediaType.APPLICATION_JSON_UTF8).
                        content(toJson(createDirectDebitMandateDTO))).
                andExpect(jsonPath("$.id").value(triggerId.toString()));
    }

}

