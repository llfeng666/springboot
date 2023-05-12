package com.wdjr.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor(force = true)
public class CardInfo {
    private  @NonNull String cardNumber;
    private  String expirationMonth;
    private  String expirationYear;
    private  @NonNull String cvc;

}
