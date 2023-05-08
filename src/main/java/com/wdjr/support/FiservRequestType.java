package com.wdjr.support;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FiservRequestType {
    CARD_PAYMENT("PaymentCardSaleTransaction"),
    CARD_PRE_AUTH("PaymentCardPreAuthTransaction"),
    POST_AUTH("PostAuthTransaction"),
    VOID("VoidTransaction"),
    VOID_PRE_AUTH("VoidPreAuthTransactions"),
    RETURN("ReturnTransaction"),
    INQUIRY("Transaction Inquiry"),
    TOKEN_PAYMENT("PaymentTokenSaleTransaction"),
    TOKEN_REFUND("PaymentTokenCreditTransaction"),
    TOKEN_PRE_AUTH("PaymentTokenPreAuthTransaction"),
    SEPA_PAYMENT("SepaSaleTransaction"),
    WALLET_SALE("WalletSaleTransaction"),
    WALLET_PRE_SALE("PaymentTokenCreditTransaction"),
    CREATE_TOKEN("PaymentCardPaymentTokenizationRequest");

    private final String textValue;

    public String getTextValue() {
        return this.textValue;
    }

    @ConstructorProperties({"textValue"})
    private FiservRequestType(final String textValue) {
        this.textValue = textValue;
    }

}
