package com.hedvig.botService.enteties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKeyColumn;

import com.hedvig.botService.enteties.userContextHelpers.AutogiroData;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.chat.OnboardingConversationDevi;

import lombok.Getter;

/*
 * Contains all state information related to a member
 * */
@Entity
public class UserContext implements Serializable {
	
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
		put("{SECURE_ITEMS_NO}", "T.ex skulle jag behöver veta hur många säkerhetsgrejer du har?"); // TODO: Redirect...
		}};
			
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Getter
    private String memberId;
    
    private boolean onboardingStarted = false;
    private boolean onboardingComplete = false;
    private boolean initClaimsProcess = false;
    private boolean ongoingClaimsProcess = false;
    private boolean ongoingMainConversation = false;
    
    @ElementCollection
    @CollectionTable(name="user_data")
    @MapKeyColumn(name="key")
    @Column(name="value")
    private Map<String, String> userData = new HashMap<String, String>();

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

    public AutogiroData getAutogiroData(){
        return new AutogiroData(this);
    };
    
    /*
     * Check if user has an ongoing conversation of type conversationClassName
     * */
    public boolean hasOngoingConversation(String conversationClassName){
    	if(conversationClassName.indexOf(".")==-1)conversationClassName = ("com.hedvig.botService.chat." + conversationClassName); // TODO: Refactor/remove hack
    	String c =  getDataEntry("{" +conversationClassName+ "}") ;
    	return(c!=null && c.equals(Conversation.conversationStatus.ONGOING.toString()));
    }

    public void startOngoingConversation(String conversationClassName){
        if(conversationClassName.indexOf(".")==-1)conversationClassName = ("com.hedvig.botService.chat." + conversationClassName); // TODO: Refactor/remove hack
        this.putUserData("{" +conversationClassName+ "}", Conversation.conversationStatus.ONGOING.toString());
    }
    
    /*
     * Set conversation to COMPLETE
     * */
    public void completeConversation(String conversationClassName){
    	if(conversationClassName.indexOf(".")==-1)conversationClassName = ("com.hedvig.botService.chat." + conversationClassName); // TODO: Refactor/remove hack
    	putUserData("{" +conversationClassName+ "}", Conversation.conversationStatus.COMPLETE.toString());
    }
    
    public UserContext(String memberId) {
    	log.info("Instantiating UserContext for member:" + memberId + " :" + this );
        this.memberId = memberId;
    }
    
    public UserContext() {
    	log.info("Instantiating UserContext " + this );
    }
    
    public Boolean claimsProcessInitiated(){
    	return initClaimsProcess;
    }
    
    public void initClaim(){
    	initClaimsProcess = true;
    }
    
    public void claimStarted(){
    	initClaimsProcess = false;
    }
    
    public Boolean onboardingComplete() {
        return onboardingComplete;
    }

    public Boolean onboardingStarted() {
        return onboardingStarted;
    }
    
    public Boolean ongoingClaimsProcess() {
        return ongoingClaimsProcess;
    }
    
    public Boolean ongoingMainConversation(){
    	return ongoingMainConversation;
    }
    
    public void startMainConversation(){
    	ongoingMainConversation = true;
    }
    
    public void endMainConversation(){
    	ongoingMainConversation = false;
    }
    
    public void ongoingClaimsProcess(Boolean ongoing) {
        ongoingClaimsProcess = ongoing;
    }
    
    public void onboardingStarted(Boolean started) {
        onboardingStarted = started;
    }
    
    public void clearContext(){
    	this.userData.clear();
    }
    
    public void onboardingComplete(Boolean complete) {
    	onboardingComplete = complete;
    }

    public void mockMe(){
    	putUserData("{ADDRESS}", "Margaretavägen 8D");
    	putUserData("{ADDRESS_ZIP}","18774");
    	putUserData("{AUTOSTART_TOKEN}","cbead301-1796-401e-bec3-26d67fd7bc9e");
    	putUserData("{com.hedvig.botService.chat.OnboardingConversationDevi}","ONGOING");
    	putUserData("{EMAIL}","johan@hedvi.com");
    	putUserData("{FAMILY_NAME}","Tjelldén");
    	putUserData("{HOUSE}","bostdsrätt");
    	putUserData("{INSURANCE_COMPANY_TODAY}","trygg-hansa");
    	putUserData("{KVM}","123");
    	putUserData("{MEMBER_BIRTH_DATE}","1984-09-18");
    	putUserData("{NAME}","Johan");
    	putUserData("{NR_PERSONS}","3");
    	putUserData("{REFERENCE_TOKEN}","a903a93f-a53a-4b4a-93a4-9cf4a4c9f15e");
    	putUserData("{SECURE_ITEM_0}","safety.extinguisher");
    	putUserData("{SECURE_ITEM_1}","safety.door");
    	putUserData("{SECURE_ITEMS_NO}","2");
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
}


