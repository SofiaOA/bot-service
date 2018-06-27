package com.hedvig.botService.chat;

import java.time.Clock;

public interface StatusBuilder {
    String getStatusMessage(Clock c);
}
