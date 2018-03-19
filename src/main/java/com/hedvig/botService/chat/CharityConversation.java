package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.enteties.message.MessageBodySingleSelect;
import com.hedvig.botService.enteties.message.SelectItem;
import com.hedvig.botService.enteties.message.SelectOption;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CharityConversation extends Conversation {

    private static final String SOSBARNBYAR_VALUE = "charity.sosbarnbyar";
    private static final String SOS_BARNBYAR_NAME = "SOS Barnbyar";
    private static final UUID SOS_BARNBYAR_ID = UUID.fromString("97b2d1d8-af4a-11e7-9b2b-bbc138162bb2");

    private static final String BARNCANCERFONDEN_VALUE = "charity.barncancerfonden";
    private static final String BARNCANCERFONDEN_NAME = "Barncancerfonden";
    private static final UUID BARNCANCERFONDEN_ID = UUID.fromString("11143ee0-af4b-11e7-a359-4f8b8d55e69f");

    private static final String MESSAGE_CHARITY_UNKOWN_CHOICE = "message.charity.unkownchoice";
    private static final String MESSAGE_KONTRAKT_CHARITY = "message.kontrakt.charity";
    private static final String MESSAGE_KONTRAKT_CHARITY_TELLMEMORE = "message.kontrakt.charity.tellmemore";
    private static final String MESSAGE_KONTRAKT_CHARITY_TACK = "message.kontrakt.charity.tack";


    private final Logger log = LoggerFactory.getLogger(CharityConversation.class);
    private final ConversationFactory conversationFactory;
    private final MemberService memberService;




    CharityConversation(ConversationFactory factory, MemberService memberService) {
        super();
        this.conversationFactory = factory;
        this.memberService = memberService;

        createChatMessage(MESSAGE_KONTRAKT_CHARITY,
                new MessageBodySingleSelect("En grej till! \f"
                        +"Som Hedvig-medlem får du välja en välgörenhetsorganisation att stödja om det blir pengar över när alla skador har betalats",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption(SOS_BARNBYAR_NAME, SOSBARNBYAR_VALUE));
                            add(new SelectOption(BARNCANCERFONDEN_NAME, BARNCANCERFONDEN_VALUE));
                            add(new SelectOption("Berätta mer", MESSAGE_KONTRAKT_CHARITY_TELLMEMORE));
                        }}
                ));

        createChatMessage(MESSAGE_CHARITY_UNKOWN_CHOICE,
                new MessageBodySingleSelect("Jag känner inte igen det alternativ du valt, du kan välja en av dessa välgörenhetsorganisationer",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption(SOS_BARNBYAR_NAME, SOSBARNBYAR_VALUE));
                            add(new SelectOption(BARNCANCERFONDEN_NAME, BARNCANCERFONDEN_VALUE));
                            add(new SelectOption("Berätta mer", MESSAGE_KONTRAKT_CHARITY_TELLMEMORE));
                        }}
                ));

        createChatMessage(MESSAGE_KONTRAKT_CHARITY_TELLMEMORE,
                new MessageBodySingleSelect("Så här, jag fungerar inte som ett vanligt försäkringsbolag\f" +
                        "Jag tar ut en fast avgift för att kunna ge dig bra service\f" +
                        "Resten av det du betalar öronmärks för att ersätta skador\f" +
                        "När alla skador har betalats skänks överskottet till organisationer som gör världen bättre\f" +
                        "Du väljer själv vad ditt hjärta klappar för!",
                        new ArrayList<SelectItem>() {{
                            add(new SelectOption(SOS_BARNBYAR_NAME, SOSBARNBYAR_VALUE));
                            add(new SelectOption(BARNCANCERFONDEN_NAME, BARNCANCERFONDEN_VALUE));
                        }}
                ));

        createMessage(MESSAGE_KONTRAKT_CHARITY_TACK,
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

        String nxtMsg = MESSAGE_KONTRAKT_CHARITY;
        switch (getMessageId(m.id)) {
            case MESSAGE_KONTRAKT_CHARITY_TELLMEMORE:
            case MESSAGE_KONTRAKT_CHARITY:

                MessageBodySingleSelect mss = (MessageBodySingleSelect) m.body;
                final SelectItem selectedItem = mss.getSelectedItem();
                if(selectedItem.value.startsWith("charity")){
                    m.body.text = "Jag vill att mitt överskott ska gå till " + selectedItem.text;
                    addToChat(m, userContext);

                    userContext.putUserData("{CHARITY}", selectedItem.value);


                    val charityId = getCharityId(selectedItem.value);
                    if(charityId.isPresent()) {

                        memberService.selectCashback(userContext.getMemberId(), charityId.get());

                        nxtMsg = MESSAGE_KONTRAKT_CHARITY_TACK;
                        addToChat(nxtMsg, userContext);
                        userContext.completeConversation(this.getClass().getName());
                        userContext.startConversation(conversationFactory.createConversation(TrustlyConversation.class));
                        return;
                    }

                    nxtMsg = MESSAGE_CHARITY_UNKOWN_CHOICE;
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

    private Optional<UUID> getCharityId(final String charity) {
        switch (charity) {
            case BARNCANCERFONDEN_VALUE:
                return Optional.of(BARNCANCERFONDEN_ID);
            case SOSBARNBYAR_VALUE:
                return Optional.of(SOS_BARNBYAR_ID);
            default:
                return Optional.empty();
        }
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
        startConversation(userContext, MESSAGE_KONTRAKT_CHARITY);
    }

    @Override
    public void init(UserContext userContext, String startMessage) {
        startConversation(userContext, startMessage);
    }
}
