package com.wdjr.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FromData {
    private String last4;
    private String transactionId;
    private  String transactionStatus;
    private String errorMsg;
}
