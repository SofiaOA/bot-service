package com.hedvig.botService.enteties.message;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import lombok.ToString;

@Entity
@ToString
public class MessageHeader {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Integer messageId;

  /*
   * Header elements
   * */
  public Long fromId;
  public Long timeStamp; // Time when sent/recieved on API-GW
  public String loadingIndicator; // Link to animation to show during load
  public String avatarName; // Link to avatar animation to show over message
  public Long pollingInterval; // Frequency of next request

  public String statusMessage = null;

  @Transient
  public boolean editAllowed; // For client use to indicate if the last message is editable

  public Boolean
      shouldRequestPushNotifications; // Should responding to this message prompt user to turn on
  // push notifications

  public MessageHeader(long hedvigUserId, long timeStamp) {
    this.fromId = hedvigUserId;
    this.timeStamp = timeStamp;
    this.pollingInterval = 1000L; // Default value = 1s
    this.loadingIndicator = "loader"; // Default value
    this.shouldRequestPushNotifications = false;
  }

  public MessageHeader(long hedvigUserId, long timeStamp, boolean shouldRequestPushNotifications) {
    this.fromId = hedvigUserId;
    this.timeStamp = timeStamp;
    this.pollingInterval = 1000L;
    this.loadingIndicator = "loader";
    this.shouldRequestPushNotifications = shouldRequestPushNotifications;
  }

  public MessageHeader() {}
}
