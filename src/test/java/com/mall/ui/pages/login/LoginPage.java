package com.mall.ui.pages.login;

import com.byebug.automation.ui.halo.HaloPage;
import com.byebug.automation.ui.halo.UIHalo;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

public class LoginPage extends HaloPage {

    @FindBy(name = "username")
    WebElement usernameInput;
    @FindBy(name = "password")
    WebElement passwordInput;

    @FindBy(xpath = "//button/span[normalize-space(text()) and normalize-space(.)='登录']")
    WebElement loginBtn;

    public void loginSuccess(String username, String password) {
        type(usernameInput, username);
        type(passwordInput, password);
        loginBtn.click();

        UIHalo.waitElementInvisible("//button/span[normalize-space(text()) and normalize-space(.)='登录']", 3);
    }

}
