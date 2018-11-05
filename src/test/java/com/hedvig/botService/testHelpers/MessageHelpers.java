package com.hedvig.botService.testHelpers;

import static com.hedvig.botService.chat.ConversationTest.TESTMESSAGE_ID;

import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBody;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.message.MessageBodyText;
import com.hedvig.botService.enteties.message.MessageHeader;
import com.hedvig.botService.enteties.message.SelectItem;
import java.util.Arrays;

public class MessageHelpers {

  public static Message createSingleSelectMessage(final String text, final SelectItem... items) {
    Arrays.asList(items);

    return createMessage(new MessageBodySingleSelect(text, Arrays.asList(items)));
  }

  public static Message createTextMessage(final String text) {
    return createMessage(new MessageBodyText(text));
  }

  public static Message createMessage(MessageBody body) {
    Message m = new Message();
    m.id = TESTMESSAGE_ID;
    m.globalId = 1;
    m.header = new MessageHeader();
    m.header.fromId = -1L;
    m.header.messageId = 1;
    m.body = body;
    return m;
  }
}
