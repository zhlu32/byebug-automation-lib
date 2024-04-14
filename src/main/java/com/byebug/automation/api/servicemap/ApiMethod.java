package com.byebug.automation.api.servicemap;

import lombok.Getter;
import lombok.Setter;

public enum ApiMethod {

    POST("post"),
    POST_FORM("post_form"),

    PUT("put"),
    PUT_FORM("put_form"),

    GET("get"),
    DELETE("delete"),

    UPLOAD("upload"),
    DOWNLOAD("download"),
    ;

    ApiMethod(String method) {
        this.apiMethod = method;
    }

    @Getter @Setter
    private String apiMethod;

    public static boolean isNoRequestBodyApiMethod(ApiMethod apiMethod) {
        return apiMethod == ApiMethod.GET || apiMethod == ApiMethod.DELETE || apiMethod == ApiMethod.DOWNLOAD;
    }

    public static boolean isFormApiMethod(ApiMethod apiMethod) {
        return apiMethod == ApiMethod.POST_FORM || apiMethod == ApiMethod.PUT_FORM;
    }
}
