package com.hedvig.botService.enteties;

import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.chat.Conversation.conversationStatus;
import com.hedvig.botService.chat.ConversationFactory;
import com.hedvig.botService.chat.OnboardingConversationDevi;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberProfile;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdSignResponse;
import com.hedvig.botService.services.SessionManager;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Contains all state information related to a member
 * */
@Entity
@ToString
public class UserContext implements Serializable {

    public static final String TRUSTLY_TRIGGER_ID = "{TRUSTLY_TRIGGER_ID}";

	private static Logger log = LoggerFactory.getLogger(UserContext.class);
	private static HashMap<String, String> requiredData = new HashMap<String, String>(){{
		put("{ADDRESS}","T.ex har jag vet jag inte var du bor. Vad har du för gatuadress?");
		put("{ADDRESS_ZIP}", "T.ex har jag inte ditt postnummer?");
		//put("{EMAIL}"); Email is not required to get a quote
		put("{FAMILY_NAME}","T.ex vet jag inte vad heter i efternamn... " + OnboardingConversationDevi.emoji_flushed_face + " ?");
		put("{HOUSE}", "T.ex vet jag inte om du bor i hus eller lägenhet?");
		put("{KVM}","T.ex vet jag inte hur stor din bostad är?");
		put("{SSN}", "T.ex har jag inte ditt personnummer?");
		put("{NAME}", "T.ex vet jag inte vad heter... " + OnboardingConversationDevi.emoji_flushed_face + " ?");
		put("{NR_PERSONS}", "Tex. hur många är ni i hushållet");
		put("{SECURE_ITEMS_NO}", "T.ex skulle jag behöver veta hur många säkerhetsgrejer du har?");
		}};
			
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
	private Long version;
    
    @Getter
    private String memberId;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name="user_data")
    @MapKeyColumn(name="key")
    @Column(name="value", length = 3000)
    private Map<String, String> userData = new HashMap<>();

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name="conversationManager_id")
    private ConversationManager conversationManager;

	@OneToOne(cascade = CascadeType.ALL)
	@Getter
	@Setter
	private MemberChat memberChat;

	@Getter
	@Setter
	@OneToMany(mappedBy = "userContext", cascade = CascadeType.ALL)
	@MapKey(name="referenceToken")
	private Map<String, BankIdSessionImpl> bankIdStatus = new HashMap<>();

	/*
     * Lookup if there is a value for the key in the user context
     * */
    public String getDataEntry(String key){
    	return userData.get(key);
    }

    public void putUserData(String key, String value){
    	log.info("Adding ("+key+":"+value+") to user context:" + this.getMemberId());
    	userData.put(key, value);
    }

	public void removeDataEntry(String key) {
    	userData.remove(key);
	}
    
    private List<ConversationEntity> getConversations(){
    	return this.conversationManager.getConversations();
    }
    
    
    // ------------ Conversation functions ------------------------------- //
    
    /*
     * Start a conversation for a user
     * */

    public void startConversation(Conversation c){
    	log.info("Starting conversation of type:" + c.getClass().getName() + " for user:" + getMemberId());

		if(conversationManager.startConversation(c.getClass())){
			c.init(this);
		}
    }
    
    public void startConversation(Conversation c, String startMessage){
    	log.info("Starting conversation of type:" + c.getClass().getName() + " for user:" + getMemberId() + " with message:" + startMessage);

		if(conversationManager.startConversation(c.getClass(), startMessage)){
			c.init(this, startMessage);
		}

    }
    
    /*
     * Set conversation to COMPLETE
     * */

    public void completeConversation(Conversation conversationClass) {
		this.conversationManager.completeConversation(conversationClass);
	}

	// Check if there is at least one conversation containing name 'Onboarding' with state COMPLETE
    public boolean hasCompletedOnboarding(){
    	for(ConversationEntity c : this.getConversations()){
    		if(c.getClassName().contains("Onboarding") && c.conversationStatus == conversationStatus.COMPLETE){
    			return true;
			}
    	}
    	return false;
    }
    
    // ------------------------------------------------------ //
    
    public UserContext(String memberId) {
        this.memberId = memberId;
        this.conversationManager = new ConversationManager(memberId);
        this.memberChat = new MemberChat(memberId);
        this.memberChat.userContext = this;
    }
    
    public UserContext() {
    }

    public void clearContext(){
    	this.getOnBoardingData().clear();
		this.conversationManager.getConversations().clear();
    	this.memberChat.reset();
    }

	/*
     * Validate that all required information is collected during onboarding and if not enable Hedvig to ask for it
     * */
    public String getMissingDataItem(){
    	for(String s : requiredData.keySet()){
    		if(!userData.containsKey(s)){
    			return requiredData.get(s);
    		}
    	}
    	return null;
    }
    
    public UserData getOnBoardingData() {
        return new UserData(this);
    }

	public BankIdSessionImpl getBankIdCollectStatus(String referenceToken) {
		return bankIdStatus.get(referenceToken);
	}

	public void startBankIdAuth(BankIdAuthResponse bankIdAuthResponse) {

		createCollectType(BankIdSessionImpl.CollectionType.AUTH, bankIdAuthResponse.getReferenceToken(), bankIdAuthResponse.getAutoStartToken());
	}

	public void startBankIdSign(BankIdSignResponse bankIdSignResponse) {

		createCollectType(BankIdSessionImpl.CollectionType.SIGN, bankIdSignResponse.getReferenceToken(), bankIdSignResponse.getAutoStartToken());
	}

	private void createCollectType(BankIdSessionImpl.CollectionType collectionType,

								   String referenceToken,
								   String autoStartToken) {
		BankIdSessionImpl bankIdSessionImpl = new BankIdSessionImpl();
		bankIdSessionImpl.setLastCallTime(Instant.now());
		bankIdSessionImpl.setUserContext(this);
		this.bankIdStatus.put(referenceToken, bankIdSessionImpl);

		bankIdSessionImpl.setCollectionType(collectionType);

		bankIdSessionImpl.setLastStatus("STARTED");
		bankIdSessionImpl.setReferenceToken(referenceToken);
		bankIdSessionImpl.setAutoStartToken(autoStartToken);

		this.putUserData("{AUTOSTART_TOKEN}", autoStartToken);
		this.putUserData("{REFERENCE_TOKEN}", referenceToken);
	}

	public void fillMemberData(MemberProfile member) {
        UserData obd = getOnBoardingData();
        obd.setBirthDate(member.getBirthDate());
        obd.setSSN(member.getSsn());
        obd.setFirstName(member.getFirstName());
        obd.setFamilyName(member.getLastName());

        //obd.setEmail(member.getEmail()); I don't think we will ever get his from bisnode

		member.getAddress().ifPresent((address) -> {
			obd.setAddressStreet(address.getStreet());
			obd.setAddressCity(address.getCity());
			obd.setAddressZipCode(address.getZipCode());
			obd.setFloor(address.getFloor());
		});
    }

    public String replaceWithContext(String input){
        //log.info("Contextualizing string:" + input);
        Pattern pattern = Pattern.compile("\\{(.*?)\\}");
        Matcher m = pattern.matcher(input);
        while (m.find()) {
            String s = m.group();
            String r = getDataEntry(s);
            //log.debug(s + ":" + r);
            if(r!=null){input = input.replace(s, r);}
        }
        //log.debug("-->" + input);
        return input;
    }

    public void addToHistory(Message m) {
        getMemberChat().addToHistory(m);
    }

	public Optional<ConversationEntity> getActiveConversation() {

		return conversationManager.getActiveConversation();
	}

    public void receiveEvent(String eventType, String value, ConversationFactory conversationFactory) {
        Conversation.EventTypes type = Conversation.EventTypes.valueOf(eventType);


        List<ConversationEntity> conversations = new ArrayList<>(getConversations()); //We will add a new element to uc.conversationManager
        for(ConversationEntity c : conversations){

            // Only deliver messages to ongoing conversations
            if(!c.getConversationStatus().equals(conversationStatus.ONGOING))continue;

            try {
                final Class<?> conversationClass = Class.forName(c.getClassName());
                final Conversation conversation = conversationFactory.createConversation(conversationClass);
                conversation.recieveEvent(type, value, this);

            } catch (ClassNotFoundException e) {
                log.error("Could not load conversation from db!", e);
            }
        }
    }

	private void initChat(String startMsg, @NotNull ConversationFactory conversationFactory) {
		putUserData("{WEB_USER}", "FALSE");

		Conversation onboardingConversation = conversationFactory.createConversation(OnboardingConversationDevi.class);
		startConversation(onboardingConversation, startMsg);
	}

	public List<Message> getMessages(SessionManager.Intent intent, ConversationFactory conversationFactory) {
		MemberChat chat = getMemberChat();

		if(getActiveConversation().isPresent() == false) {
			if(intent == SessionManager.Intent.LOGIN) {
				initChat(OnboardingConversationDevi.MESSAGE_START_LOGIN, conversationFactory);
			} else {
				initChat(OnboardingConversationDevi.MESSAGE_WAITLIST_START, conversationFactory);
			}
		}

		val returnList = chat.getMessages();

		if(returnList.size() > 0){
			Message lastMessage = returnList.get(0);
			if(lastMessage!=null) {
				receiveEvent("MESSAGE_FETCHED", lastMessage.id, conversationFactory);
			}
		}else{
			log.info("No messages in chat....");
		}

		return returnList;
	}

	public void receiveMessage(Message m, ConversationFactory conversationFactory) {
		List<ConversationEntity> conversations = new ArrayList<>(getConversations()); //We will add a new element to uc.conversationManager
		for(ConversationEntity c : conversations){

			// Only deliver messages to ongoing conversations
			if(!c.getConversationStatus().equals(conversationStatus.ONGOING))continue;

			try {
				final Class<?> conversationClass = Class.forName(c.getClassName());
				final Conversation conversation = conversationFactory.createConversation(conversationClass);
				conversation.receiveMessage(this, m);

			} catch (ClassNotFoundException e) {
				log.error("Could not load conversation from db!", e);
			}
		}
	}
}


