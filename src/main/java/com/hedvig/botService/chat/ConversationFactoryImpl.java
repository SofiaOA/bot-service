package com.hedvig.botService.chat;

import com.hedvig.botService.enteties.SignupCodeRepository;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.triggerService.TriggerService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class ConversationFactoryImpl implements ConversationFactory {
    private final MemberService memberService;
    private final ProductPricingService productPricingService;
    private final TriggerService triggerService;
    private final SignupCodeRepository signupCodeRepository;
    private final ApplicationEventPublisher eventPublisher;


    private Integer queuePos;

    public ConversationFactoryImpl(MemberService memberService, ProductPricingService productPricingService, TriggerService triggerService, SignupCodeRepository signupCodeRepository, ApplicationEventPublisher eventPublisher, @Value("${hedvig.waitlist.length}") Integer queuePos) {
        this.memberService = memberService;
        this.productPricingService = productPricingService;
        this.triggerService = triggerService;

        this.signupCodeRepository = signupCodeRepository;
        this.eventPublisher = eventPublisher;
        this.queuePos = queuePos;
    }

    @Override
    public Conversation createConversation(Class conversationClass) {

        if(conversationClass.equals(CharityConversation.class)) {
            return new CharityConversation();
        }

        if(conversationClass.equals(ClaimsConversation.class)) {
            return new ClaimsConversation(eventPublisher);
        }

        if(conversationClass.equals(MainConversation.class)) {
            return new MainConversation(productPricingService, this, eventPublisher);
        }

        if(conversationClass.equals(OnboardingConversationDevi.class)) {
            final OnboardingConversationDevi onboardingConversationDevi = new OnboardingConversationDevi(memberService, productPricingService, signupCodeRepository, eventPublisher, this);
            onboardingConversationDevi.queuePos = queuePos;
            return onboardingConversationDevi;
        }

        if(conversationClass.equals(TrustlyConversation.class)) {
                return new TrustlyConversation(triggerService, this);
        }

        if(conversationClass.equals(UpdateInformationConversation.class)) {
            return new UpdateInformationConversation(memberService, productPricingService);
        }

        return null;
    }


}
