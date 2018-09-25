package com.hedvig.botService.web;

import static net.logstash.logback.marker.Markers.append;

import net.logstash.logback.marker.LogstashMarker;
import org.jetbrains.annotations.NotNull;

public class ControllerUtils {
  @NotNull
  static LogstashMarker markDeprecated() {
    return append("deprecated", "true");
  }
}
