package com.wdjr.entity;

import java.io.IOException;
import java.util.Objects;
import javax.annotation.Nullable;

import com.github.GBSEcom.model.ACSResponse;
import com.github.GBSEcom.model.AuthenticationUpdateRequest;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.JsonAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(
        description = "Authentication update request specific to 3DSecure 2.x transactions."
)
public class Secure3DSAuthenticationUpdateRequest extends AuthenticationUpdateRequest {
    public static final String SERIALIZED_NAME_METHOD_NOTIFICATION_STATUS = "methodNotificationStatus";
    @SerializedName("methodNotificationStatus")
    private Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum
            methodNotificationStatus;
    public static final String SERIALIZED_NAME_ACS_RESPONSE = "acsResponse";
    @SerializedName("acsResponse")
    private ACSResponse acsResponse;

    @SerializedName("securityCode")
    private String securityCode;

    public Secure3DSAuthenticationUpdateRequest methodNotificationStatus(
            Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum methodNotificationStatus) {
        this.methodNotificationStatus = methodNotificationStatus;
        return this;
    }

    @Nullable
    @ApiModelProperty(
            example = "RECEIVED",
            value = "Indicates how the merchant received the 3DS method."
    )
    public Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum getMethodNotificationStatus() {
        return this.methodNotificationStatus;
    }

    public Secure3DSAuthenticationUpdateRequest securityCode(String securityCode) {
        this.securityCode = securityCode;
        return this;
    }

    public void setSecurityCode(String securityCode) {
        this.securityCode = securityCode;
    }

    public void setMethodNotificationStatus(
            Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum methodNotificationStatus) {
        this.methodNotificationStatus = methodNotificationStatus;
    }

    public Secure3DSAuthenticationUpdateRequest acsResponse(ACSResponse acsResponse) {
        this.acsResponse = acsResponse;
        return this;
    }

    @Nullable
    @ApiModelProperty("")
    public ACSResponse getAcsResponse() {
        return this.acsResponse;
    }

    public void setAcsResponse(ACSResponse acsResponse) {
        this.acsResponse = acsResponse;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            Secure3DSAuthenticationUpdateRequest
                    secure3DAuthenticationUpdateRequest = (Secure3DSAuthenticationUpdateRequest)o;
            return Objects.equals(this.methodNotificationStatus, secure3DAuthenticationUpdateRequest.methodNotificationStatus) && Objects.equals(this.acsResponse, secure3DAuthenticationUpdateRequest.acsResponse) && super.equals(o);
        } else {
            return false;
        }
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.methodNotificationStatus, this.acsResponse, super.hashCode()});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Secure3DAuthenticationUpdateRequest {\n");
        sb.append("    ").append(this.toIndentedString(super.toString())).append("\n");
        sb.append("    methodNotificationStatus: ").append(this.toIndentedString(this.methodNotificationStatus)).append("\n");
        sb.append("    acsResponse: ").append(this.toIndentedString(this.acsResponse)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        return o == null ? "null" : o.toString().replace("\n", "\n    ");
    }

    @JsonAdapter(MethodNotificationStatusEnum.Adapter.class)
    public static enum MethodNotificationStatusEnum {
        RECEIVED("RECEIVED"),
        EXPECTED_BUT_NOT_RECEIVED("EXPECTED_BUT_NOT_RECEIVED"),
        NOT_EXPECTED("NOT_EXPECTED");

        private String value;

        private MethodNotificationStatusEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

        public String toString() {
            return String.valueOf(this.value);
        }
        

        public static class Adapter extends
                TypeAdapter<Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum> {
            public Adapter() {
            }

            public void write(JsonWriter jsonWriter, Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum enumeration) throws
                    IOException {
                jsonWriter.value(enumeration.getValue());
            }

            public Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum read(
                    JsonReader jsonReader) throws IOException {
                String value = jsonReader.nextString();
                return Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum.fromValue(value);
            }
        }

        public static Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum fromValue(String value) {
            Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum[] var1 = values();
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
                Secure3DSAuthenticationUpdateRequest.MethodNotificationStatusEnum b = var1[var3];
                if (b.value.equals(value)) {
                    return b;
                }
            }

            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }
}
