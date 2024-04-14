package com.byebug.automation.listeners;

/**
 * 测试套件的回调
 */
public interface ByeBugCallbackListener {

    // 发生异常后的回调
    void onException(String filePath, String throwable);

    // 测试报告生产后的回调
    void afterTestReportGenerated(String filePath, int total, int passed, int failed, int skip);

}
