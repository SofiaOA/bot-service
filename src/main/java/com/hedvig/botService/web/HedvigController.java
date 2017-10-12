package com.hedvig.botService.web;

import com.hedvig.botService.web.dto.UpdateTypes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hedvig")
public class HedvigController {

    @PostMapping("initiateUpdate")
    ResponseEntity<String> initiateUpdate(@RequestParam UpdateTypes what) {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("quoteAccepted")
    ResponseEntity<String> quoteAccepted(){
        return ResponseEntity.ok().build();
    }

}
