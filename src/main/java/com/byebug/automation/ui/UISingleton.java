package com.byebug.automation.ui;

import com.byebug.automation.config.ByteBugConfig;
import com.byebug.automation.listeners.ByeBugCallbackListener;
import com.byebug.automation.ui.listener.ByeBugWebDriverListener;
import lombok.Getter;
import lombok.Setter;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringDecorator;
import org.testng.Assert;

import java.io.File;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class UISingleton {

    private static UISingleton singleton;

    private WebDriver webDriver;

    private ByeBugCallbackListener byeBugCallbackListener;

    public static synchronized UISingleton getInstance() {
        if (singleton == null) {
            singleton = new UISingleton();
        }
        return singleton;
    }

    /**
     * 关闭web浏览器
     */
    public void quitWebDriver() {
        if (webDriver != null) {
            webDriver.close();
            webDriver.quit();
            webDriver = null;
        }
        singleton = null;
    }

    private UISingleton() {
        String projectRootPath = System.getProperty("user.dir");

        // 浏览器ui驱动程序设置-默认仅支持Chrome浏览器
        String osName = System.getProperty("os.name");
        String macDriver = projectRootPath + ByteBugConfig.WEBDRIVER_CHROME_DIR + "driver-mac";
        String winDriver = projectRootPath + ByteBugConfig.WEBDRIVER_CHROME_DIR + "driver-win.exe";
        String linuxDriver = projectRootPath + ByteBugConfig.WEBDRIVER_CHROME_DIR + "driver-linux";

        if (osName.toLowerCase().contains("mac")) {
            File file = new File(macDriver);
            if(!file.exists()) {
                Assert.fail("指定的默认路径下 " + macDriver + " 未找到ui驱动程序，您可以通过修改ByteBugConfig文件中的常量，自定义驱动放置位置！！！");
            }
            System.setProperty("webdriver.chrome.driver", macDriver);
        } else if (osName.toLowerCase().contains("window")) {
            File file = new File(winDriver);
            if(!file.exists()) {
                Assert.fail("指定的默认路径下 " + winDriver + " 未找到ui驱动程序，您可以通过修改ByteBugConfig文件中的常量，自定义驱动放置位置！！！");
            }
            System.setProperty("webdriver.chrome.driver", winDriver);
        } else {
            File file = new File(linuxDriver);
            if(!file.exists()) {
                Assert.fail("指定的默认路径下 " + linuxDriver + " 未找到ui驱动程序，您可以通过修改ByteBugConfig文件中的常量，自定义驱动放置位置！！！");
            }
            System.setProperty("webdriver.chrome.driver", linuxDriver);
        }

        // 设置
        ChromeOptions chromeOptions = setChromeOptions();
        webDriver = new ChromeDriver(chromeOptions);

        ByeBugWebDriverListener eventListener = new ByeBugWebDriverListener();
        webDriver = new EventFiringDecorator(eventListener).decorate(webDriver);

        webDriver.manage().window().maximize();
        webDriver.manage().deleteAllCookies();
        webDriver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ByteBugConfig.PAGE_LOAD_TIMEOUT));
        webDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(ByteBugConfig.IMPLICIT_WAIT));
    }

    private static ChromeOptions setChromeOptions() {
        Map<String, Object> prefs = new HashMap<String, Object>();
        // 禁用chrome的通知
        prefs.put("profile.default_content_setting_values.notifications", 2);
        prefs.put("profile.default_content_setting_values.clipboard", 2);
        // 设置文件下载后的存储路径
        prefs.put("download.default_directory", System.getProperty("user.dir") + ByteBugConfig.DOWNLOAD_DIR);
        ChromeOptions chromeOptions = new ChromeOptions();
        // 禁用https证书认证
        chromeOptions.addArguments("--ignore-certificate-errors");
        chromeOptions.addArguments("--disable-notifications");
        chromeOptions.setExperimentalOption("prefs", prefs);

        // 无头模式
        if (ByteBugConfig.UI_HEADLESS_MODE) {
            chromeOptions.addArguments("--headless");
        }
        return chromeOptions;
    }

}