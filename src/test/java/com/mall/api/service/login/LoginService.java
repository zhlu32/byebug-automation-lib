package com.mall.api.service.login;

import com.byebug.automation.api.halo.ApiRequestData;
import com.mall.api.impl.MallApiSuit;
import com.mall.api.service.login.param.LoginParam;
import com.mall.api.service.login.servicemap.LoginServiceMap;

public class LoginService extends MallApiSuit {

    public void loginSuccess(String username, String pwd) {
        LoginParam loginParam = new LoginParam();
        loginParam.setUsername(username);
        loginParam.setPassword(pwd);

        ApiRequestData ard = new ApiRequestData(LoginServiceMap.LOGIN, loginParam);
        apiTest(ard);
    }

}
