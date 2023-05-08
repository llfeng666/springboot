package com.wdjr.support;

import java.util.Map;

import lombok.Getter;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "fiserv")
@Configuration
@NonFinal
@Getter
public class FiservConfig {
    @Value("${fiserv.baseUrl:#{null}}")
    private String baseUrl;
    @Value("${fiserv.apiKey:#{null}}")
    private String apiKey;
    @Value("${fiserv.apiSecret:#{null}}")
    private String apiSecret;
    @Value("${fiserv.storeId:#{null}}")
    private String storeId;
    @Value("${fiserv.challengeWithoutIframe:#{null}}")
    private String challengeWithoutIframe;
    @Value("${fiserv.challengeWithIframe:#{null}}")
    private String challengeWithIframe;
    @Value("${fiserv.frictionlessWithIframe:#{null}}")
    private String frictionlessWithIframe;
    @Value("${fiserv.frictionlessWithoutIframe:#{null}}")
    private  String frictionlessWithoutIframe;
    @Value("${fiserv.termURL:#{null}}")
    private String termURL;

    public Map<String,FiservTestDataType> getFiservCardMap(){
        return Map.of( getChallengeWithoutIframe(),FiservTestDataType.CHALLENG_WITHOUT_IFRAME,
                getChallengeWithIframe(),FiservTestDataType.CHALLENGE_WITH_IFRAME,
                getFrictionlessWithIframe(),FiservTestDataType.FRICTIONLESS_WITH_IFRAME,
                getFrictionlessWithoutIframe(),FiservTestDataType.FRICTIONLESS_WITHOUT_IFRAME);
    }
}
