package com.hedvig.botService.chat;


import static com.hedvig.botService.chat.OnboardingConversationDevi.MESSAGE_50K_LIMIT_YES_YES;
import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_FIRSTNAME;
import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_LASTNAME;
import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_MEMBER_ID;
import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_PHONE_NUMBER;
import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_PRODUCT_TYPE;
import static com.hedvig.botService.testHelpers.TestData.TOLVANSSON_SSN;
import static com.hedvig.botService.testHelpers.TestData.addCityToContext;
import static com.hedvig.botService.testHelpers.TestData.addFamilynameToContext;
import static com.hedvig.botService.testHelpers.TestData.addFirstnameToContext;
import static com.hedvig.botService.testHelpers.TestData.addFloorToContext;
import static com.hedvig.botService.testHelpers.TestData.addSsnToContext;
import static com.hedvig.botService.testHelpers.TestData.addStreetToContext;
import static com.hedvig.botService.testHelpers.TestData.addZipCodeToContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.hedvig.botService.chat.Conversation.EventTypes;
import com.hedvig.botService.enteties.SignupCodeRepository;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.services.events.OnboardingQuestionAskedEvent;
import com.hedvig.botService.services.events.RequestObjectInsuranceEvent;
import com.hedvig.botService.services.events.UnderwritingLimitExcededEvent;
import com.hedvig.botService.testHelpers.TestData;
import lombok.val;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationEventPublisher;

@RunWith(MockitoJUnitRunner.class)
public class OnboardingConversationDeviTest {

  @Mock private MemberService memberService;

  @Mock private ProductPricingService productPricingService;

  @Mock private SignupCodeRepository signupRepo;

  @Mock private ApplicationEventPublisher publisher;

  @Mock private ConversationFactory conversationFactory;

  private UserContext userContext;
  private OnboardingConversationDevi testConversation;

  @Before
  public void setup() {
    userContext = new UserContext(TOLVANSSON_MEMBER_ID);
    userContext.putUserData(UserData.HOUSE, TOLVANSSON_PRODUCT_TYPE);

    testConversation =
        new OnboardingConversationDevi(
            memberService, productPricingService, signupRepo, publisher, conversationFactory);
  }

  @Test
  public void ClearMembersAddress_WhenMemberEntersAddressManually() {
    addSsnToContext(userContext, TOLVANSSON_SSN);
    addFirstnameToContext(userContext, TOLVANSSON_FIRSTNAME);
    addFamilynameToContext(userContext, TOLVANSSON_LASTNAME);
    TestData.addBirthDateToContext(userContext, TestData.TOLVANSSON_BIRTH_DATE);

    addFloorToContext(userContext, TestData.TOLVANSSON_FLOOR);
    addStreetToContext(userContext, TestData.TOLVANSSON_STREET);
    addCityToContext(userContext, TestData.TOLVANSSON_CITY);
    addZipCodeToContext(userContext, TestData.TOLVANSSON_ZIP);

    Message m = testConversation.getMessage("message.bankidja");
    val body = (MessageBodySingleSelect) m.body;
    body.choices.get(1).selected = true;

    testConversation.receiveMessage(userContext, m);

    val onBoardingData = userContext.getOnBoardingData();
    assertThat(onBoardingData.getAddressCity()).isNull();
    assertThat(onBoardingData.getAddressStreet()).isNull();
    assertThat(onBoardingData.getAddressZipCode()).isNull();
    assertThat(onBoardingData.getFloor()).isZero();
  }

  @Test
  public void SendNotificationEventOn_HousingUnderWritingLimit() {

    addFirstnameToContext(userContext, TOLVANSSON_FIRSTNAME);
    addFamilynameToContext(userContext, TOLVANSSON_LASTNAME);

    Message m = testConversation.getMessage("message.uwlimit.housingsize");
    m.body.text = TOLVANSSON_PHONE_NUMBER;

    testConversation.receiveMessage(userContext, m);

    then(publisher)
        .should()
        .publishEvent(
            new UnderwritingLimitExcededEvent(
                TOLVANSSON_MEMBER_ID,
                TOLVANSSON_PHONE_NUMBER,
                TOLVANSSON_FIRSTNAME,
                TOLVANSSON_LASTNAME,
                UnderwritingLimitExcededEvent.UnderwritingType.HouseingSize));
  }

  @Test
  public void SendNotificationEventOn_HouseholdUnderWritingLimit() {

    addFirstnameToContext(userContext, TOLVANSSON_FIRSTNAME);
    addFamilynameToContext(userContext, TOLVANSSON_LASTNAME);

    Message m = testConversation.getMessage("message.uwlimit.householdsize");
    m.body.text = TOLVANSSON_PHONE_NUMBER;

    testConversation.receiveMessage(userContext, m);

    then(publisher)
        .should()
        .publishEvent(
            new UnderwritingLimitExcededEvent(
                TOLVANSSON_MEMBER_ID,
                TOLVANSSON_PHONE_NUMBER,
                TOLVANSSON_FIRSTNAME,
                TOLVANSSON_LASTNAME,
                UnderwritingLimitExcededEvent.UnderwritingType.HouseholdSize));
  }

  @Test
  public void SendNotificationEventOn_FriFraga() {
    addFirstnameToContext(userContext, TOLVANSSON_LASTNAME);
    addFamilynameToContext(userContext, TOLVANSSON_FIRSTNAME);

    Message m = testConversation.getMessage("message.frifraga");
    m.body.text = "I wonder if I can get a home insurance, even thouh my name is Tolvan?";

    testConversation.receiveMessage(userContext, m);

    then(publisher)
        .should()
        .publishEvent(new OnboardingQuestionAskedEvent(TOLVANSSON_MEMBER_ID, m.body.text));
  }

  @Test
  public void
  DoNotSendNotificationEvent_WhenMessage_50K_LIMIT_YES_withAnswer_MESSAGE_50K_LIMIT_YES_YES() {
    Message m =
        testConversation.getMessage(
          OnboardingConversationDevi.MESSAGE_50K_LIMIT_YES + ".2");
    val choice =
        ((MessageBodySingleSelect) m.body)
            .choices
            .stream()
            .filter(x -> x.value.equalsIgnoreCase(MESSAGE_50K_LIMIT_YES_YES))
            .findFirst();

    choice.get().selected = true;

    testConversation.receiveMessage(userContext, m);
    then(publisher)
        .should(times(0))
        .publishEvent(
            new RequestObjectInsuranceEvent(TOLVANSSON_MEMBER_ID, TOLVANSSON_PRODUCT_TYPE));
  }

  @Test
  public void SendNotificationEvent_WhenMemberSignedIsCalled_withUserContextValue50K_LIMITeqTRUE() {
    String referenceId = "53bb6e92-5cc7-11e8-8c3b-235d0786c76b";
    userContext.putUserData("{50K_LIMIT}", "true");
    testConversation.memberSigned(referenceId, userContext);
    then(publisher)
        .should(times(1))
        .publishEvent(
            new RequestObjectInsuranceEvent(TOLVANSSON_MEMBER_ID, TOLVANSSON_PRODUCT_TYPE));
  }

  @Test
  public void DoNothing_WhenMemberSignedIsCalled_withUserContextValue50K_LIMITeqNULL() {
    String referenceId = "53bb6e92-5cc7-11e8-8c3b-235d0786c76b";
    testConversation.memberSigned(referenceId, userContext);
    then(publisher)
        .should(times(0))
        .publishEvent(
            new RequestObjectInsuranceEvent(TOLVANSSON_MEMBER_ID, TOLVANSSON_PRODUCT_TYPE));
  }

  @Test
  public void ReturnFalse_WhenChat_IsBeforeHouseChoice() {

    val uc = new UserContext(TOLVANSSON_MEMBER_ID);
    val canAcceptAnswer = testConversation.canAcceptAnswerToQuestion(uc);

    assertThat(canAcceptAnswer).isEqualTo(false);
  }

  @Test
  public void ReturnTrue_WhenUserContext_ContainsHouseChoice() {

    userContext
        .getOnBoardingData()
        .setHouseType(OnboardingConversationDevi.ProductTypes.BRF.toString());

    val canAcceptAnswer = testConversation.canAcceptAnswerToQuestion(userContext);

    assertThat(canAcceptAnswer).isEqualTo(true);
  }

  @Test
  public void AddCorrectStartMessage_WhenInitWithMessageId() {

    testConversation.init(userContext,
      OnboardingConversationDevi.MESSAGE_ONBOARDINGSTART_ASK_NAME);

    assertThat(userContext.getMemberChat().chatHistory)
        .first()
        .hasFieldOrPropertyWithValue(
            "id", OnboardingConversationDevi.MESSAGE_ONBOARDINGSTART_ASK_NAME);
  }

  @Test
  public void AddMessageOnboardingStartShort_WhenCallingInit_WithOutMessageId() {

    testConversation.init(userContext);

    assertThat(userContext.getMemberChat().chatHistory)
        .first()
        .hasFieldOrPropertyWithValue(
            "id", OnboardingConversationDevi.MESSAGE_ONBOARDINGSTART_ASK_NAME);
  }

  @Test
  public void RelayToMessageForslagsstart_FromMessageOboardingstartShort() {

    testConversation.receiveEvent(
        EventTypes.MESSAGE_FETCHED,
        testConversation.findLastChatMessageId(
          OnboardingConversationDevi.MESSAGE_ONBOARDINGSTART_SHORT),
        userContext);

    assertThat(userContext.getMemberChat().chatHistory)
        .first()
        .hasFieldOrPropertyWithValue("id",
          OnboardingConversationDevi.MESSAGE_FORSLAGSTART);
  }

  @Test
  public void MessageForelegStartContainsOptionForExistingMembers() {
    val message = testConversation.getMessage(
      OnboardingConversationDevi.MESSAGE_FORSLAGSTART);

    val body = (MessageBodySingleSelect) message.body;

    assertThat(body.choices)
        .extracting("text", "value")
        .contains(tuple("Jag är redan medlem", "message.bankid.start"));
  }
}
