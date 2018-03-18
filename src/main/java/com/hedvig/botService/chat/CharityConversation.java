package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.message.SelectItem;
import com.hedvig.botService.enteties.message.SelectOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class CharityConversation extends Conversation {

    private final Logger log = LoggerFactory.getLogger(CharityConversation.class);
    private final ConversationFactory conversationFactory;

    public CharityConversation(ConversationFactory factory) {
        super();
        this.conversationFactory = factory;

        createChatMessage("message.kontrakt.charity",
                new MessageBodySingleSelect("En grej till! \f"
                        +"Som Hedvig-medlem får du välja en välgörenhetsorganisation att stödja om det blir pengar över när alla skador har betalats",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("SOS Barnbyar", "charity.sosbarnbyar"));
                            add(new SelectOption("Barncancerfonden", "charity.barncancerfonden"));
                            add(new SelectOption("Berätta mer", "message.kontrakt.charity.tellmemore"));
                        }}
                ));

        createChatMessage("message.kontrakt.charity.tellmemore",
                new MessageBodySingleSelect("Så här, jag fungerar inte som ett vanligt försäkringsbolag\f" +
                        "Jag tar ut en fast avgift för att kunna ge dig bra service\f" +
                        "Resten av det du betalar öronmärks för att ersätta skador\f" +
                        "När alla skador har betalats skänks överskottet till organisationer som gör världen bättre\f" +
                        "Du väljer själv vad ditt hjärta klappar för!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("SOS Barnbyar", "charity.sosbarnbyar"));
                            add(new SelectOption("Cancerfonden", "charity.cancerfonden"));
                        }}
                ));

        createMessage("message.kontrakt.charity.tack",
                new MessageBodySingleSelect("Toppen, tack!",
                        new ArrayList<SelectItem>() {{
                            //add(new SelectLink("Börja utforska appen", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ));
    }

    @Override
    public List<SelectItem> getSelectItemsForAnswer(UserContext uc) {
        return null;
    }

    @Override
    public boolean canAcceptAnswerToQuestion() {
        return false;
    }

    @Override
    public void receiveMessage(UserContext userContext, MemberChat memberChat, Message m) {

        String nxtMsg = "message.kontrakt.charity";
        switch (m.id) {
            case "message.kontrakt.charity.tellmemore.8":
            case "message.kontrakt.charity.2":

                MessageBodySingleSelect mss = (MessageBodySingleSelect) m.body;
                final SelectItem selectedItem = mss.getSelectedItem();
                if(selectedItem.value.startsWith("charity")){
                    m.body.text = "Jag vill att mitt överskott ska gå till " + selectedItem.text;
                    addToChat(m, userContext);
                    userContext.putUserData("{CHARITY}", selectedItem.value);
                    nxtMsg = "message.kontrakt.charity.tack";
                    addToChat(getMessageId(nxtMsg), userContext);
                    userContext.completeConversation(this.getClass().getName());
                    userContext.startConversation(conversationFactory.createConversation(TrustlyConversation.class));
                    return;
                }
                else{
                    m.body.text = selectedItem.text;
                    nxtMsg = selectedItem.value;
                    addToChat(m, userContext);
                }
                break;
        }

        completeRequest(nxtMsg, userContext, memberChat);
    }

    @Override
    public void recieveEvent(EventTypes e, String value, UserContext userContext, MemberChat memberChat){

        switch(e){
            // This is used to let Hedvig say multiple message after another
            case MESSAGE_FETCHED:
                log.info("Message fetched:" + value);

                // New way of handeling relay messages
                String relay = getRelay(value);
                if(relay!=null){
                    completeRequest(relay, userContext, memberChat);
                }
                break;
        }
    }


    @Override
    public void init(UserContext userContext) {
        startConversation(userContext, "message.kontrakt.charity");
    }

    @Override
    public void init(UserContext userContext, String startMessage) {
        startConversation(userContext, startMessage);
    }
}
