package com.hedvig.botService.web;

import com.hedvig.botService.serviceIntegration.memberService.dto.BankIdCollectResponse;
import com.hedvig.botService.serviceIntegration.memberService.exceptions.BankIdError;
import com.hedvig.botService.services.OnboardingService;
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
    final private Logger log = LoggerFactory.getLogger(OnboardingController.class);

    @Autowired
    public OnboardingController(OnboardingService onboardingService) {
        this.onboardingService = onboardingService;
    }

    @PostMapping("offerClosed")
    public ResponseEntity<?> offerClosed(@RequestHeader("hedvig.token") String hid) {

        onboardingService.offerClosed(hid);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("sign")
    public ResponseEntity<?> sign(@RequestHeader("hedvig.token") String hid) {

        try {
            BankidStartResponse response = onboardingService.sign(hid);
            return ResponseEntity.ok(response);
        }
        catch(BankIdError e) {
            log.info("Got BankIdError: {}, {} " + e.getErrorType().name(), e.getMessage());
            String errorCode = "unknown";
            switch (e.getErrorType()) {
                case ALREADY_IN_PROGRESS:
                    errorCode = "alreadyInProgress";
                    break;
                case INVALID_PARAMETERS:
                    errorCode = "invalidParameters";
                    break;
                case INTERNAL_ERROR:
                    errorCode = "internalError";
                    break;
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BankIdCollectError(errorCode, e.getMessage()));
        }
        catch( FeignException ex) {
            log.error("Error starting bankidSign with member-service ", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BankIdCollectError("unkown", ex.getMessage()));
        }catch (Exception ex) {
            log.error("Got exception: ", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BankIdCollectError("unkown", ex.getMessage()));
        }
    }

    @PostMapping("collect")
    public ResponseEntity<?> collect(@RequestHeader("hedvig.token") String hid, @Valid @RequestBody BankIdCollectRequest body) {

        try {
            String orderRef = body.getOrderRef();
            BankIdCollectResponse collect = onboardingService.collect(hid, orderRef);
            log.info("{}", collect);

            String hint = "unknown";
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

            String hint = "unknown";
            boolean clientFailure = false;
            switch(e.getErrorType()) {
                case EXPIRED_TRANSACTION:
                    hint = "expiredTransaction";
                    clientFailure = true;
                    break;
                case CERTIFICATE_ERR:
                    hint = "certificateErr";
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
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BankIdCollectError(hint, e.getMessage()));
            }


        }catch( Exception ex) {
            log.error("Error collecting result from member-service ", ex);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BankIdCollectError("unknown", ex.getMessage()));
        }
    }
}
