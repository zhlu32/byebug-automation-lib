package com.byebug.automation.ui.listener;

import com.byebug.automation.listeners.ByeBugCallbackListener;
import com.byebug.automation.ui.UISingleton;
import com.byebug.automation.ui.halo.UIHalo;
import com.byebug.automation.utils.ReportUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverListener;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.Reporter;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;

public class ByeBugWebDriverListener implements WebDriverListener {

    public void beforeTo(WebDriver.Navigation navigation, String url) {
        ReportUtil.log("Before navigating to: '" + url + "'");
    }

    public void afterTo(WebDriver.Navigation navigation, String url) {
        ReportUtil.log("Navigated to:'" + url + "'");
        UIHalo.injectStableXHRJS();
    }

    public void beforeClick(WebElement element) {
        ReportUtil.log("Trying to click on: " + element.toString());

        // 点击前，判断当前没有网络请求再发起点击；依赖于TestUtil.injectStableXHRJS方法
        UIHalo.waitUntilNetworkFinish();

        //点击前，确保元素在屏幕中间
        WebDriver driver = UISingleton.getInstance().getWebDriver();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({inline: 'center'});", element);

        // 点击前，确保element可点击
        WebDriverWait webDriverWait = new WebDriverWait(driver, Duration.ofSeconds(10));
        webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
    }

    public void afterClick(WebElement element) {
        ReportUtil.log("Clicked on: " + element.toString());
    }

    public void onError (Object target, Method method, Object[] args, InvocationTargetException e) {
        /** findElement抛出来的异常的特点：1. 多  2.可能预期是正确的结果，如预期某个element消失（属于误报）  所以就不再处理这个异常 **/
        if("findElement".equals(method.getName())) {
            return;
        }
        ReportUtil.log("Exception occured: " + e);

        // 截图并记录到ITestResult对象的属性中，用于测试报告的生成
        WebDriver driver = UISingleton.getInstance().getWebDriver();
        String screenShortFullPath = "";
        try {
            ITestResult tr = Reporter.getCurrentTestResult();
            screenShortFullPath = UIHalo.takeScreenshot(driver);
            String fileName = screenShortFullPath.substring(screenShortFullPath.lastIndexOf(File.separator) + 1);
            tr.setAttribute(ReportUtil.CASE_FAILED_SCREEN_SHORT_PATH, fileName);
        }catch(Exception ex){
            ReportUtil.log("UI异常后进行屏幕截图操作失败！！！" + ex);
        }

        // 回调异常
        ByeBugCallbackListener callbackListener = UISingleton.getInstance().getByeBugCallbackListener();
        if(callbackListener != null) {
            callbackListener.onException(e.toString(), screenShortFullPath);
        }

        // 刷新web
        driver.navigate().refresh();
    }


    public void beforeFindElement(WebDriver driver, By by) {
        ReportUtil.log("Trying to find Element By : " + by.toString());
    }

    public void afterFindElement(WebDriver driver, By by, WebElement result) {
        ReportUtil.log("Found Element By : " + by.toString());
    }

}