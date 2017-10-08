package com.hedvig.botService.enteties;

import java.io.Serializable;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;

/*
 * Contains all state information related to a member
 * */
@Entity
public class UserContext implements Serializable {

	private static Logger log = LoggerFactory.getLogger(UserContext.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Getter
    private String memberId;
    
    private boolean onboardingStarted = false;
    private boolean onboardingComplete = false;

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
    	userData.put(key, value);
    }
    
    public UserContext(String memberId) {
    	log.info("Instantiating UserContext for member:" + memberId + " :" + this );
        this.memberId = memberId;
    }
    
    public UserContext() {
    	log.info("Instantiating UserContext " + this );
    }
    
    public Boolean onboardingComplete() {
        return onboardingComplete;
    }

    public Boolean onboardingStarted() {
        return onboardingStarted;
    }
    
    public void onboardingStarted(Boolean started) {
        onboardingStarted = started;
    }
    
    public void onboardingComplete(Boolean complete) {
    	onboardingComplete = complete;
    }
    
}


