package com.hedvig.botService.session;

import com.hedvig.botService.enteties.DirectDebitMandateTrigger;
import com.hedvig.botService.enteties.DirectDebitRepository;
import com.hedvig.botService.serviceIntegration.paymentService.PaymentService;
import com.hedvig.botService.serviceIntegration.paymentService.dto.DirectDebitResponse;
import com.hedvig.botService.session.exceptions.UnathorizedException;
import com.hedvig.botService.session.triggerService.TriggerService;
import com.hedvig.botService.session.triggerService.dto.CreateDirectDebitMandateDTO;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class TriggerServiceTest {

    public static final String TOLVANSSON_SSN = "19121212-1221";
    public static final String TOLVANSSON_FIRSTNAME = "Tolvan";
    public static final String TOLVANSSON_LAST_NAME = "Tolvansson";
    public static final String TOLVANSSON_EMAIL = "tolvan@tolvansson.se";
    public static final String TOLVANSSON_MEMBERID = "1337";
    public static final String TRIGGER_URL = "http://localhost:8080";
    @Mock
    DirectDebitRepository repo;

    @Mock
    PaymentService pService;

    private TriggerService sut;

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private UUID generatedTriggerId;

    @Before
    public void setUp(){

        sut = new TriggerService(repo, pService);

        given(repo.save(any(DirectDebitMandateTrigger.class))).will(x -> {
            x.getArgumentAt(0, DirectDebitMandateTrigger.class).setId(generatedTriggerId);
            return x;
        });
    }

    @Test
    public void createDirecteDebitMandate_returns_triggerUUID(){
        //arrange

        generatedTriggerId = UUID.randomUUID();

        CreateDirectDebitMandateDTO requestData =
                directDebitMandateRequest(TOLVANSSON_SSN, TOLVANSSON_FIRSTNAME, TOLVANSSON_LAST_NAME, TOLVANSSON_EMAIL);


        //act
        UUID triggerId = sut.createDirectDebitMandate(requestData, TOLVANSSON_MEMBERID);

        //assert
        assertThat(triggerId).isNotNull();
    }

    @Test
    public void createDirectDebitmandate_saves_DirectDebitMandate(){
        //arrange
        generatedTriggerId = UUID.randomUUID();

        CreateDirectDebitMandateDTO requestData =
                directDebitMandateRequest(TOLVANSSON_SSN, TOLVANSSON_FIRSTNAME, TOLVANSSON_LAST_NAME, TOLVANSSON_EMAIL);

        //act

        final UUID triggerURL = sut.createDirectDebitMandate(requestData, TOLVANSSON_MEMBERID);

        //assert

        assertThat(triggerURL).isEqualByComparingTo(generatedTriggerId);
    }

    @Captor
    ArgumentCaptor<DirectDebitMandateTrigger> mandateCaptor;

    @Test
    public void getTriggerUrl_willCall_paymentService() {
        //arrange
        UUID triggerId = UUID.randomUUID();

        DirectDebitMandateTrigger ddm = createDirectDebitMandateTrigger(triggerId, null, TOLVANSSON_MEMBERID);
        given(repo.findOne(triggerId)).willReturn(ddm);

        given(pService.registerTrustlyDirectDebit(TOLVANSSON_FIRSTNAME, TOLVANSSON_LAST_NAME, TOLVANSSON_SSN, TOLVANSSON_EMAIL, TOLVANSSON_MEMBERID)).willReturn(new DirectDebitResponse(TRIGGER_URL));

        //act

        final String actualTriggerUrl = sut.getTriggerUrl(triggerId, TOLVANSSON_MEMBERID);

        //assert
        assertThat(actualTriggerUrl).isEqualTo(TRIGGER_URL);
        then(repo).should().save(mandateCaptor.capture());
        assertThat(mandateCaptor.getValue().getUrl()).isEqualTo(TRIGGER_URL);

    }

    @Test
    public void getTriggerUrl_willNotCall_paymentService_WhenTriggerHasURL() {

        //arrange
        UUID triggerId = UUID.randomUUID();
        DirectDebitMandateTrigger ddm = createDirectDebitMandateTrigger(triggerId, TRIGGER_URL, TOLVANSSON_MEMBERID);

        given(repo.findOne(triggerId)).willReturn(ddm);

        //act
        final String actualTriggerUrl = sut.getTriggerUrl(triggerId, TOLVANSSON_MEMBERID);

        //assert
        assertThat(actualTriggerUrl).isEqualTo(TRIGGER_URL);
        then(pService).should(never()).registerTrustlyDirectDebit(any(),any(),any(),any(),any());
    }

    @Test
    public void getTriggerUrl_willThrow_UnathorizedException_if_memberIdDoesNotMatch() {
        //arrange
        UUID triggerId = UUID.randomUUID();

        DirectDebitMandateTrigger ddm = createDirectDebitMandateTrigger(triggerId, TRIGGER_URL, TOLVANSSON_MEMBERID);
        given(repo.findOne(triggerId)).willReturn(ddm);

        //act
        thrown.expect(UnathorizedException.class);
        final String actualTriggerUrl = sut.getTriggerUrl(triggerId, "1338");

        //assert

    }


    private CreateDirectDebitMandateDTO directDebitMandateRequest(String ssn, String firstName, String lastName, String email) {
        return new CreateDirectDebitMandateDTO(ssn, firstName, lastName, email);
    }

    private DirectDebitMandateTrigger createDirectDebitMandateTrigger(UUID triggerId, String triggerUrl, String tolvanssonMemberid) {
        DirectDebitMandateTrigger ddm = new DirectDebitMandateTrigger();
        ddm.setId(triggerId);
        ddm.setSsn(TOLVANSSON_SSN);
        ddm.setFirstName(TOLVANSSON_FIRSTNAME);
        ddm.setLastName(TOLVANSSON_LAST_NAME);
        ddm.setEmail(TOLVANSSON_EMAIL);
        ddm.setUrl(triggerUrl);
        ddm.setMemberId(tolvanssonMemberid);
        return ddm;
    }

}