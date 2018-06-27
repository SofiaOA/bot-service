package com.hedvig.botService.web;

import com.hedvig.botService.BotServiceApplicationTests;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdProgressStatus;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.services.SessionManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = HedvigController.class)
@ContextConfiguration(classes=BotServiceApplicationTests.class)
@ActiveProfiles("test")
public class HedvigControllerTest {

    @MockBean
    private
    SessionManager sessionManager;

    @MockBean
    ProductPricingService productPricingService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void collect() throws Exception {
        final String referenceToken = "someReferenceToken";
        final String memberId = "1337";

        BankIdCollectResponse mockedCollectResponse = new BankIdCollectResponse(BankIdProgressStatus.COMPLETE, referenceToken, memberId);

        when(sessionManager.collect(memberId, referenceToken)).thenReturn(mockedCollectResponse);

        mockMvc
                .perform(
                        post("/hedvig/collect")
                        .param("referenceToken", referenceToken)
                        .header("hedvig.token", memberId))
                .andExpect(jsonPath("$.bankIdStatus").value("COMPLETE"));

    }

    @Test
    public void collect_newMemberID_returns_hedvigId_header() throws Exception {
        final String referenceToken = "someReferenceToken";
        final String requestMemberId = "1337";
        final String responseMemberId = "1338";
        BankIdCollectResponse mockedCollectResponse = new BankIdCollectResponse(BankIdProgressStatus.COMPLETE, referenceToken, responseMemberId);

        when(sessionManager.collect(requestMemberId, referenceToken)).thenReturn(mockedCollectResponse);

        mockMvc
                .perform(
                        post("/hedvig/collect")
                                .param("referenceToken", referenceToken)
                                .header("hedvig.token", requestMemberId))
                .andExpect(jsonPath("$.bankIdStatus").value("COMPLETE"))
                .andExpect(header().string("Hedvig.Id", responseMemberId));
    }

}