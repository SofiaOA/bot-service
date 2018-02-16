package com.hedvig.botService.web;

import com.hedvig.botService.Profiles;
import com.hedvig.botService.session.triggerService.TriggerService;
import com.hedvig.botService.session.exceptions.UnathorizedException;
import com.hedvig.botService.session.triggerService.dto.CreateDirectDebitMandateDTO;
import com.hedvig.botService.web.dto.TriggerResponseDTO;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/hedvig/trigger")
public class TriggerController {

    TriggerService triggerService;
    private final Environment environment;

    public TriggerController(TriggerService triggerService, Environment environment) {
        this.triggerService = triggerService;
        this.environment = environment;
    }

    @PostMapping("{triggerId}")
    public ResponseEntity<TriggerResponseDTO> index(@RequestHeader("hedvig.token") String hid, @PathVariable UUID triggerId) {

        final String triggerUrl = triggerService.getTriggerUrl(triggerId, hid);
        if(triggerUrl == null) {
            return ResponseEntity.notFound().build();
        }

        TriggerResponseDTO response = new TriggerResponseDTO(triggerUrl);
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


        final UUID triggerId = triggerService.createDirectDebitMandate(
                requestData,
                hid
        );


        return ResponseEntity.ok("{\"id\":\"" + triggerId.toString() + "\"}");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(UnathorizedException.class)
    public String handleException(UnathorizedException ex) {
        return ex.getMessage();
    }
}
