package com.hedvig.botService.chat;


import com.hedvig.botService.enteties.SignupCodeRepository;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.triggerService.TriggerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ConversationFactoryTest {

    private final Class conversationClass;

    @Mock
    private MemberService memberService;

    @Mock
    private ProductPricingService productPricingService;

    @Mock
    SignupCodeRepository signupCodeRepository;

    @Mock
    private TriggerService triggerService;

    @Mock
    ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    Environment springEnvironment;

    @Parameterized.Parameters
    public static Collection<Object> data() {
        return Arrays.asList(new Object[] {
                TrustlyConversation.class,
                ClaimsConversation.class,
                CharityConversation.class,
                MainConversation.class,
                OnboardingConversationDevi.class,
                UpdateInformationConversation.class});
    }

    public ConversationFactoryTest(Class conversationClass) {
        this.conversationClass = conversationClass;
    }

    /*@Test
    public void canResturnTrustlyConversation() {

        ConversationFactory factory = new ConversationFactory(memberService, productPricingService, triggerService);

        Conversation conversation = factory.createConversation(TrustlyConversation.class);

        assertThat(conversation).isNotNull();
    }

    @Test
    public void canResturnCharityConversation() {

        ConversationFactory factory = new ConversationFactory(memberService, productPricingService, triggerService);

        Conversation conversation = factory.createConversation(CharityConversation.class);

        assertThat(conversation).isNotNull();
        assertThat(conversation).isInstanceOf(CharityConversation.class);
    }*/

    @Test
    public void test(){
        ConversationFactory factory = new ConversationFactoryImpl(memberService, productPricingService, triggerService, signupCodeRepository, applicationEventPublisher, springEnvironment, 0);

        Conversation conversation = factory.createConversation(conversationClass);

        assertThat(conversation).isNotNull();
        assertThat(conversation).isInstanceOf(conversationClass);
    }




}