package com.hedvig.botService.web;

import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.MemberAuthedEvent;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventsController {

    private final SessionManager sessionManager;

    @Autowired
    public EventsController(SessionManager sessionManager) {

        this.sessionManager = sessionManager;
    }


    @PostMapping("/event/memberservice")
    public ResponseEntity<String> memberservice(@RequestBody MemberAuthedEvent event) {

        sessionManager.receiveEvent(event);

        return ResponseEntity.ok("");
    }

}
