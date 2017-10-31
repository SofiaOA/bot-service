package com.hedvig.botService.web;

import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
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
    private final ProductPricingService service;

    @Autowired
    public HedvigController(SessionManager sessions, ProductPricingService service)
	{
		this.sessionManager = sessions;
        this.service = service;
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

    	return ResponseEntity.ok().build();
    }

}
