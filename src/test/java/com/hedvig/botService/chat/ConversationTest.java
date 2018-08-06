package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import static com.hedvig.botService.testHelpers.MessageHelpers.createSingleSelectMessage;
import static com.hedvig.botService.testHelpers.MessageHelpers.createTextMessage;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class ConversationTest {

  public static final String TESTMESSAGE_ID = "testmessage";

  @Mock(answer = Answers.CALLS_REAL_METHODS)
  Conversation sut;

  UserContext uc;

  @Spy MemberChat mc;
  @Captor ArgumentCaptor<Message> messageCaptor;

  @Before
  public void setup() {
    uc = new UserContext();
    uc.setMemberChat(mc);
  }

  @Test
  public void addToChat_renders_selectLink() throws Exception {
    // Arrange
    uc.putUserData("{TEST}", "localhost");

    Message m =
        createSingleSelectMessage(
            "En förklarande text",
            new SelectLink(
                "Länk text",
                "selected.value",
                null,
                "bankid:///{TEST}/text",
                "http://{TEST}/text",
                false));

    // ACT
    sut.addToChat(m, uc);

    // Assert
    then(mc).should().addToHistory(messageCaptor.capture());
    MessageBodySingleSelect body = (MessageBodySingleSelect) messageCaptor.getValue().body;

    SelectLink link = (SelectLink) body.choices.get(0);
    assertThat(link.appUrl).isEqualTo("bankid:///localhost/text");
    assertThat(link.webUrl).isEqualTo("http://localhost/text");
  }

  @Test
  public void addToChat_renders_message() throws Exception {
    // Arrange
    uc.putUserData("{REPLACE_THIS}", "kort");

    Message m = createTextMessage("En förklarande {REPLACE_THIS} text");

    // ACT
    sut.addToChat(m, uc);

    // Assert
    then(mc).should().addToHistory(messageCaptor.capture());
    MessageBody body = messageCaptor.getValue().body;

    assertThat(body.text).isEqualTo("En förklarande kort text");
  }
}
