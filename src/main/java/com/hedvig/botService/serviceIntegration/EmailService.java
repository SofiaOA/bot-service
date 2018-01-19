package com.hedvig.botService.serviceIntegration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailService {
  
    @Autowired
    public JavaMailSender emailSender;

    public void send() {
    	SimpleMailMessage msg = new SimpleMailMessage ();
        try {
        	msg.setTo("john@hedvig.com");
            //helper.setReplyTo("someone@localhost");
        	msg.setFrom("hedvig@hedvig.com");
        	msg.setSubject("Lorem ipsum");
        	msg.setText("Lorem ipsum dolor sit amet [...]");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {}
        emailSender.send(msg);
        //return helper;
    }
}