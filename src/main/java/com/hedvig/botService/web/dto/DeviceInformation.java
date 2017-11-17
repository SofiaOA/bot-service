package com.hedvig.botService.web.dto;

import lombok.ToString;
import lombok.Value;

@Value
@ToString
public class DeviceInformation {
/*
    {"deviceInfo":
        {"appOwnership":"expo",
                "expoVersion":"2.1.0.1010818",
                "deviceId":"9957DEEA-2B34-4645-A79F-CE472469E21A",
                "deviceName":"Johans iPhone",
                "deviceYearClass":2016,
                "isDevice":true,
                "platform":{
                    "ios":{
                        "model":"iPhone 7",
                        "platform":"iPhone9,3",
                        "systemVersion":"11.0.3",
                        "userInterfaceIdiom":"handset"
                    }
                },
                "sessionId":"AAF80C0E-468E-4F6D-BF0E-64A6E51FE002",
                "statusBarHeight":20,
                "linkingUri":"exp://192.168.131.239:19000/+",
                "deviceHeight":667,
                "deviceWidth":375}}
*/

    private String appOwnership;
    private String expoVersion;
    private String deviceId;
    private String deviceName;
    private Integer deviceYearClass;
    private Boolean isDevice;

    //private String platform;

    private String sessionId;
    private Integer statusBarHeight;
    private String linkingUri;
    private Integer deviceHeight;
    private Integer deviceWidth;

}
