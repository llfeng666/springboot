package com.wdjr.entity;

import com.wdjr.support.FiservTestDataType;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class CardInfo {
    private  @NonNull String cardNumber;
    private  String expirationMonth;
    private  String expirationYear;
    private  @NonNull String cvc;
    private String testType;
}
