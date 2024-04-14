package com.mall.api.smoke.login;

import com.mall.api.impl.MallApiSuit;
import com.mall.api.impl.MallProperties;
import com.mall.api.service.login.LoginService;
import org.testng.annotations.Test;

@Test(groups = {"tag:登录模块", "author:奔跑的鸵鸟"})
public class LoginTest extends MallApiSuit {

    public void loginSuccessTest() {
        LoginService loginService = new LoginService();
        loginService.loginSuccess(MallProperties.get(MallProperties.g_mall_username), MallProperties.get(MallProperties.g_mall_user_pwd));
    }

}
