package com.hedvig.botService.web;

import com.hedvig.botService.chat.OnboardingConversationDevi;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.AvatarDTO;
import com.hedvig.botService.web.dto.EventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.support.ByteArrayMultipartFileEditor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    	
    	log.info("Getting " + messageCount + " messages for member:" + hid);

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
    	
    	log.info("Getting all messages for member:" + hid);

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

     	log.info("Response recieved from messageId: " + msg.globalId);

        msg.header.fromId = new Long(hid);
        
        // Clear all key information to generate a new entry
        msg.globalId = null;
        msg.header.messageId = null;
        msg.body.id = null;
        if(msg.body.text != null) {
            msg.body.text = msg.body.text.trim();
        }
        
        sessionManager.receiveMessage(msg, hid);

    	return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/avatars")
    public ResponseEntity<List<AvatarDTO>> getAvatars(@RequestHeader(value="hedvig.token", required = false) String hid) {

    	// TODO: Implement 
     	log.info("Getting avatars member: " + hid);
        
     	ArrayList<AvatarDTO> avatars = new ArrayList<AvatarDTO>();
     	//AvatarDTO avatar1 = new AvatarDTO("loader", "https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/hedvig_typing_animation.json",78,52,1000);
     	AvatarDTO avatar1 = new AvatarDTO("loader", "https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/hedvig_typing_animation_2.json",78,52,100000);

     	AvatarDTO avatar2 = new AvatarDTO("h_symbol", "https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/hedvig_h_symbol.json",315,90,2000);
     	//AvatarDTO avatar2 = new AvatarDTO("h_symbol", "https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/h_symbol.json",315,90,2000);
     	AvatarDTO avatar3 = new AvatarDTO("family_to_h", "https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/family_to_h.json",280,180,2000);
     	AvatarDTO avatar4 = new AvatarDTO("h_to_family", "https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/h_to_family.json",280,180,2000);
     	AvatarDTO avatar5 = new AvatarDTO("h_to_house", "https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/h_to_house.json",315,90,2000);
     	AvatarDTO avatar6 = new AvatarDTO("house_to_h", "https://s3.eu-central-1.amazonaws.com/com-hedvig-web-content/house_to_h.json",315,90,2000);
     	
     	avatars.add(avatar1);
     	avatars.add(avatar2);
     	avatars.add(avatar3);
     	avatars.add(avatar4);
     	avatars.add(avatar5);
     	avatars.add(avatar6);
    	return new ResponseEntity<List<AvatarDTO>>(avatars,HttpStatus.OK);
    }
    
    @PostMapping(path = "/initclaim")
    public ResponseEntity<?> initClaim(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Init claim for member:" + hid);
        sessionManager.initClaim(hid);
    	return ResponseEntity.noContent().build();
    }
    
    @PostMapping(path = "/event")
    public ResponseEntity<?> eventRecieved(@RequestBody EventDTO e, @RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Event {} received from member:", e.type, hid);
        sessionManager.recieveEvent(e.type, e.value, hid);
    	return ResponseEntity.noContent().build();
    }
    
    /*
     * Already member start
     * */
    @RequestMapping(path = "/chat/login")
    public ResponseEntity<?> chatLogin(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Chat login event from user: " + hid);
     	sessionManager.startOnboardingConversation(hid, "message.start.login");
    	return ResponseEntity.noContent().build();
    }
    
    /*
     * Regular start
     * */
    @RequestMapping(path = "/chat/startweb")
    public ResponseEntity<?> chatStartWeb(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Chat start web event from user: " + hid);
        sessionManager.startOnboardingConversationWeb(hid, OnboardingConversationDevi.MESSAGE_WAITLIST_START);
    	return ResponseEntity.noContent().build();
    }
    
    /*
     * Regular start
     * */
    @RequestMapping(path = "/chat/start")
    public ResponseEntity<?> chatStart(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Chat start event from user: " + hid);
        sessionManager.startOnboardingConversation(hid, OnboardingConversationDevi.MESSAGE_WAITLIST_START);
    	return ResponseEntity.noContent().build();
    }
    
    @PostMapping(path = "/chat/reset")
    public ResponseEntity<?> resetChat(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Reset chat for member:" + hid);
        sessionManager.resetOnboardingChat(hid);

    	return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/chat/main")
    public ResponseEntity<?> mainMenue(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Putting main message in chat for member:" + hid);
        sessionManager.mainMenu(hid);

    	return ResponseEntity.noContent().build();
    }
    
    @PostMapping(path = "/chat/edit")
    public ResponseEntity<?> editChat(@RequestHeader(value="hedvig.token", required = false) String hid) {

     	log.info("Edit chat for member:" + hid);
        sessionManager.editHistory(hid);

    	return ResponseEntity.noContent().build();
    }

    @PostMapping(path = "/claim/asset/{assetId}")
	public ResponseEntity<?> startClaim(@RequestHeader(value="hedvig.token", required = true) String hid, @PathVariable String assetId) {

    	log.info("Post assetId: {}", assetId);
    	sessionManager.initClaim(hid, assetId);

    	return ResponseEntity.noContent().build();
	}
}
