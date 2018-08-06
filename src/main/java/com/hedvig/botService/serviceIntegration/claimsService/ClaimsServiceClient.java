package com.hedvig.botService.serviceIntegration.claimsService;

import com.hedvig.botService.serviceIntegration.claimsService.dto.ActiveClaimsDTO;
import com.hedvig.botService.serviceIntegration.claimsService.dto.StartClaimAudioDTO;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@FeignClient(value = "claimsServiceClient", url = "${hedvig.claims-service.url:claims-service}")
public interface ClaimsServiceClient {

  @RequestMapping(value = "/_/claims/startClaimFromAudio", method = RequestMethod.POST)
  void createClaimFromAudio(@RequestBody StartClaimAudioDTO dto);

  @RequestMapping(value = "/_/claims/activeClaims/{userId}", method = RequestMethod.GET)
  ActiveClaimsDTO getActiveClaims(@PathVariable("userId") String memberId);
}
