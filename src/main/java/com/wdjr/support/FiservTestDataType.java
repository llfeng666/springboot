package com.wdjr.support;

import java.beans.ConstructorProperties;

public enum FiservTestDataType {
     CHALLENG_WITHOUT_IFRAME("challengeWithoutIframe"),
     CHALLENGE_WITH_IFRAME("challengeWithIframe"),
     FRICTIONLESS_WITH_IFRAME ("frictionlessWithIframe"),
     FRICTIONLESS_WITHOUT_IFRAME("frictionlessWithoutIframe");

    private final String textValue;

    public String getTextValue() {
        return this.textValue;
    }

    @ConstructorProperties({"textValue"})
    private FiservTestDataType(final String textValue) {
        this.textValue = textValue;
    }

}
