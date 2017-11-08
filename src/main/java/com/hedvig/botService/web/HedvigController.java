package com.hedvig.botService.web;

import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdAuthResponse;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.UpdateTypes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/hedvig")
public class HedvigController {

	private final SessionManager sessionManager;
    private final ProductPricingService service;
    private final MemberService memberService;

    @Autowired
    public HedvigController(SessionManager sessions, ProductPricingService service, MemberService memberService)
	{
		this.sessionManager = sessions;
        this.service = service;
        this.memberService = memberService;
    }
    
    @PostMapping("initiateUpdate")
    ResponseEntity<String> initiateUpdate(@RequestParam UpdateTypes what, @RequestHeader(value="hedvig.token", required = false) String hid) {
    	sessionManager.updateInfo(hid, what);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("quoteAccepted")
    ResponseEntity<String> quoteAccepted(@RequestHeader(value="hedvig.token") String hid){

        service.quoteAccepted(hid);

        this.sessionManager.quoteAccepted(hid);

    	return ResponseEntity.noContent().build();
    }

    @PostMapping("collect")
    ResponseEntity<?> collect(@RequestParam  String referenceToken, @RequestHeader(value="hedvig.token", required = false) String hid) {

        Optional<BankIdAuthResponse> response = this.sessionManager.collect(hid, referenceToken);


        if(response.isPresent()) {
            return ResponseEntity.ok(response.get());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("");
    }

}
