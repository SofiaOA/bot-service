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
    	sessionManager.updateInfo(hid, what);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("quoteAccepted")
    ResponseEntity<String> quoteAccepted(@RequestHeader(value="hedvig.token", required = false) String hid){
        return ResponseEntity.ok().build();
    }

}
