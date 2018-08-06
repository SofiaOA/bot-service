package com.hedvig.botService.web.dto;

public class AvatarDTO {

  public String name;
  public String URL;
  public int width;
  public int height;
  public long duration;

  public AvatarDTO() {}

  public AvatarDTO(String name, String URL, int width, int height, long duration) {

    this.name = name;
    this.URL = URL;
    this.width = width;
    this.height = height;
    this.duration = duration;
  }
}
