package com.byebug.automation.ui.halo;

import com.byebug.automation.ui.UISingleton;
import com.byebug.automation.utils.HaloUtil;
import org.apache.http.util.TextUtils;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;

import java.util.Set;

public abstract class HaloPage {

    public HaloPage() {
        PageFactory.initElements(UISingleton.getInstance().getWebDriver(), this);
    }

    public void isPageTitleValidate(String title) {
        if (title == null) {
            title = "";
        }
        String current = UISingleton.getInstance().getWebDriver().getTitle();
        if(!title.equals(current)) {
            Assert.fail("当前页面标题：" + current + "; 预期：" + title);
        }
    }

    public void isPageUrlEndWith(String url) {
        if (url == null) {
            url = "";
        }
        String currentUrl = UISingleton.getInstance().getWebDriver().getCurrentUrl();
        if (TextUtils.isEmpty(currentUrl) || !currentUrl.endsWith(url)) {
            Assert.fail("当前页面URL：" + currentUrl + "; 预期URL结尾是：" + url);
        }
    }

    public void isPageUrlContainWith(String url) {
        String currentURL = UISingleton.getInstance().getWebDriver().getCurrentUrl();
        if(TextUtils.isEmpty(currentURL) || !currentURL.contains(url)) {
            Assert.fail("当前页面URL：" + currentURL + "; 预期URL包含：" + url);
        }
    }


    public void freshCurrentUrl() {
        WebDriver dr = UISingleton.getInstance().getWebDriver();
        dr.navigate().refresh();
        HaloUtil.sleep(2);
        UIHalo.waitUntilNetworkFinish();
    }

    public void scrollPageByY(WebElement element, float y) {
        try {
            ((JavascriptExecutor) UISingleton.getInstance().getWebDriver()).executeScript("arguments[0].scrollTop = arguments[1];", element, y);
            Thread.sleep(1000);
        } catch (Exception e) {

        }
    }

    /***
     * 滚动页面底部到指定元素
     */
    public void scrollEleToVisible(WebElement ele) {
        WebDriver driver = UISingleton.getInstance().getWebDriver();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(false);", ele);
    }

    /**
     * 输入文本，输入前做了自动清除的操作
     */
    public void type(WebElement ele, String str) {
        clearTextByJS(ele);
        ele.clear();
        ele.sendKeys(str);
    }

    /***
     * 部分输入框使用selenium的clear或使用keys.delete、keys.back_space清理无效时，可以使用此方法，通过js清除文本
     * @param ele 页面元素对象
     */
    public void clearTextByJS(WebElement ele) {
        HaloUtil.sleep(1);
        WebDriver driver = UISingleton.getInstance().getWebDriver();
        ((JavascriptExecutor) driver).executeScript("arguments[0].value='';var evt = document.createEvent('Event');evt.initEvent('input', true, true);arguments[0].dispatchEvent(evt);", ele);
    }

    /***
     * 文本变化后需要触发前端的事件时用，比如穿梭界面的
     * @param ele 页面元素对象
     */
    public void clearTextByBackSpace(WebElement ele) {
        HaloRobot robot = new HaloRobot();
        HaloUtil.sleep(1);
        ele.click();
        HaloUtil.sleep(1);
        robot.press("control");
        robot.press("a");
        HaloUtil.sleep(1);
        robot.release("a");
        robot.release("control");
        ele.sendKeys(Keys.BACK_SPACE.toString());
        HaloUtil.sleep(2);
    }


    /***
     * 切换到新窗口
     */
    public void switchToOtherWindow(String currentWindow) {
        WebDriver dr = UISingleton.getInstance().getWebDriver();
        Set<String> handlers = dr.getWindowHandles();
        for (String handler : handlers) {
            if (!handler.equals(currentWindow)) {
                dr.switchTo().window(handler);
                HaloUtil.sleep(2);
                break;
            }
        }
    }

    /***
     * 切换到指定窗口
     */
    public void switchToWindow(String toWindow) {
        WebDriver dr = UISingleton.getInstance().getWebDriver();
        Set<String> handlers = dr.getWindowHandles();
        for (String handler : handlers) {
            if (handler.equals(toWindow)) {
                dr.switchTo().window(handler);
                HaloUtil.sleep(2);
                break;
            }
        }
    }

}
