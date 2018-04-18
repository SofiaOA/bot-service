package com.hedvig.botService.serviceIntegration.claimsService;

import com.hedvig.botService.security.InternalJwtToken;
import com.hedvig.botService.serviceIntegration.claimsService.dto.StartClaimAudioDTO;
import feign.FeignException;
import lombok.val;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClaimsService {

    private final Logger log = LoggerFactory.getLogger(ClaimsService.class);
    private final ClaimsServiceClient claimsClient;
    private final InternalJwtToken jwtToken;

    @Autowired
    public ClaimsService(ClaimsServiceClient claimsClient, InternalJwtToken jwtToken) {
        this.claimsClient = claimsClient;
        this.jwtToken = jwtToken;
    }


    public void createClaimFromAudio(final String memberId, final String audioUrl) {
        val dto = new StartClaimAudioDTO(memberId, LocalDateTime.now(), audioUrl);

        try {
            claimsClient.createClaimFromAudio(dto, jwtToken.getInternalToken());
        }catch (FeignException ex) {
            log.error("Could not start claim at claim service", ex);
        }
    }
}