package com.hedvig.botService;

import java.util.TreeMap;

import com.hedvig.botService.enteties.MemberChatRepository;
import com.hedvig.botService.enteties.UserContextRepository;
import com.hedvig.botService.externalEvents.KafkaProperties;
import com.hedvig.botService.session.SessionManager;
import org.axonframework.config.EventHandlingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;

@SpringBootApplication
@EnableConfigurationProperties(KafkaProperties.class)
public class BotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotServiceApplication.class, args);
	}

    @Autowired
    public void configure(EventHandlingConfiguration config) {
        config.usingTrackingProcessors();
    }
    
    @Bean
    public SessionManager createSessionManager(MemberChatRepository repo, UserContextRepository userrepo){
    	return new SessionManager(repo, userrepo);
    }

    /*@Bean
    public TreeMap<String, UserContext> createSession(){
    	return new TreeMap<String, UserContext>();
    }*/
    
}
