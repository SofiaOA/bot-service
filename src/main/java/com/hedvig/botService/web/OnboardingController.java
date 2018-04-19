package com.hedvig.botService.web;

import com.hedvig.botService.serviceIntegration.memberService.MemberService;
import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.serviceIntegration.memberService.exceptions.BankIdError;
import com.hedvig.botService.session.OnboardingService;
import com.hedvig.botService.session.SessionManager;
import com.hedvig.botService.web.dto.BankIdCollectError;
import com.hedvig.botService.web.dto.BankIdCollectRequest;
import com.hedvig.botService.web.dto.BankidCollectResponse;
import com.hedvig.botService.web.dto.BankidStartResponse;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static net.logstash.logback.argument.StructuredArguments.value;

@RestController
@RequestMapping("/hedvig/onboarding")
public class OnboardingController {

    final private OnboardingService onboardingService;
    final private MemberService memberService;
    final private Logger log = LoggerFactory.getLogger(OnboardingController.class);

    @Autowired
    public OnboardingController(SessionManager sessionManager, OnboardingService onboardingService, MemberService memberService) {

        this.onboardingService = onboardingService;
        this.memberService = memberService;
    }

    @PostMapping("sign")
    public ResponseEntity<?> sign(@RequestHeader("hedvig.token") String hid) {

        BankidStartResponse response = onboardingService.sign(hid);

        return ResponseEntity.ok(response);
    }

    @PostMapping("collect")
    public ResponseEntity<?> collect(@RequestHeader("hedvig.token") String hid, @Valid @RequestBody BankIdCollectRequest body) {

        try {
            BankIdCollectResponse collect = memberService.collect(body.getOrderRef(), hid);
            log.info("{}", collect);

            String hint = "unkown";
            String status = "pending";
            switch (collect.getBankIdStatus()) {

                case OUTSTANDING_TRANSACTION:
                    hint = "outstandingTransaction";
                    break;
                case NO_CLIENT:
                    hint = "noClient";
                    break;
                case STARTED:
                    hint = "started";
                    break;
                case USER_SIGN:
                    hint = "userSign";
                    break;
                case COMPLETE:
                    hint = "";
                    status = "complete";
                    break;
            }

            return ResponseEntity.ok(new BankidCollectResponse(body.getOrderRef(), status, hint));
        }
        catch(BankIdError e) {//Handle error
            log.error("Got bankIderror {} response from member service with reference token: {}",
                    value("referenceToken", body.getOrderRef()), e.getErrorType());

            String hint = "unkown";
            boolean clientFailure = false;
            switch(e.getErrorType()) {
                case EXPIRED_TRANSACTION:
                    hint = "expiredTransaction";
                    clientFailure = true;
                    break;
                case CERTIFICATE_ERR:
                    hint = "certificate_err";
                    clientFailure = true;
                    break;
                case USER_CANCEL:
                    hint = "userCancel";
                    clientFailure = true;
                    break;
                case CANCELLED:
                    hint = "cancelled";
                    clientFailure = true;
                    break;
                case START_FAILED:
                    hint = "startFailed";
                    clientFailure = true;
                    break;
                case INVALID_PARAMETERS:
                    hint = "invalidParameters";
                    return ResponseEntity.badRequest().body(new BankIdCollectError(hint, e.getMessage()));
            }
            if(clientFailure) {
                return ResponseEntity.ok(new BankidCollectResponse(body.getOrderRef(), "failed", hint));
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BankIdCollectError(hint, e.getMessage()));
            }


        }catch( FeignException ex) {
            log.error("Error collecting result from member-service ", ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BankIdCollectError("unkown", ex.getMessage()));


        }finally {

        }
    }
}