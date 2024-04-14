package com.mall.ui.impl;

import com.byebug.automation.api.ApiSingleton;
import com.byebug.automation.ui.UISingleton;
import com.byebug.automation.ui.UISuit;
import com.mall.api.impl.MallCallbackListener;
import com.mall.api.impl.MallProperties;
import org.openqa.selenium.WebDriver;

public class MallUISuit extends UISuit {
    @Override
    public void beforeSuitCallback() {
        ApiSingleton.getInstance().setByeBugCallbackListener(new MallCallbackListener());

        WebDriver driver = UISingleton.getInstance().getWebDriver();
        driver.get(MallProperties.get(MallProperties.g_mall_ui_host));
    }

    @Override
    public void beforeTestCallback() {

    }

    @Override
    public void beforeClassCallback() {

    }

    @Override
    public void beforeMethodCallback() {

    }

    @Override
    public void afterMethodCallback() {

    }

    @Override
    public void afterClassCallback() {

    }

    @Override
    public void afterTestCallback() {

    }

    @Override
    public void afterSuitCallback() {
        super.afterSuitCallback();
    }
}
