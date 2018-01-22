package com.hedvig.botService.chat;

import java.nio.charset.Charset;

public class BankIDStrings {
    public final static String emoji_smile = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0x81}, Charset.forName("UTF-8"));
    public final static String emoji_hand_ok = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8C}, Charset.forName("UTF-8"));
    public final static String emoji_closed_lock_with_key = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x94, (byte)0x90}, Charset.forName("UTF-8"));
    public final static String emoji_postal_horn = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x93, (byte)0xAF}, Charset.forName("UTF-8"));
    public final static String emoji_school_satchel = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x8E, (byte)0x92}, Charset.forName("UTF-8"));
    public final static String emoji_mag = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x94, (byte)0x8D}, Charset.forName("UTF-8"));
    public final static String emoji_revlolving_hearts = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x92, (byte)0x9E}, Charset.forName("UTF-8"));
    public final static String emoji_tada = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x8E, (byte)0x89}, Charset.forName("UTF-8"));
    public final static String emoji_thumbs_up = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8D}, Charset.forName("UTF-8"));
    public final static String emoji_hug = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0xA4, (byte)0x97}, Charset.forName("UTF-8"));
    public final static String emoji_waving_hand = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x91, (byte)0x8B}, Charset.forName("UTF-8"));
    public final static String emoji_flushed_face = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0x98, (byte)0xB3}, Charset.forName("UTF-8"));
    public final static String emoji_thinking = new String(new byte[]{(byte)0xF0, (byte)0x9F, (byte)0xA4, (byte)0x94}, Charset.forName("UTF-8"));


    public static final String expiredTransactionError = "Fel från bankID: \"BankID-appen svarar inte. Kontrollera att den är startad och att du har internetanslutning. Om du inte har något giltigt BankID kan du hämta ett hos din Bank. Försök sedan igen..\"" + emoji_mag;

    public static final String certificateError = "Fel från bankID: \"Det BankID du försöker använda är för gammalt eller spärrat. Använd ett annat BankID eller hämta ett nytt hos din internetbank.\"";

    public static final String userCancel = "Fel från bankID: \"Åtgärden avbruten.\"";

    public static final String cancelled = "Fel från bankID: \"Åtgärden avbruten. Försök igen.\"";

    public static final String startFailed = "Fel från bankID: \"BankID-appen verkar inte finnas i din dator eller telefon. Installera den och hämta ett BankID hos din internetbank. Installera appen från install.bankid.com.\"";
}
