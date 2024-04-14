package com.byebug.automation.ui.halo;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.byebug.automation.config.ByteBugConfig;
import com.byebug.automation.ui.UISingleton;
import com.byebug.automation.utils.HaloUtil;
import com.byebug.automation.utils.ReportUtil;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class UIHalo {

    /**
     * 在window对象初始化后，立即调用
     * 插入XMLHttpRequest的脚本，用于判断当前是否有网络请求 window.bytebug_requesting
     */
    public static void injectStableXHRJS() {
        WebDriver driver = UISingleton.getInstance().getWebDriver();
        ((JavascriptExecutor)driver).executeScript("(function () {\n" +
                "    var OldXHR = window.XMLHttpRequest;\n" +
                "\n" +
                "    function newXHR() {\n" +
                "        var realXHR = new OldXHR();\n" +
                "\n" +
                "        realXHR.addEventListener('loadstart', function () {\n" +
                "            window.bytebug_requesting = true;\n" +
                "        }, false);\n" +
                "\n" +
                "        realXHR.addEventListener('loadend', function () {\n" +
                "            window.bytebug_requesting = false;\n" +
                "        }, false);\n" +
                "\n" +
                "        return realXHR;\n" +
                "    }\n" +
                "\n" +
                "    window.XMLHttpRequest = newXHR;\n" +
                "})();");
    }

    /**
     * 等待当前没有网络请求；依赖于TestUtil.injectStableXHRJS方法
     */
    public static void waitUntilNetworkFinish() {
        while (true) {
            WebDriver driver = UISingleton.getInstance().getWebDriver();
            Object isRequesting = ((JavascriptExecutor) driver).executeScript("return window.bytebug_requesting;");
            if(isRequesting != null && "true".equals(isRequesting.toString())) {
                HaloUtil.sleep(1);
                System.out.println("-----------There is a requesting.....  Wait One Second......-----------");
            }else{
                break;
            }
        }
    }

    public static void waitPageRenderFinshed(){
        WebDriver driver = UISingleton.getInstance().getWebDriver();
        while(true) {
            String renderingStatus = ((JavascriptExecutor) driver).executeScript("console.log(document.readyState);" +
                    "return document.readyState;").toString();
            if ("complete".equals(renderingStatus)) {
//                System.out.println("+++++ page render "+renderingStatus+" +++++");
                return;
            } else {
//                System.out.println("=====page render "+renderingStatus+",wait=====");
                HaloUtil.sleep(1);
            }
        }
    }


    /**
     * 关闭非主窗口的windows
     * @param driver
     */
    public static void closeWindowsExceptMain(WebDriver driver) {
        String currentWindow = driver.getWindowHandle();
        Set<String> windows = driver.getWindowHandles();
        for (String windowItem : windows) {
            if (!currentWindow.equals(windowItem)) {
                driver.switchTo().window(windowItem);
                driver.close();
            }
        }
        driver.switchTo().window(currentWindow);
    }

    /**
     * 屏幕截屏，保存图片
     * @param driver
     * @return
     */
    public static String takeScreenshot(WebDriver driver) {
        File scrFile = ((TakesScreenshot)driver).getScreenshotAs(OutputType.FILE);
        String screenShotPath = System.getProperty("user.dir") + ByteBugConfig.OUTPUT_DIR;
        File ssFile = new File(screenShotPath);
        if(!ssFile.exists()) {
            ssFile.mkdirs();
        }
        String fileFullPath = screenShotPath + File.separator + DateTime.now().toString().replaceAll(":", "-") + ".png";
        try {
            FileUtil.copy(scrFile, new File(fileFullPath), true);
        }catch (Exception e) {
            fileFullPath = "";
        }
        return fileFullPath;
    }

    public static void uploadFileByRobot(WebElement element, String relativePath) {
        // 把文件路径复制到剪贴板
        String fileAbsPath = System.getProperty("user.dir") + relativePath;
        ReportUtil.log("上传文件完整路径为：" + fileAbsPath);
        StringSelection sel = new StringSelection(fileAbsPath);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(sel, null);

        element.click();
        HaloUtil.sleep(3);

        // 新建一个Robot类的对象
        HaloRobot robot = null;
        try {
            robot = new HaloRobot();
        } catch (Exception e) {
            ReportUtil.log("uploadFileByRobot Excepiton: " + e.toString());
            return;
        }

        // 按下回车
        robot.press("enter");
        // 释放回车
        robot.release("enter");
        HaloUtil.sleep(1);
        // 按下 CTRL+V
        robot.press("control");
        robot.press("v");
        HaloUtil.sleep(1);
        // 释放 CTRL+V
        robot.release("control");
        robot.release("v");
        HaloUtil.sleep(2);
        // 点击回车 Enter
        robot.press("enter");
        robot.release("enter");

        HaloUtil.sleep(5);
    }


    /**
     * 元素是否显示
     * @param webDriver
     * @param webElement
     * @return
     */
    public static boolean isElementDisplayed(WebDriver webDriver, WebElement webElement) {
        if (webElement != null) {
            ((JavascriptExecutor) webDriver).executeScript("arguments[0].scrollIntoView(false);", webElement);
            return webElement.isDisplayed();
        }
        return false;
    }

    /**
     * 元素是否存在
     *
     * @param driver
     * @param parentElement
     * @param by
     * @param delay
     * @return
     */
    public static boolean isElementExist(WebDriver driver, WebElement parentElement, final By by, int delay) {
        try {
            driver.manage().timeouts().implicitlyWait(delay, TimeUnit.SECONDS);
            if (parentElement != null) {
                parentElement.findElement(by);
            } else {
                driver.findElement(by);
            }
            driver.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT, TimeUnit.SECONDS);

            return true;
        } catch (Exception e) {
            driver.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT, TimeUnit.SECONDS);
            return false;
        }
    }
    public static boolean isElementExist(WebDriver driver, final By by, int delay) {
        return isElementExist(driver, null, by, delay);
    }
    public static boolean isElementExist(WebDriver driver, final By by) {
        return isElementExist(driver, by, 6);
    }

    /***
     * 等待元素不可见
     * @param xpath
     * @param delay
     * @return
     */
    public static boolean waitElementInvisible(String xpath, int delay) {
        WebDriver wd = UISingleton.getInstance().getWebDriver();
        wd.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
        try{
            new WebDriverWait(wd, Duration.ofSeconds(delay)).until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(xpath)));
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return true;
        }
        catch (Exception e){
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return false;
        }
    }

    /**
     * 等待元素可见
     * @param xpath
     * @param delay
     */
    public static boolean waitElementVisible(String xpath, int delay) {
        WebDriver wd = UISingleton.getInstance().getWebDriver();
        wd.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
        try{
            new WebDriverWait(wd, Duration.ofSeconds(delay)).until(ExpectedConditions.visibilityOfElementLocated(By.xpath(xpath)));
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return true;
        }
        catch (Exception e){
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return false;
        }
    }

    public static boolean waitElementClickable(String xpath, int delay) {
        WebDriver wd = UISingleton.getInstance().getWebDriver();
        wd.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
        try{
            new WebDriverWait(wd, Duration.ofSeconds(delay)).until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return true;
        }
        catch (Exception e){
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return false;
        }
    }

    /**
     * 寻找元素的Text和指定的objName相等，且元素可见
     * @param xpath 父对象
     * @param objName
     * @param delay
     * @return
     */
    public static boolean isElementEqualTextVisible(String xpath, String objName, int delay) {
        WebDriver wd = UISingleton.getInstance().getWebDriver();
        wd.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
        try {
            String baseXpath = "//*[normalize-space(text()) ='" + objName + "']";
            if (StrUtil.isNotEmpty(xpath)) {
                baseXpath = xpath + baseXpath;
            }
            new WebDriverWait(wd, Duration.ofSeconds(delay)).until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath(baseXpath)
                    ));
            return true;
        } catch (Exception e) {
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return false;
        }
    }
    public static boolean isElementEqualTextVisible(String objName) {
        return isElementEqualTextVisible("", objName, ByteBugConfig.IMPLICIT_WAIT);
    }
    public static boolean isElementEqualTextVisible(String objName, int delay) {
        return isElementEqualTextVisible("", objName, delay);
    }
    public static boolean isElementEqualTextVisible(String xpath, String objName) {
        return isElementEqualTextVisible(xpath, objName, ByteBugConfig.IMPLICIT_WAIT);
    }

    /**
     * 寻找元素的Text和指定的objName相等，且元素不可见
     * @param xpath
     * @param objName
     * @param delay
     * @return
     */
    public static boolean isElementEqualTextInvisible(String xpath, String objName, int delay) {
        WebDriver wd = UISingleton.getInstance().getWebDriver();
        wd.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
        try {
            String baseXpath = "//*[normalize-space(text())='" + objName + "']";
            if (StrUtil.isNotEmpty(xpath)) {
                baseXpath = xpath + baseXpath;
            }
            new WebDriverWait(wd, Duration.ofSeconds(delay)).until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.xpath(baseXpath)
                    ));
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return false;
        }
    }
    public static boolean isElementEqualTextInvisible(String objName) {
        return isElementEqualTextInvisible("",objName,3);
    }
    public static boolean isElementEqualTextInvisible(String xpath, String objName) {
        return isElementEqualTextInvisible(xpath,objName,3);
    }
    public static boolean isElementEqualTextInvisible(String objName, int delay) {
        return isElementEqualTextInvisible("",objName,delay);
    }


    /**
     * 寻找元素的Text和指定的objName模糊匹配，且元素可见
     * @param xpath
     * @param objName
     * @param delay
     * @return
     */
    public static boolean isElementContainTextVisible(String xpath, String objName, int delay) {
        WebDriver wd = UISingleton.getInstance().getWebDriver();
        wd.manage().timeouts().implicitlyWait(0,TimeUnit.SECONDS);
        try {
            String baseXpath = "//*[contains(text(),'" + objName + "')]";
            if (StrUtil.isNotEmpty(xpath)) {
                baseXpath = xpath + baseXpath;
            }
            new WebDriverWait(wd, Duration.ofSeconds(delay)).until(
                    ExpectedConditions.visibilityOfElementLocated(
                            By.xpath(baseXpath)
                    ));
            return true;
        } catch (Exception e) {
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return false;
        }
    }
    public static boolean isElementContainTextVisible(String objName) {
        return isElementContainTextVisible("", objName, ByteBugConfig.IMPLICIT_WAIT);
    }
    public static boolean isElementContainTextVisible(String objName, int delay) {
        return isElementContainTextVisible("", objName, delay);
    }
    public static boolean isElementContainTextVisible(String xpath, String objName) {
        return isElementContainTextVisible(xpath, objName, ByteBugConfig.IMPLICIT_WAIT);
    }

    /**
     * 寻找元素的Text和指定的objName模糊匹配，且元素不可见
     * @param xpath
     * @param objName
     * @param delay
     * @return
     */
    public static boolean isElementContainTextInvisible(String xpath, String objName, int delay) {
        WebDriver wd = UISingleton.getInstance().getWebDriver();
        wd.manage().timeouts().implicitlyWait(0, TimeUnit.SECONDS);
        try {
            String baseXpath = "//*[contains(text(),'" + objName + "')]";
            if (StrUtil.isNotEmpty(xpath)) {
                baseXpath = xpath + baseXpath;
            }
            new WebDriverWait(wd, Duration.ofSeconds(delay)).until(
                    ExpectedConditions.invisibilityOfElementLocated(
                            By.xpath(baseXpath)
                    ));
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return true;
        } catch (Exception e) {
            wd.manage().timeouts().implicitlyWait(ByteBugConfig.IMPLICIT_WAIT,TimeUnit.SECONDS);
            return false;
        }
    }
    public static boolean isElementContainTextInvisible(String objName) {
        return isElementContainTextInvisible("",objName,3);
    }

    public static boolean isElementContainTextInvisible(String xpath, String objName) {
        return isElementContainTextInvisible(xpath,objName,3);
    }

    public static boolean isElementContainTextInvisible(String objName, int delay) {
        return isElementContainTextInvisible("",objName,delay);
    }


}