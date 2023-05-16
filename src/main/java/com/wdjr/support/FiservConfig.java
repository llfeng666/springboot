package com.wdjr.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.wdjr.entity.CardInfo;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

@ConfigurationProperties(prefix = "fiserv")
@Configuration
@NonFinal
@Data
public class FiservConfig {
    @Value("${fiserv.baseUrl:#{null}}")
    private String baseUrl;
    @Value("${fiserv.apiKey:#{null}}")
    private String apiKey;
    @Value("${fiserv.apiSecret:#{null}}")
    private String apiSecret;
    @Value("${fiserv.storeId:#{null}}")
    private String storeId;

    @Value("${fiserv.termURL:#{null}}")
    private String termURL;

    @Value("${fiserv.methodNotifictionURL:#{null}}")
    private String methodNotifictionURL;

    private List<CardInfo> cardInfoList;


    public CardInfo getCardInfo(final String cardNo) {
        final Map<String, List<CardInfo>> cardMap =
                cardInfoList.stream().collect(Collectors.groupingBy(CardInfo::getCardNumber));
        return CollectionUtils.isEmpty(cardMap) ? null : cardMap.get(cardNo).get(0);
    }
}
