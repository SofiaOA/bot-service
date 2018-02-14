package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;

import java.util.ArrayList;

public class CharityConversation extends Conversation {

    public CharityConversation() {
        super("charity");

        createChatMessage("message.kontrakt.charity",
                new MessageBodySingleSelect("En sista grej bara.. "
                        +"Som Hedvig-medlem får du välja en välgörenhetsorganisation att stödja om det blir pengar över när alla skador har betalats",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption("SOS Barnbyar", "charity.sosbarnbyar"));
                            add(new SelectOption("Cancerfonden", "charity.cancerfonden"));
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
                            add(new SelectLink("Börja utforska appen", "onboarding.done", "Dashboard", null, null,  false));
                        }}
                ));
    }

    @Override
    public void recieveMessage(UserContext userContext, MemberChat memberChat, Message m) {

        String nxtMsg = "message.kontrakt.charity";
        switch (m.id) {
            case "message.kontrakt.charity.tellmemore":
            case "message.kontrakt.charity":

                MessageBodySingleSelect mss = (MessageBodySingleSelect) m.body;
                final SelectItem selectedItem = mss.getSelectedItem();
                if(selectedItem.text.startsWith("charity")){
                    m.body.text = "Jag vill att mitt överskott ska gå till " + selectedItem.text;
                    addToChat(m, userContext);
                    userContext.putUserData("{CHARITY}", selectedItem.value);
                    nxtMsg = "message.kontrakt.charity.tack";
                    userContext.completeConversation(this.getClass().getName());
                }
                else{
                    nxtMsg = selectedItem.value;
                }
                break;
        }

        completeRequest(nxtMsg, userContext, memberChat);
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
