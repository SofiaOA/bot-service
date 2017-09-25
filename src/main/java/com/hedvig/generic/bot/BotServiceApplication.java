package com.hedvig.generic.bot;

import java.util.TreeMap;

import org.axonframework.config.EventHandlingConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.hedvig.generic.bot.externalEvents.KafkaProperties;
import com.hedvig.generic.bot.session.SessionManager;
import com.hedvig.generic.bot.session.UserContext;

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
    public TreeMap<String, UserContext> createSession(){
    	return new TreeMap<String, UserContext>();
    }
    
    @Bean
    public SessionManager createSessionManager(TreeMap<String, UserContext> tm){
    	return new SessionManager(tm);
    }
}
