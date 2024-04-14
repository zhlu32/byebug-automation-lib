package com.byebug.automation.utils;

import cn.hutool.core.util.StrUtil;
import com.byebug.automation.config.ByteBugConfig;
import org.testng.Reporter;

import java.util.Calendar;

public class ReportUtil {
	public static final String CASE_FAILED_SCREEN_SHORT_PATH = "caseFailedScreenShortPath";

	private static String reportName = ByteBugConfig.REPORT_NAME;
	private static String splitTimeAndMsg = "===";

	public static void log(String msg) {
		long timeMillis = Calendar.getInstance().getTimeInMillis();
		Reporter.log(timeMillis + splitTimeAndMsg + msg, true);
	}

	public static String getReportName() {
		return reportName;
	}

	public static String getSpiltTimeAndMsg() {
		return splitTimeAndMsg;
	}

	public static void setReportName(String reportName) {
		if(StrUtil.isNotEmpty(reportName)){
			ReportUtil.reportName = reportName;
		}
	}
}

