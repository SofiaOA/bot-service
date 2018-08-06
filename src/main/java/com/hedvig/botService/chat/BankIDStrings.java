package com.hedvig.botService.chat;

import java.nio.charset.Charset;

public class BankIDStrings {
  public static final String emoji_smile =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0x81},
          Charset.forName("UTF-8"));
  public static final String emoji_hand_ok =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8C},
          Charset.forName("UTF-8"));
  public static final String emoji_closed_lock_with_key =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x94, (byte) 0x90},
          Charset.forName("UTF-8"));
  public static final String emoji_postal_horn =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x93, (byte) 0xAF},
          Charset.forName("UTF-8"));
  public static final String emoji_school_satchel =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x8E, (byte) 0x92},
          Charset.forName("UTF-8"));
  public static final String emoji_mag =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x94, (byte) 0x8D},
          Charset.forName("UTF-8"));
  public static final String emoji_revlolving_hearts =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x92, (byte) 0x9E},
          Charset.forName("UTF-8"));
  public static final String emoji_tada =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x8E, (byte) 0x89},
          Charset.forName("UTF-8"));
  public static final String emoji_thumbs_up =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8D},
          Charset.forName("UTF-8"));
  public static final String emoji_hug =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0xA4, (byte) 0x97},
          Charset.forName("UTF-8"));
  public static final String emoji_waving_hand =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x91, (byte) 0x8B},
          Charset.forName("UTF-8"));
  public static final String emoji_flushed_face =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0x98, (byte) 0xB3},
          Charset.forName("UTF-8"));
  public static final String emoji_thinking =
      new String(
          new byte[] {(byte) 0xF0, (byte) 0x9F, (byte) 0xA4, (byte) 0x94},
          Charset.forName("UTF-8"));

  public static final String expiredTransactionError =
      "Hmm, det verkar inte som att ditt BankID svarar. Testa igen!";

  public static final String certificateError =
      "Det BankID du försöker använda är för gammalt eller spärrat. Hämta ett nytt genom din internetbank och testa sen igen!";

  public static final String userCancel =
      "Hmm, det verkar inte som att ditt BankID svarar. Testa igen!";

  public static final String cancelled =
      "Hmm, det verkar inte som att ditt BankID svarar. Testa igen!";

  public static final String startFailed =
      "Hmm. Det verkar inte som att du har någon BankID-app på din telefon. Ladda ner appen och hämta ett BankID från din internetbank, testa sen igen!";
}
