package com.hedvig.botService.web;

import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.UpdateTypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hedvig")
public class HedvigController {

	private final SessionManager sessionManager;
	
    @Autowired
    public HedvigController(SessionManager sessions)
	{
		this.sessionManager = sessions;
    }
    
    @PostMapping("initiateUpdate")
    ResponseEntity<String> initiateUpdate(@RequestParam UpdateTypes what, @RequestHeader(value="hedvig.token", required = false) String hid) {
    	switch(what){
    	case APARTMENT_INFORMATION: sessionManager.updateInfo(hid);
    		break;
    	case BANK_ACCOUNT: sessionManager.updateInfo(hid);
    		break;
    	case FAMILY_MEMBERS: sessionManager.updateInfo(hid);
    		break;
    	case PERSONAL_INFORMATOIN: sessionManager.updateInfo(hid);
    		break;
    	}
        return ResponseEntity.noContent().build();
    }

    @PostMapping("quoteAccepted")
    ResponseEntity<String> quoteAccepted(@RequestHeader(value="hedvig.token", required = false) String hid){
        return ResponseEntity.ok().build();
    }

}
