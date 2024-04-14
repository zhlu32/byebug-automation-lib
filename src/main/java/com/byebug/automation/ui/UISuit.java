package com.byebug.automation.ui;

import com.byebug.automation.BaseSuit;

public abstract class UISuit extends BaseSuit {

    @Override
    public void afterSuitCallback() {
        UISingleton.getInstance().quitWebDriver();
    }

}