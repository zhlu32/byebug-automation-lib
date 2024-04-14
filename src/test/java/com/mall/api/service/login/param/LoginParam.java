package com.mall.api.service.login.param;

import com.byebug.automation.api.annotation.InvalidValue;
import com.byebug.automation.api.param.BaseParam;
import lombok.Data;

@Data
public class LoginParam extends BaseParam {
    @InvalidValue({"null", ""})
    private String username;
    @InvalidValue({"null", ""})
    private String password;
}
