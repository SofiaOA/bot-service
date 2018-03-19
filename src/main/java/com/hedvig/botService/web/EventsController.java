package com.hedvig.botService.web;

import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.MemberAuthedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventsController {

    private final SessionManager sessionManager;
    private final Logger log = LoggerFactory.getLogger(EventsController.class);

    @Autowired
    public EventsController(SessionManager sessionManager) {

        this.sessionManager = sessionManager;
    }


    @PostMapping("/event/memberservice")
    public ResponseEntity<String> memberservice(@RequestBody MemberAuthedEvent event) {
        MDC.put("memberId", event.getMemberId().toString());

        log.info("Received MemberAuthedEvent");
        sessionManager.receiveEvent(event);

        return ResponseEntity.ok("");
    }

}
