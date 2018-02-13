package com.hedvig.botService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
@EnableTransactionManagement
@EnableSwagger2
public class BotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotServiceApplication.class, args);
	}

    @Bean
    public RestTemplate createRestTemplate() {
	    return new RestTemplate();
    }

    /*
    @Primary
    @Bean
    public MemberService createMemberService() {
        return new MemberServiceFake();
   }*/

}
