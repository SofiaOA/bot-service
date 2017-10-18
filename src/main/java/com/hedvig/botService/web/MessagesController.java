package com.hedvig.botService.web;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import com.hedvig.botService.enteties.Message;
import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.AvatarDTO;
import com.hedvig.botService.web.dto.EventDTO;

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

    @GetMapping(path = "/avatars")
    public ResponseEntity<List<AvatarDTO>> getAvatars(@RequestHeader(value="hedvig.token", required = false) String hid) {

    	// TODO: Implement 
     	log.info("Getting avatars user:" + hid);
        
     	ArrayList<AvatarDTO> avatars = new ArrayList<AvatarDTO>();
     	AvatarDTO avatar1 = new AvatarDTO("loader", "https://www.lottiefiles.com/storage/datafiles/qm9uaAEoe13l3eQ/data.json",500,500,1000);
     	AvatarDTO avatar2 = new AvatarDTO("bike", "https://www.lottiefiles.com/storage/datafiles/dlzGwlfS0fkCJcq/data.json",500,500,2000);
     	avatars.add(avatar1);
     	avatars.add(avatar2);
    	return new ResponseEntity<List<AvatarDTO>>(avatars,HttpStatus.OK);
    }
    
    @PostMapping(path = "/initclaim")
    public ResponseEntity<?> initClaim(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Init claims for user:" + hid);
        sessionManager.initClaim(hid);
    	return ResponseEntity.noContent().build();
    }
    
    @PostMapping(path = "/event")
    public ResponseEntity<?> eventRecieved(@RequestBody EventDTO e, @RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Event recieved from user:" + hid);
        sessionManager.recieveEvent(e.type, e.value, hid);
    	return ResponseEntity.noContent().build();
    }
    
    @PostMapping(path = "/chat/reset")
    public ResponseEntity<?> resetChat(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Reset chat for user:" + hid);
        //sessionManager.receiveMessage(msg, hid);

    	return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/chat/main")
    public ResponseEntity<?> mainMenue(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Putting main message in chat for user:" + hid);
        sessionManager.mainMenu(hid);

    	return ResponseEntity.noContent().build();
    }
    
    @PostMapping(path = "/chat/edit")
    public ResponseEntity<?> editChat(@RequestBody Message msg, @RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Edit chat for user:" + hid);
        //sessionManager.receiveMessage(msg, hid);

    	return ResponseEntity.noContent().build();
    }
}
