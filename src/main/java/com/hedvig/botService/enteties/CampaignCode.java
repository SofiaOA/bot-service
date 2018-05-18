package com.hedvig.botService.enteties;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
@ToString
public class CampaignCode {

	private static Logger log = LoggerFactory.getLogger(CampaignCode.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    public CampaignCode(String memberId, String key, String value){
    	this.memberId = memberId;
    	this.key = key;
    	this.value = value;
    }
    
    public String memberId;
	public String key;
	public String value;

}
