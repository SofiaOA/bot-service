package com.hedvig.botService.enteties;

import lombok.Value;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Value
public class OnBoardingContext implements Serializable {

    private boolean complete = false;
    private String name = "";

    public OnBoardingContext() {

    }

    public Boolean complete() {
        return complete;
    }

}
