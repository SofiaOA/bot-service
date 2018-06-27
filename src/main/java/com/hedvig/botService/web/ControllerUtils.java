package com.hedvig.botService.web;

import net.logstash.logback.marker.LogstashMarker;
import org.jetbrains.annotations.NotNull;

import static net.logstash.logback.marker.Markers.append;

public class ControllerUtils {
    @NotNull
    static LogstashMarker markDeprecated() {
        return append("deprecated", "true");
    }
}
