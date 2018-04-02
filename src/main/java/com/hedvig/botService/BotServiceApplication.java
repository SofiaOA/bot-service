package com.hedvig.botService;

import com.amazonaws.services.sns.AmazonSNS;
import com.hedvig.botService.security.JWTAuthFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.support.TaskUtils;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
@EnableTransactionManagement
public class BotServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BotServiceApplication.class, args);
	}

    @Bean
    public RestTemplate createRestTemplate() {
	    return new RestTemplate();
    }

    @Bean
	@Profile(Profiles.PRODUCTION)
	public NotificationMessagingTemplate notificationTemplate(AmazonSNS amazonSNS) {
		return new NotificationMessagingTemplate(amazonSNS);
	}

	@Bean
	public ApplicationEventMulticaster applicationEventMulticaster() {
		SimpleApplicationEventMulticaster eventMulticaster = new SimpleApplicationEventMulticaster();
		eventMulticaster.setErrorHandler(TaskUtils.LOG_AND_SUPPRESS_ERROR_HANDLER);
		return eventMulticaster;
	}

	@Bean
	public FilterRegistrationBean jwtAuthFilter(@Value("${oauth.cert:https://www.googleapis.com/oauth2/v2/certs}") String certUrl,
												@Value("${oauth.secret:}") String secret) {
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new JWTAuthFilter(certUrl, secret));
		filterRegistrationBean.addUrlPatterns("/_/*", "/i/*");
		filterRegistrationBean.setEnabled(Boolean.TRUE);
		filterRegistrationBean.setAsyncSupported(Boolean.TRUE);

		return filterRegistrationBean;
	}

}
