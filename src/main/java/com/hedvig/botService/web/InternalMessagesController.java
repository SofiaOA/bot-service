package com.hedvig.botService.web;

import com.hedvig.botService.chat.Conversation;
import com.hedvig.botService.enteties.message.Message;
import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.AvatarDTO;
import com.hedvig.botService.web.dto.BackOfficeMessageDTO;
import com.hedvig.botService.web.dto.EventDTO;
import com.hedvig.botService.web.dto.ExpoDeviceInfoDTO;
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

import static net.logstash.logback.argument.StructuredArguments.value;

@RestController
@RequestMapping("/_/messages")
public class InternalMessagesController {

	private static Logger log = LoggerFactory.getLogger(InternalMessagesController.class);
	private final SessionManager sessionManager;

    @Autowired
    public InternalMessagesController(SessionManager sessions)
	{
		this.sessionManager = sessions;
    }

    /*
     * This endpoint is used internally to send messages from back-office personnel to end users
     * */
    @RequestMapping(path = "/addmessage", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public ResponseEntity<?> addmessage(@RequestBody BackOfficeMessageDTO m) {

    	Message msg = m.msg;
    	String hid = m.userId;
    	
     	log.info("Message from Hedvig to hid:"+ hid +" with messageId: " + msg.globalId);

        msg.header.fromId = Conversation.HEDVIG_USER_ID; //new Long(hid);
        
        // Clear all key information to generate a new entry
        msg.globalId = null;
        msg.header.messageId = null;
        msg.body.id = null;

        sessionManager.addMessageFromHedvig(msg, hid);

    	return ResponseEntity.noContent().build();
    }
    
    @RequestMapping(path = "/init", method = RequestMethod.POST)
    public ResponseEntity<?> create(@RequestHeader(value="hedvig.token", required = false) String hid, @RequestBody(required = false) ExpoDeviceInfoDTO json) {

     	log.info("Init recieved from api-gateway: " + hid);

     	String linkUri = "hedvig://+";
		if(json != null && json.getDeviceInfo() != null) {
			log.info(json.toString());
			linkUri = json.getDeviceInfo().getLinkingUri();
		}
        sessionManager.init(hid, linkUri);

    	return ResponseEntity.noContent().build();
    }
    

}
