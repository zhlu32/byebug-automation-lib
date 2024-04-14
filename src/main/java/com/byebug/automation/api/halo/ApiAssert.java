package com.byebug.automation.api.halo;

import cn.hutool.core.util.StrUtil;
import com.byebug.automation.config.ByteBugConfig;
import lombok.Data;
import org.testng.Assert;

@Data
public class ApiAssert {

    private int httpStatus;
    private int responseCode;

    // 断言字符串
    private String verify;

    // 是否需要断言
    private boolean needAssert = true;

    // 使用整形的最小值代表，期望接口返回的code是不成功的，如：$.code != 0
    private static int RESPONSE_CODE_NOT_SUCCESS = Integer.MIN_VALUE;

    public static ApiAssert createSkip(){
        ApiAssert apiAssert = new ApiAssert();
        apiAssert.needAssert = false;
        return apiAssert;
    }

    public static ApiAssert createHttpStatusNotSuccess(int httpStatus) {
        if(httpStatus == 200) {
            Assert.fail("200不是HttpStatus不成功的Code！！！");
        }
        return create(httpStatus, RESPONSE_CODE_NOT_SUCCESS, "");
    }

    public static ApiAssert createSuccess() {
        return createSuccess("");
    }

    public static ApiAssert createSuccess(String verify) {
        return create(200, ByteBugConfig.RESPONSE_CODE_SUCCESS, verify);
    }

    public static ApiAssert createNotSuccess() {
        return createNotSuccess("");
    }

    public static ApiAssert createNotSuccess(String verify) {
        return create(200, RESPONSE_CODE_NOT_SUCCESS, verify);
    }

    public static ApiAssert create(int status, int code, String verify) {
        ApiAssert apiAssert = new ApiAssert();
        apiAssert.httpStatus = status;
        apiAssert.responseCode = code;

        apiAssert.verify = code == RESPONSE_CODE_NOT_SUCCESS ?
                ByteBugConfig.RESPONSE_CODE_JSON_PATH + " != " + ByteBugConfig.RESPONSE_CODE_SUCCESS :
                ByteBugConfig.RESPONSE_CODE_JSON_PATH + " = " + code;

        if(StrUtil.isNotEmpty(verify)){
            apiAssert.verify = apiAssert.verify + ";" + verify;
        }

        return apiAssert;
    }

}