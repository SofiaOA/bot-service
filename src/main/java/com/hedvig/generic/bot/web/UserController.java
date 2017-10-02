package com.hedvig.generic.bot.web;

import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.hedvig.generic.bot.chat.Message;
import com.hedvig.generic.bot.query.UserRepository;
import com.hedvig.generic.bot.session.SessionManager;

@RestController
public class UserController {

	private static Logger log = LoggerFactory.getLogger(UserController.class);
	private final SessionManager sessionManager;
    private final UserRepository userRepository;
    private final CommandGateway commandBus;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public UserController(CommandBus commandBus, UserRepository repository, SessionManager sessions) {
        this.commandBus = new DefaultCommandGateway(commandBus);
        this.userRepository = repository;
        this.sessionManager = sessions;
    }

    /*
     * TODO: Change hedvig.token from optional to required
     * */
    @RequestMapping(path="/messages/{messageCount}")
    public String messages(@PathVariable int messageCount, @RequestHeader(value="hedvig.token", required = false) String hid) {
    	
    	log.info("Getting " + messageCount + " messages for user:" + hid);

    	try {
			return mapper.writeValueAsString(sessionManager.getMessages(messageCount, hid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    /*
     * TODO: Change hedvig.token from optional to required
     * */
    @RequestMapping(path="/messages")
    public String allMessages(@RequestHeader(value="hedvig.token", required = false) String hid) {
    	
    	log.info("Getting all messages for user:" + hid);

    	try {
			return mapper.writeValueAsString(sessionManager.getAllMessages(hid));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    /*
     * TODO: Change hedvig.token from optional to required
     * */
    @RequestMapping(path = "/response", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ResponseEntity<?> create(@RequestBody Object message, @RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Message recieved from user:" + hid);
    	
    	String result;
		try {
			result = mapper.writeValueAsString(message);
			Message msg = mapper.readValue(result, Message.class);
			sessionManager.recieveMessage(msg, hid);
			log.info("Of type:" + msg.body.getClass());
			log.info(result);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

    	return ResponseEntity.noContent().build();
    }

}
