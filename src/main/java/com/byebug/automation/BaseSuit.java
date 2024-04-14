package com.byebug.automation;

import org.testng.annotations.*;

/**
 * 测试套件生命周期的回调函数
 */
public abstract class BaseSuit {

    @BeforeSuite
    public void beforeSuit() {
        beforeSuitCallback();
    }

    @BeforeTest
    public void beforeTest() {
        beforeTestCallback();
    }

    @BeforeClass
    public void beforeClass() {
        beforeClassCallback();
    }

    @BeforeMethod
    public void beforeMethod() {
        beforeMethodCallback();
    }

    @AfterMethod
    public void afterMethod() {
        afterMethodCallback();
    }

    @AfterClass
    public void afterClass() {
        afterClassCallback();
    }

    @AfterTest
    public void afterTest() {
        afterTestCallback();
    }

    @AfterSuite
    public void afterSuit() {
        afterSuitCallback();
    }


    public abstract void beforeSuitCallback();
    public abstract void beforeTestCallback();
    public abstract void beforeClassCallback();
    public abstract void beforeMethodCallback();

    public abstract void afterMethodCallback();
    public abstract void afterClassCallback();
    public abstract void afterTestCallback();
    public abstract void afterSuitCallback();
}