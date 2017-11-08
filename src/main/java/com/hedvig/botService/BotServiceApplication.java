package com.hedvig.botService;

import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.serviceIntegration.memberService.MemberServiceFake;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingClient;
import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.SessionManager;

import org.axonframework.config.EventHandlingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class BotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotServiceApplication.class, args);
	}

    @Autowired
    ProductPricingClient client;
    
    @Autowired
    public void configure(EventHandlingConfiguration config) {
        //config.usingTrackingProcessors();
    }

    @Bean
    public RestTemplate createRestTemplate() {
	    return new RestTemplate();
    }

//    @Primary
//    @Bean
//    public MemberService createMemberService() {
//        return new MemberServiceFake();
//    }

    @Bean
    public SessionManager createSessionManager(UserContextRepository userrepo, MemberService memberService, ProductPricingService ppservice){
    	return new SessionManager(userrepo, memberService, ppservice);
    }
}
