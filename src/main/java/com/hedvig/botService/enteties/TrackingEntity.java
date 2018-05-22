package com.hedvig.botService.enteties;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hedvig.botService.web.dto.TrackingDTO;

@Entity
@ToString
public class TrackingEntity {

	private static Logger log = LoggerFactory.getLogger(TrackingEntity.class);
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    private String utmSource;
    
    @Getter
    private String utmMedium;
    
    @ElementCollection
    private List<String> utmContent = new ArrayList<String>();
    
    @Getter
    private String utmCampaign;
    
    @ElementCollection
    private List<String> utmTerm = new ArrayList<String>();

    @Getter
    private String phoneNumber;
    
    
    public TrackingEntity(String memberId, TrackingDTO tracking){
    	this.memberId = memberId;
    	this.utmSource = tracking.getUtmSource();
    	this.utmMedium = tracking.getUtmMedium();
    	this.utmCampaign = tracking.getUtmCampaign();
    	this.phoneNumber = tracking.getPhoneNumber();
    	this.utmContent = tracking.getUtmContent();
    	this.utmTerm = tracking.getUtmTerm();
    }
    
    @Getter
    public String memberId;

}
