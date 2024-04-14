package com.mall.ui.smoke.login;

import com.mall.api.impl.MallProperties;
import com.mall.ui.impl.MallUISuit;
import com.mall.ui.pages.login.LoginPage;
import org.testng.annotations.Test;

@Test(groups = {"tag:登录模块", "author:奔跑的鸵鸟"})
public class LoginTest extends MallUISuit {

    public void loginTest(){
        LoginPage loginPage = new LoginPage();
        loginPage.loginSuccess(MallProperties.get(MallProperties.g_mall_username), MallProperties.get(MallProperties.g_mall_user_pwd));
    }

}
