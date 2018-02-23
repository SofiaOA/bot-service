package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.DirectDebitMandateTrigger;
import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.paymentService.PaymentService;
import com.hedvig.botService.serviceIntegration.paymentService.dto.OrderState;
import com.hedvig.botService.session.triggerService.TriggerService;

import java.util.ArrayList;
import java.util.UUID;

public class TrustlyConversation extends Conversation {

    public static final String START = "trustly.start";
    public static final String TRUSTLY_POLL = "trustly.poll";
    private static final String CANCEL = "trustly.cancel";
    private static final String COMPLETE = "trustly.complete";
    private final TriggerService triggerService;
    private final ConversationFactory factory;

    public TrustlyConversation(TriggerService triggerService,
                               ConversationFactory factory) {
        super("conversation.trustly");
        this.triggerService = triggerService;
        this.factory = factory;


        createChatMessage(START,
                new MessageBodySingleSelect("Fantastiskt! Nu är allt klart, vi ska bara sätta upp betalning \fDet ska såklart vara lika smidigt som allt annat, så vi använder digitalt autogiro genom Trustly",
                        new ArrayList<SelectItem>(){{
                            add(new SelectItemTrustly("Välj bankkonto", "trustly.poll"));
                        }}
                ));

        createMessage(TRUSTLY_POLL,
                new MessageBodySingleSelect("Hej igen {NAME}! För att din försäkring ska gälla behöver vi koppla din månadsbetalning :) ",
                        new ArrayList<SelectItem>(){{
                            add(new SelectItemTrustly("Välj bankkonto", "trustly.poll"));
                        }}));

        createMessage(CANCEL,
                new MessageBodySingleSelect("Oj, nu verkar det som att något gick lite fel med betalningsregistreringen. Vi testar igen!",
                        new ArrayList<SelectItem>(){{
                            add(new SelectItemTrustly("Välj bankkonto", "trustly.poll"));
                        }}));

        createMessage(COMPLETE,
                new MessageBodySingleSelect("Tack! Ingen är gladare än jag att ha dig här :angel:",
                        new ArrayList<SelectItem>(){{
                            add(new SelectLink("Nu kan du börja utforska appen", "end", "Dashboard", null, null, false));
                        }}));
    }

    @Override
    public void recieveMessage(final UserContext userContext, final MemberChat memberChat, final Message m) {

        String nxtMsg = "";
              /*
	  * In a Single select, there is only one trigger event. Set default here to be a link to a new message
	  */
        if (m.body.getClass().equals(MessageBodySingleSelect.class)) {

            MessageBodySingleSelect body1 = (MessageBodySingleSelect) m.body;
            for (SelectItem o : body1.choices) {
                if(o.selected) {
                    m.body.text = o.text;
                    addToChat(m, userContext);
                    nxtMsg = o.value;
                }
            }
        }

        switch (m.id) {
            case START:
                //endConversation(userContext);
                return;
            case TRUSTLY_POLL:
                return;
            case CANCEL:
                return;
            case COMPLETE:
                endConversation(userContext);
                return;
        }


        completeRequest(nxtMsg, userContext, memberChat);
    }

    private void endConversation(UserContext userContext) {
        userContext.completeConversation(this.getClass().toString());
    }

    @Override
    public void recieveEvent(EventTypes e, String value, UserContext userContext, MemberChat memberChat){

        switch(e){
            // This is used to let Hedvig say multiple message after another
            case MESSAGE_FETCHED:
                //log.info("Message fetched:" + value);

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
        addToChat(START, userContext);
    }

    @Override
    public void init(UserContext userContext, String startMessage) {
        addToChat(startMessage, userContext);
    }

    @Override
    void addToChat(Message m, UserContext userContext) {
        if((m.id.equals(START) || m.id.equals(CANCEL)) &&
                m.header.fromId == HEDVIG_USER_ID) {
            final UserData userData = userContext.getOnBoardingData();
            UUID triggerUUID = triggerService.createTrustlyDirectDebitMandate(
                    userData.getSSN(),
                    userData.getFirstName(),
                    userData.getFamilyName(),
                    userData.getEmail(),
                    userContext.getMemberId()
                    );

            userContext.putUserData(UserContext.TRUSTLY_TRIGGER_ID, triggerUUID.toString());
        }

        super.addToChat(m, userContext);
    }

    public void windowClosed(UserContext uc) {
        String nxtMsg;

        final DirectDebitMandateTrigger.TriggerStatus orderState = triggerService.getTrustlyOrderInformation(uc.getDataEntry(UserContext.TRUSTLY_TRIGGER_ID));
        if(orderState.equals(DirectDebitMandateTrigger.TriggerStatus.FAILED)) {
            nxtMsg = CANCEL;
        }else if(orderState.equals(DirectDebitMandateTrigger.TriggerStatus.SUCCESS)) {
            nxtMsg = COMPLETE;
            addToChat(getMessage(nxtMsg), uc);
            endConversation(uc);
            return;
        }else {
            nxtMsg = TRUSTLY_POLL;
        }

        addToChat(getMessage(nxtMsg), uc);
    }
}
