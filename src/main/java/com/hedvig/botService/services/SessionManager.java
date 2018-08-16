package com.hedvig.botService.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hedvig.botService.chat.*;
import com.hedvig.botService.enteties.*;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.serviceIntegration.claimsService.ClaimsService;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.web.dto.AddMessageRequestDTO;
import com.hedvig.botService.web.dto.BackOfficeAnswerDTO;
import com.hedvig.botService.web.dto.TrackingDTO;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Objects;
import static com.hedvig.botService.chat.OnboardingConversationDevi.LOGIN;
import static com.hedvig.botService.chat.OnboardingConversationDevi.MESSAGE_START_LOGIN;

/*
 * The services manager is the main controller class for the chat service. It contains all user
 * sessions with chat histories, context etc It is a singleton accessed through the request
 * controller
 */

@Component
@Transactional
public class SessionManager {

  public enum Intent {
    LOGIN, ONBOARDING
  }

  private static Logger log = LoggerFactory.getLogger(SessionManager.class);
  private final UserContextRepository userContextRepository;
  private final MemberService memberService;

  private final ClaimsService claimsService;
  private final ConversationFactory conversationFactory;
  private final TrackingDataRespository trackerRepo;
  private final ObjectMapper objectMapper;

  @Autowired
  public SessionManager(UserContextRepository userContextRepository, MemberService memberService,
      ClaimsService claimsService, ConversationFactory conversationFactory,
      TrackingDataRespository trackerRepo, ObjectMapper objectMapper) {
    this.userContextRepository = userContextRepository;
    this.memberService = memberService;
    this.claimsService = claimsService;
    this.conversationFactory = conversationFactory;
    this.trackerRepo = trackerRepo;
    this.objectMapper = objectMapper;
  }

  public List<Message> getMessages(int i, String hid) {
    log.info("Getting " + i + " messages for user: " + hid);
    List<Message> messages = getAllMessages(hid, null);

    return messages.subList(Math.max(messages.size() - i, 0), messages.size());
  }

  public void saveExpoPushToken(String hid, String pushToken) {
    UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(
        () -> new ResourceNotFoundException("Could not find usercontext for user: " + hid));
    uc.putUserData("PUSH-TOKEN", pushToken);
  }

  public void saveFirebasePushToken(String hid, String pushToken) {
    UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(
        () -> new ResourceNotFoundException("Could not find UserContext for user :" + hid));
    uc.putUserData("FIREBASE-TOKEN", pushToken);
  }

  public String getFirebasePushToken(String hid) {
    UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(() -> new ResourceNotFoundException("Could not find UserContext for user: " + hid));
    return uc.getDataEntry("FIREBASE-TOKEN");
  }

  public void saveTrackingInformation(String hid, TrackingDTO tracker) {
    TrackingEntity cc = new TrackingEntity(hid, tracker);
    trackerRepo.save(cc);
  }

  public String getPushToken(String hid) {
    UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(
        () -> new ResourceNotFoundException("Could not find usercontext for user: " + hid));
    return uc.getDataEntry("PUSH-TOKEN");
  }

  public void receiveEvent(String eventType, String value, String hid) {

    UserContext uc = userContextRepository.findByMemberId(hid)
        .orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

    uc.conversationManager.receiveEvent(eventType, value, conversationFactory, uc);
  }

  public BankIdCollectResponse collect(String hid, String referenceToken) {

    CollectService service = new CollectService(userContextRepository, memberService);

    return service.collect(hid, referenceToken,
        (BankIdChat) conversationFactory.createConversation(OnboardingConversationDevi.class));
  }

  /*
   * Create a new users chat and context
   */
  public void init(String hid, String linkUri) {

    UserContext uc = userContextRepository.findByMemberId(hid).orElseGet(() -> {
      UserContext newUserContext = new UserContext(hid);
      userContextRepository.save(newUserContext);
      return newUserContext;
    });

    uc.putUserData("{LINK_URI}", linkUri);
    uc.putUserData(UserContext.ONBOARDING_COMPLETE, "false");

    userContextRepository.saveAndFlush(uc);
  }

  /*
   * Mark all messages (incl) last input from user deleted
   */
  public void editHistory(String hid) {
    UserContext uc = userContextRepository.findByMemberId(hid)
        .orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
    MemberChat mc = uc.getMemberChat();
    mc.revertLastInput();
    userContextRepository.saveAndFlush(uc);
  }

  public boolean addAnswerFromHedvig(BackOfficeAnswerDTO backOfficeAnswer) {
    UserContext uc = userContextRepository.findByMemberId(backOfficeAnswer.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

    Conversation conversation = getActiveConversationOrStart(uc, MainConversation.class);

    return conversation.addMessageFromBackOffice(uc, backOfficeAnswer.getMsg(), "message.answer");
  }

  public boolean addMessageFromHedvig(AddMessageRequestDTO backOfficeMessage) {
    val uc = userContextRepository.findByMemberId(backOfficeMessage.getMemberId())
        .orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

    Conversation activeConversation = getActiveConversationOrStart(uc, MainConversation.class);
    return activeConversation.addMessageFromBackOffice(uc, backOfficeMessage.getMsg(),
        "message.bo.message");
  }

  private Conversation getActiveConversationOrStart(UserContext uc,
      Class<MainConversation> conversationToStart) {
    return uc.getActiveConversation()
        .map(x -> conversationFactory.createConversation(x.getClassName())).orElseGet(() -> {
          val newConversation = conversationFactory.createConversation(conversationToStart);
          uc.startConversation(newConversation);
          return newConversation;
        });
  }

  /*
   * Mark all messages (incl) last input from user deleted
   */
  public void resetOnboardingChat(String hid) {
    UserContext uc = userContextRepository.findByMemberId(hid)
        .orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));
    MemberChat mc = uc.getMemberChat();

    // Conversations can only be reset during onboarding

    val data = uc.getOnBoardingData();

    if (!data.getUserHasSigned()) {

      String email = uc.getOnBoardingData().getEmail();
      mc.reset(); // Clear chat
      uc.clearContext(); // Clear context

      uc.getOnBoardingData().setEmail(email);

      Conversation onboardingConversation =
          conversationFactory.createConversation(OnboardingConversationDevi.class);
      if (Objects.equals("true", uc.getDataEntry(LOGIN)) == true) {
        uc.startConversation(onboardingConversation, MESSAGE_START_LOGIN);
      } else {
        uc.startConversation(onboardingConversation);
      }

      userContextRepository.saveAndFlush(uc);
    }
  }

  public List<Message> getAllMessages(String hid, Intent intent) {

    /*
     * Find users chat and context. First time it is created
     */

    UserContext uc = userContextRepository.findByMemberId(hid)
        .orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

    val messages = uc.getMessages(intent, conversationFactory);

    userContextRepository.save(uc);

    return messages;
  }

  /*
   * Add the "what do you want to do today" message to the chat
   */
  public void mainMenu(String hid) {
    log.info("Main menu from user: " + hid);

    UserContext uc = userContextRepository.findByMemberId(hid)
        .orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

    Conversation mainConversation = conversationFactory.createConversation(MainConversation.class);
    uc.startConversation(mainConversation);

    userContextRepository.saveAndFlush(uc);
  }

  public void trustlyClosed(String hid) {
    UserContext uc = userContextRepository.findByMemberId(hid).orElseThrow(
        () -> new ResourceNotFoundException("Could not find usercontext for user: " + hid));

    TrustlyConversation tr =
        (TrustlyConversation) conversationFactory.createConversation(TrustlyConversation.class);
    tr.windowClosed(uc);

    userContextRepository.save(uc);
  }

  public void receiveMessage(Message m, String hid) {
    log.info("Recieving messages from user: " + hid);
    try {
      log.info(objectMapper.writeValueAsString(m));
    } catch (JsonProcessingException ex) {
      log.error("Could not convert message to json in order to log: {}", m.toString());
    }

    m.header.fromId = new Long(hid);

    UserContext uc = userContextRepository.findByMemberId(hid)
        .orElseThrow(() -> new ResourceNotFoundException("Could not find usercontext."));

    uc.conversationManager.receiveMessage(m, conversationFactory, uc);
  }

  public void getFabActions(String hid) {

    if (claimsService.getActiveClaims(hid) > 0) {
    }
  }
}
