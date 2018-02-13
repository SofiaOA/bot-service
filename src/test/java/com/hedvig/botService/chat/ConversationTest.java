package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.*;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@RunWith(MockitoJUnitRunner.class)
public class ConversationTest {

    public static final String TESTMESSAGE_ID = "testmessage";

    @Mock(answer = Answers.CALLS_REAL_METHODS)
    Conversation sut;

    UserContext uc;

    @Spy
    MemberChat mc;

    @Before
    public void setup() {
        uc = new UserContext();
        uc.setMemberChat(mc);
    }



    @Captor
    ArgumentCaptor<Message> messageCaptor;


    @Test
    public void addToChat_renders_selectLink() throws Exception {
        //Arrange
        uc.putUserData("{TEST}", "localhost");

        Message m = createSingleSelectMessage("En förklarande text",
                new SelectLink("Länk text", "selected.value", null, "bankid:///{TEST}/text", "http://{TEST}/text", false));

        //ACT
        sut.addToChat(m, uc);

        //Assert
        then(mc).should().addToHistory(messageCaptor.capture());
        MessageBodySingleSelect body = (MessageBodySingleSelect) messageCaptor.getValue().body;

        SelectLink link = (SelectLink) body.choices.get(0);
        assertThat(link.appUrl).isEqualTo("bankid:///localhost/text");
        assertThat(link.webUrl).isEqualTo("http://localhost/text");

    }

    @Test
    public void addToChat_renders_message() throws Exception {
        //Arrange
        uc.putUserData("{REPLACE_THIS}", "kort");

        Message m = createTextMessage("En förklarande {REPLACE_THIS} text");

        //ACT
        sut.addToChat(m, uc);

        //Assert
        then(mc).should().addToHistory(messageCaptor.capture());
        MessageBody body =  messageCaptor.getValue().body;

        assertThat(body.text).isEqualTo("En förklarande kort text");

    }

    private Message createSingleSelectMessage(final String text, final SelectItem... items) {
        Arrays.asList(items);

        return createMessage(new MessageBodySingleSelect(text, Arrays.asList(items)));
    }

    private Message createTextMessage(final String text) {
        return createMessage(new MessageBodyText(text));
    }

    private Message createMessage(MessageBody body) {
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