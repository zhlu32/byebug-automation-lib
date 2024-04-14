package com.byebug.automation.properties;

import cn.hutool.core.util.StrUtil;
import com.byebug.automation.config.ByteBugConfig;
import org.testng.Assert;

import java.io.*;
import java.util.Properties;

public class ByeBugProperties {

    private static Properties prop;
    private static String projectRootPath = System.getProperty("user.dir");

    static {
        // 创建必要的目录
        String[] DEFAULT_MK_DIRS = new String[]{
                ByteBugConfig.PROP_DIR,
                ByteBugConfig.DOWNLOAD_DIR,
                ByteBugConfig.OUTPUT_DIR,
                ByteBugConfig.WEBDRIVER_CHROME_DIR
        };
        for(String s : DEFAULT_MK_DIRS) {
            File dir = new File(projectRootPath + s);
            if(!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    private static synchronized void initProp() {
        if(prop == null) {
            prop = new Properties();
            for(String s : ByteBugConfig.BIZ_PROP_FILES) {
                FileInputStream ip = null;
                String pFile = projectRootPath + ByteBugConfig.PROP_DIR + s;
                try {
                    ip = new FileInputStream(pFile);
                } catch (FileNotFoundException e) {
                    Assert.fail("---------Properties文件：" + pFile + " 不存在---------");
                }
                BufferedReader bf = new BufferedReader(new InputStreamReader(ip));
                try {
                    prop.load(bf);
                } catch (IOException e) {
                    Assert.fail("---------读取Properties文件：" + pFile + " 报错---------");
                }
            }
        }
    }

    public static String get(String key){
        return get(key, "");
    }

    public static String get(String key, String defaultValue) {
        if(StrUtil.isEmpty(key)) {
            return "";
        }

        initProp();
        return prop.getProperty(key, defaultValue);
    }

    public static void set(String key, String value) {
        initProp();
        prop.setProperty(key, value);
    }

}
