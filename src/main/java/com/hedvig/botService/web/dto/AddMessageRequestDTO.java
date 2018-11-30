package com.hedvig.botService.web.dto;

public class AddMessageRequestDTO extends BackOfficeInputMessageDTO {
  public static final String MESSAGE_ID="message.bo.message";
  public AddMessageRequestDTO () { super (); }

  public AddMessageRequestDTO(String memberId, String msg) {
    this.memberId = memberId;
    this.msg = msg;
  }
}
