package com.wdjr.support;

import java.beans.ConstructorProperties;

public enum FiservTestDataType {
     CHALLENG_WITHOUT_IFRAME("CHALLENG_WITHOUT_IFRAME"),
     CHALLENGE_WITH_IFRAME("CHALLENGE_WITH_IFRAME"),
     FRICTIONLESS_WITH_IFRAME ("FRICTIONLESS_WITH_IFRAME"),
     FRICTIONLESS_WITHOUT_IFRAME("FRICTIONLESS_WITHOUT_IFRAME");

    private final String textValue;

    public String getTextValue() {
        return this.textValue;
    }

    @ConstructorProperties({"textValue"})
    private FiservTestDataType(final String textValue) {
        this.textValue = textValue;
    }

}
