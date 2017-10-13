package com.hedvig.botService.web;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.hedvig.botService.enteties.Message;
import com.hedvig.botService.session.SessionManager;

@RestController
public class MessagesController {

	private static Logger log = LoggerFactory.getLogger(MessagesController.class);
	private final SessionManager sessionManager;

    @Autowired
    public MessagesController(SessionManager sessions)
	{
		this.sessionManager = sessions;
    }

    protected void initBinder(HttpServletRequest request, ServletRequestDataBinder binder)
		throws ServletException {
	
		// Convert multipart object to byte[]
		binder.registerCustomEditor(byte[].class, new ByteArrayMultipartFileEditor());
	
	}
    
    /*
     * TODO: Change hedvig.token from optional to required
     * */
    @RequestMapping(path="/messages/{messageCount}")
    public Map<Integer, Message> messages(@PathVariable int messageCount, @RequestHeader(value="hedvig.token", required = false) String hid) {
    	
    	log.info("Getting " + messageCount + " messages for user:" + hid);

    	try {
			return sessionManager.getMessages(messageCount, hid).stream().collect(Collectors.toMap( m -> m.getGlobalId(), Function.identity()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return null;
    }
    
    /*
     * TODO: Change hedvig.token from optional to required
     * */
    @RequestMapping(path="/messages", produces = "application/json; charset=utf-8")
    public Map<Integer, Message> allMessages(@RequestHeader(value="hedvig.token", required = false) String hid) {
    	
    	log.info("Getting all messages for user:" + hid);

    	try {
			return sessionManager.getAllMessages(hid).stream()
					.sorted((x,y)->x.getTimestamp().compareTo(y.getTimestamp()))
					.collect(Collectors.toMap(m -> m.getGlobalId(), Function.identity(),
							(x, y) -> y, LinkedHashMap::new)
					);
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
    public ResponseEntity<?> create(@RequestBody Message msg, @RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Message recieved from user:" + hid);

        msg.header.fromId = new Long(hid);
        
        // Clear all key information to generate a new entry
        msg.globalId = null;
        msg.header.messageId = null;
        msg.body.id = null;
        
        sessionManager.receiveMessage(msg, hid);

        log.info("Of type:" + msg.body.getClass());

    	return ResponseEntity.noContent().build();
    }

    @RequestMapping(path = "/initclaim", method = RequestMethod.POST)
    public ResponseEntity<?> initClaim(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Init claims for user:" + hid);
        sessionManager.initClaim(hid);
    	return ResponseEntity.noContent().build();
    }
    
    @RequestMapping(path = "/chat/reset", method = RequestMethod.POST)
    public ResponseEntity<?> resetChat(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Reset chat for user:" + hid);
        //sessionManager.receiveMessage(msg, hid);

    	return ResponseEntity.noContent().build();
    }
    
    @RequestMapping(path = "/chat/main", method = RequestMethod.POST)
    public ResponseEntity<?> mainMenue(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Putting main message in chat for user:" + hid);
        sessionManager.mainMenu(hid);

    	return ResponseEntity.noContent().build();
    }
    
    @RequestMapping(path = "/chat/edit", method = RequestMethod.POST)
    public ResponseEntity<?> editChat(@RequestBody Message msg, @RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Edit chat for user:" + hid);
        //sessionManager.receiveMessage(msg, hid);

    	return ResponseEntity.noContent().build();
    }
}
