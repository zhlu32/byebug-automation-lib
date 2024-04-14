package com.byebug.automation.listeners;

import com.byebug.automation.api.ApiSingleton;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;

public class ByeBugTestListener extends TestListenerAdapter {

    @Override
    public void onTestSuccess(ITestResult tr) {
        super.onTestSuccess(tr);
    }

    @Override
    public void onTestFailure(ITestResult tr) {
        super.onTestFailure(tr);
        callBackListener(tr.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult tr) {
        super.onTestSkipped(tr);
    }

    private void callBackListener(Throwable throwable) {
        ByeBugCallbackListener listener = ApiSingleton.getInstance().getByeBugCallbackListener();
        if(listener != null) {
            listener.onException("", throwable != null ? throwable.toString() : "");
        }
    }

}
