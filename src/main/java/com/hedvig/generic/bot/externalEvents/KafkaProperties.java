package com.hedvig.generic.bot.externalEvents;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "hedvig.kafka")
public class KafkaProperties {

    @Getter
    @Setter
    public static class Bootstrap {
        public String servers = "";
    }

    public String acks;

    public Bootstrap bootstrap = new Bootstrap();

    public int retries;

    @NestedConfigurationProperty
    public Schema schema = new Schema();

    @Getter
    @Setter
    public static class Url {
        public String url = "";
    }

    @Getter
    @Setter
    public static class Schema {
        @NestedConfigurationProperty
        public Url registry = new Url();
    }
}
