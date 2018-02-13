package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.MemberChat;
import com.hedvig.botService.enteties.UserContext;
import com.hedvig.botService.enteties.message.*;
import com.hedvig.botService.enteties.userContextHelpers.UserData;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.triggerService.TriggerService;

import java.util.ArrayList;
import java.util.UUID;

public class TrustlyConversation extends Conversation {

    public static final String START = "trustly.start";
    private final TriggerService triggerService;

    public TrustlyConversation(MemberService memberService, ProductPricingService productPricingService, TriggerService triggerService) {
        super("conversation.trustly", memberService, productPricingService);
        this.triggerService = triggerService;

        createMessage(START,
                new MessageBodySingleSelect("Nu ska vi bara välja ett autogirokonto!",
                        new ArrayList<SelectItem>(){{
                            add(new SelectLink("Ja välj trustlykonto", "trustly.choose.account",  null, null,  "https://google.com", false));

                        }}
                ));
    }

    @Override
    public void recieveMessage(UserContext userContext, MemberChat memberChat, Message m) {

    }

    @Override
    public void init(UserContext userContext, MemberChat memberChat) {
        addToChat(getMessage("trustly.start"), userContext);
    }

    @Override
    public void init(UserContext userContext, MemberChat memberChat, String startMessage) {
        addToChat(getMessage(startMessage), userContext);
    }

    @Override
    void addToChat(Message m, UserContext userContext) {
        if(m.id.equals(START)) {
            final UserData userData = userContext.getOnBoardingData();
            UUID triggerUUID = triggerService.createDirectDebitMandate(
                    userData.getSSN(),
                    userData.getFirstName(),
                    userData.getFamilyName(),
                    userData.getEmail(),
                    userContext.getMemberId()
                    );

            userContext.putUserData("{TRUSTLY_TRIGGER_ID}", triggerUUID.toString());
        }

        super.addToChat(m, userContext);
    }
}
