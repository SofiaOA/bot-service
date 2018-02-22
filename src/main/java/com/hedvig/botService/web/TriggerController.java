package com.hedvig.botService.web;

import com.hedvig.botService.Profiles;
import com.hedvig.botService.enteties.DirectDebitMandateTrigger;
import com.hedvig.botService.session.triggerService.TriggerService;
import com.hedvig.botService.session.exceptions.UnathorizedException;
import com.hedvig.botService.session.triggerService.dto.CreateDirectDebitMandateDTO;
import com.hedvig.botService.web.dto.TriggerResponseDTO;
import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/hedvig/trigger")
public class TriggerController {

    private final Logger log = LoggerFactory.getLogger(TriggerController.class);

    private final TriggerService triggerService;
    private final Environment environment;
    private final URI errorPageUrl;

    public TriggerController(TriggerService triggerService, Environment environment, @Value("${hedvig.trigger.errorPageUrl:http://hedvig.com/error}") String errorPageUrl) {
        this.triggerService = triggerService;
        this.environment = environment;
        this.errorPageUrl = URI.create(errorPageUrl);
    }

    @PostMapping("{triggerId}")
    public ResponseEntity<TriggerResponseDTO> index(@RequestHeader("hedvig.token") String hid, @PathVariable UUID triggerId) {

        final String triggerUrl = triggerService.getTriggerUrl(triggerId, hid);
        if(triggerUrl == null) {
            return ResponseEntity.notFound().build();
        }

        TriggerResponseDTO response = new TriggerResponseDTO(triggerUrl + "&gui=native&color=%230F7A64&bordercolor=%230F7A64");
        return ResponseEntity.ok(response);
    }

    /**
     * Helper function that allows easy testing of the DirectDebitMandates during development.
     * @return
     */
    @PostMapping("_/createDDM")
    public ResponseEntity<String> index(@RequestHeader("hedvig.token") String hid, @RequestBody CreateDirectDebitMandateDTO requestData) {

        if(ArrayUtils.contains(environment.getActiveProfiles(), Profiles.PRODUCTION)) {
            return ResponseEntity.notFound().build();
        }


        final UUID triggerId = triggerService.createTrustlyDirectDebitMandate(
                requestData,
                hid
        );


        return ResponseEntity.ok("{\"id\":\"" + triggerId.toString() + "\"}");
    }

    @GetMapping("/notification")
    public ResponseEntity<?> getNotification(@RequestParam("triggerId") UUID triggerId, DirectDebitMandateTrigger.TriggerStatus status) {

        try {
            final String redirectionUrl = triggerService.clientNotificationReceived(triggerId, status);

            return ResponseEntity.status(304).location(URI.create(redirectionUrl)).build();//.location()
        }
        catch(Exception ex) {
            log.error("Exception caught in TriggerController.getNotificaiton, redirecting to " + errorPageUrl, ex);
        }

        return ResponseEntity.status(304).location(this.errorPageUrl).build();
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnathorizedException.class)
    public String handleException(UnathorizedException ex) {
        return ex.getMessage();
    }
}
