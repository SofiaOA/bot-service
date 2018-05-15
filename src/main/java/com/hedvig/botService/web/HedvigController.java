package com.hedvig.botService.web;

import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.serviceIntegration.productPricing.ProductPricingService;
import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.AvatarDTO;
import com.hedvig.botService.web.dto.CollectResponse;
import com.hedvig.botService.web.dto.SignupStatus;
import com.hedvig.botService.web.dto.UpdateTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static net.logstash.logback.argument.StructuredArguments.value;

import java.util.List;


@RestController
@RequestMapping("/hedvig")
public class HedvigController {

    private final Logger log = LoggerFactory.getLogger(HedvigController.class);
	private final SessionManager sessionManager;
    private final ProductPricingService service;

    @Autowired
    public HedvigController(SessionManager sessions, ProductPricingService service)
	{
		this.sessionManager = sessions;
        this.service = service;
    }
    
    @PostMapping(path = "/waitlist")
    public ResponseEntity<SignupStatus> waitlistPosition(@RequestBody String externalToken) {
    	log.info("Fetching waitlist position for externalToken:" + externalToken);
    	
        SignupStatus ss = sessionManager.getSignupQueuePosition(externalToken);

        return new ResponseEntity<>(ss,HttpStatus.OK);
    }

    @PostMapping(path = "/deeplink")
    public ResponseEntity<Void> deeplink(@RequestBody String link, @RequestHeader(value="hedvig.token") String hid) {
    	log.info("Received deep link " + link + " for user " + hid);
    	sessionManager.saveDeepLink(hid, link);       
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/push-token")
    ResponseEntity<Void> pushToken(@RequestBody String tokenJson, @RequestHeader(value="hedvig.token") String hid) {
        log.info("Push token for memberId:{}, is: {}", value("memberId", ""), tokenJson);
        sessionManager.savePushToken(hid, tokenJson);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("initiateUpdate")
    ResponseEntity<String> initiateUpdate(@RequestParam UpdateTypes what, @RequestHeader(value="hedvig.token", required = false) String hid) {
    	log.info("Member initiated update: ", what.name());
        sessionManager.updateInfo(hid, what);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("quoteAccepted")
    ResponseEntity<String> quoteAccepted(@RequestHeader(value="hedvig.token") String hid){

        log.info("Quote accepted");
        service.quoteAccepted(hid);

        this.sessionManager.quoteAccepted(hid);

    	return ResponseEntity.noContent().build();
    }

    @PostMapping("trustlyClosed")
    ResponseEntity<String> trustlyClosed(@RequestHeader(value="hedvig.token") String hid) {
        log.info("GET trustlyClosed");

        this.sessionManager.trustlyClosed(hid);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("collect")
    ResponseEntity<?> collect(@RequestParam  String referenceToken,
                              @RequestHeader(value="hedvig.token") String hid) {

        log.info("Post collect with reference token: {}", value("referenceToken", referenceToken));
        try{
        BankIdCollectResponse response = this.sessionManager.collect(hid, referenceToken);

            ResponseEntity.BodyBuilder responseEntity = ResponseEntity.ok();

            String newMemberId = response.getNewMemberId();

            if(newMemberId != null && !newMemberId.equals(hid)) {
                 responseEntity = responseEntity.header("Hedvig.Id", newMemberId);
            }

            CollectResponse responseBody = new CollectResponse(response.getBankIdStatus().name());

            return responseEntity.body(responseBody);
        }catch (Exception e) {
            log.error("Error collecting: ", e);
            return ResponseEntity.ok(new CollectResponse("ERROR"));
        }
    }

}
