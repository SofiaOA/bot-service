package com.hedvig.botService.chat;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.springframework.stereotype.Component;

/*
*Måndag-Torsdag*
09-18 – Hedvig svarar inom 5 min
18-23 – Hedvig svarar inom 15 min
23-02 – Hedvig svarar i morgon
02-08.59 – Hedvig svarar efter kl. 09

*Fredag*
09-18 – Hedvig svarar inom 5 min
18-23 – Hedvig svarar inom 30 min
23-02 – Hedvig svarar i morgon
02-09.59 – Hedvig svarar efter kl. 10

*Helg och helgdag*
10-23 – Hedvig svarar inom 30 min
23-02 – Hedvig svarar i morgon
02-09.59 – Hedvig svarar efter kl. 10
 */

@Component
public class StatusBuilderImpl implements StatusBuilder {

  @Override
  public String getStatusMessage(Clock c) {

    Instant now = Instant.now(c);
    ZonedDateTime time = now.atZone(ZoneId.of("Europe/Stockholm"));

    final DayOfWeek dayOfWeek = time.getDayOfWeek();
    final int hour = time.getHour();
    switch (dayOfWeek) {
      case MONDAY:
      case TUESDAY:
      case WEDNESDAY:
      case THURSDAY:
        {
          if (hour <= 2) {
            return "Hedvig svarar imorgon";
          }
          if (hour <= 9) {
            return "Hedvig svarar efter kl. 9";
          }
          if (hour < 18) {
            return "Hedvig svarar inom 5 min";
          } else if (hour < 23) {
            return "Hedvig svarar inom 15 min";
          } else {
            return "Hedvig svarar imorgon";
          }
        }
      case FRIDAY:
        {
          if (hour <= 2) {
            return "Hedvig svarar imorgon";
          }
          if (hour <= 9) {
            return "Hedvig svarar efter kl. 9";
          }
          if (hour < 18) {
            return "Hedvig svarar inom 5 min";
          } else if (hour < 23) {
            return "Hedvig svarar inom 30 min";
          } else {
            return "Hedvig svarar imorgon";
          }
        }
      case SATURDAY:
      case SUNDAY:
        {
          if (hour <= 2) {
            return "Hedvig svarar imorgon";
          }
          if (hour <= 10) {
            return "Hedvig svarar efter kl. 10";
          } else if (hour < 23) {
            return "Hedvig svarar inom 30 min";
          } else {
            return "Hedvig svarar imorgon";
          }
        }
    }

    return "";
  }
}
