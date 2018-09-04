package com.hedvig.botService.enteties;

import com.hedvig.botService.web.dto.TrackingDTO;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
public class TrackingEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter private String utmSource;

  @Getter private String utmMedium;

  @ElementCollection private List<String> utmContent = new ArrayList<String>();

  @Getter private String utmCampaign;

  @ElementCollection private List<String> utmTerm = new ArrayList<String>();

  @Getter private String phoneNumber;

  @NotNull
  @Column(nullable = false)
  @Getter
  @Setter
  private Instant timestamp;

  public TrackingEntity(String memberId, TrackingDTO tracking) {
    this.memberId = memberId;
    this.utmSource = tracking.getUtmSource();
    this.utmMedium = tracking.getUtmMedium();
    this.utmCampaign = tracking.getUtmCampaign();
    this.phoneNumber = tracking.getPhoneNumber();
    this.utmContent = tracking.getUtmContent();
    this.utmTerm = tracking.getUtmTerm();
    this.timestamp = Instant.now();
  }

  @Getter public String memberId;
}
