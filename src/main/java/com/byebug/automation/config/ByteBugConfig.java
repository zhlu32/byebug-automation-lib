package com.byebug.automation.config;

import java.io.File;

/**
 * 自动化测试库，可以让BIZ自定义的配置
 */
public class ByteBugConfig {

    // Properties文件目录
    public static String PROP_DIR = File.separator + "properties" + File.separator;
    // 存储下载文件目录
    public static String DOWNLOAD_DIR = File.separator + "download" + File.separator;
    // 测试报告生成目录
    public static String OUTPUT_DIR = File.separator + "test-output" + File.separator;
    // webdriver-chrome驱动目录
    public static String WEBDRIVER_CHROME_DIR = File.separator + "drivers" + File.separator + "chrome" + File.separator;


    /**  业务自定义Properties文件，只能放置在@PROP_DIR目录下 **/
    public static String[] BIZ_PROP_FILES = new String[]{
            "default.properties"
    };

    /* =============测试报告相关===================  */
    /**
     * 自动化测试报告-名称
     */
    public static String REPORT_NAME = "自动化测试报告";
    /**
     * 自动化测试报告-仅记录错误case的日志
     */
    public static boolean REPORT_JUST_ERROR_CASE = true;
    /**
     * 自动化测试报告-是否打印请求Head
     */
    public static boolean REPORT_LOG_REQUEST_HEADERS = false;


    /* =============发送邮件===================  */
    // SMTP邮件服务器
    public static String EMAIL_HOST = "smtp.126.com";
    // SMTP邮件服务器默认端口
    public static String EMAIL_PORT = "465";
    // 发件人
    public static String EMAIL_FROM_USER = "midisec@126.com";
    public static String EMAIL_FROM_PWD = "MGZXQMYOMQYJNFWE";
    public static String EMAIL_FROM_NICKNAME = "自动化测试Job";


    /* =============接口自动化===================  */
    // http请求超时时间
    public static int HTTP_CLIENT_TIMEOUT = 300000;
    // http是否跳转，默认不跳转
    public static boolean HTTP_CLIENT_REDIRECT = false;
    // http请求，返回值中code字段的path路径
    public static String RESPONSE_CODE_JSON_PATH = "$.code";
    public static int RESPONSE_CODE_SUCCESS = 0;


    /* =============UI自动化===================  */
    // 无头模式 默认关闭
    public static boolean UI_HEADLESS_MODE = false;

    public static int PAGE_LOAD_TIMEOUT = 20;
    public static int IMPLICIT_WAIT = 3;

}