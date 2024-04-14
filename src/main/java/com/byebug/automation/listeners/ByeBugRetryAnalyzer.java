package com.byebug.automation.listeners;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class ByeBugRetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    public static final int maxRetryCount = 0;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            return true;
        }
        return false;
    }

}
