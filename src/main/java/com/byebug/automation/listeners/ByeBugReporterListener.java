package com.byebug.automation.listeners;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.ZipUtil;
import com.aventstack.extentreports.AnalysisStrategy;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.byebug.automation.api.ApiSingleton;
import com.byebug.automation.config.ByteBugConfig;
import com.byebug.automation.ui.UISingleton;
import com.byebug.automation.utils.ReportUtil;
import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.util.*;

public class ByeBugReporterListener implements IReporter {

    private static Map<String, ExtentTest> classTestMap = new HashMap<>();
    private int allSuiteFailSize = 0;
    private int allSuitePassSize = 0;
    private int allSuiteSkipSize = 0;

    private Long suiteTestStartTime, suiteTestEndTime;
    private static final String FILE_NAME = "index.html";

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        ExtentReports extentReports = init();

        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();

            for (ISuiteResult r : result.values()) {
                String suitTestUUID = RandomUtil.randomString(6);
                ITestContext context = r.getTestContext();

                ExtentTest suiteTest = extentReports.createTest(r.getTestContext().getSuite().getName() + "-" + context.getName());
                buildTestNodes(suitTestUUID, suiteTest, context.getFailedTests(), Status.FAIL);
                allSuiteFailSize += context.getFailedTests().size();
                buildTestNodes(suitTestUUID, suiteTest, context.getSkippedTests(), Status.SKIP);
                allSuiteSkipSize += context.getSkippedTests().size();
                buildTestNodes(suitTestUUID, suiteTest, context.getPassedTests(), Status.PASS);
                allSuitePassSize += context.getPassedTests().size();

                // Bugfix: 修改Suit-Test未正确显示耗时问题
                if(suiteTest != null) {
                    suiteTest.getModel().setStartTime(getTime(suiteTestStartTime));
                    suiteTest.getModel().setEndTime(getTime(suiteTestEndTime));
                    suiteTestStartTime = null;
                    suiteTestEndTime = null;
                }
            }

        }

        extentReports.flush();
        callBackListener();
    }

    private ExtentReports init() {
        File reportDir= new File(ByteBugConfig.OUTPUT_DIR);
        if(!reportDir.exists() && !reportDir.isDirectory()){
            reportDir.mkdir();
        }

        ExtentReports extentReports = new ExtentReports();
        ExtentSparkReporter spark = new ExtentSparkReporter(System.getProperty("user.dir") + ByteBugConfig.OUTPUT_DIR + FILE_NAME);
        extentReports.attachReporter(spark);
        extentReports.setReportUsesManualConfiguration(true);
        extentReports.setAnalysisStrategy(AnalysisStrategy.SUITE);

        return extentReports;
    }

    private void buildTestNodes(String suitTestUUID, ExtentTest suiteTest, IResultMap tests, Status status) {
        ExtentTest testNode;
        ExtentTest classNode;

        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {
                String className = result.getInstance().getClass().getSimpleName();

                // Bugfix：避免同一个测试类，放在不同的测试套件执行的场景下，测试类全部放在第一个测试套件下的
                String classTestMapKey = suitTestUUID + className;

                // Bugfix：避免重试场景下，测试方法在执行，最后一次重试前的所有的失败记录，都录入到测试报告中
                if(status.equals(Status.SKIP) && result.wasRetried()) {
                    continue;
                }

                if (classTestMap.containsKey(classTestMapKey)) {
                    classNode = classTestMap.get(classTestMapKey);
                } else {
                    classNode = suiteTest.createNode(className);
                    classTestMap.put(classTestMapKey, classNode);

                    // Bugfix: 修改Class未正确显示耗时问题
                    classNode.getModel().setStartTime(null);
                    classNode.getModel().setEndTime(null);
                }

                testNode = classNode.createNode(result.getMethod().getMethodName(), result.getMethod().getDescription());

                // 添加标签功能
                String[] groups = result.getMethod().getGroups();
                assignGroups(testNode, groups);

                // feature：添加用例过程中的log输出到测试报告中
                if(status != Status.PASS || !ByteBugConfig.REPORT_JUST_ERROR_CASE) {
                    List<String> outputList = Reporter.getOutput(result);
                    for(String output : outputList) {
                        testNode.info(output.replaceAll("<","&lt;").replaceAll(">","&gt;"));
                    }
                }

                if (result.getThrowable() != null) {
                    testNode.log(status, result.getThrowable());
                } else {
                    testNode.log(status, "Test " + status.toString().toLowerCase() + "ed");
                }

                if(status != Status.PASS || !ByteBugConfig.REPORT_JUST_ERROR_CASE) {
                    // feature：支持添加截图
                    String caseFailedScreenShotPathKey = ReportUtil.CASE_FAILED_SCREEN_SHORT_PATH;
                    Object caseFailedScreenShotPathObject = result.getAttribute(caseFailedScreenShotPathKey);
                    if(caseFailedScreenShotPathObject != null) {
                        String path = (String)caseFailedScreenShotPathObject;
                        try {
                            testNode.addScreenCaptureFromPath(path);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                // feature：添加用例的log输出到报告中-添加日志记录时间
                testNode.getModel().getLogs().forEach(log -> {
                    String details = log.getDetails();
                    if(details.contains(ReportUtil.getSpiltTimeAndMsg())){
                        String time = details.split(ReportUtil.getSpiltTimeAndMsg())[0];
                        log.setTimestamp(getTime(Long.parseLong(time)));
                        log.setDetails(details.substring(time.length()+ReportUtil.getSpiltTimeAndMsg().length()));
                    }else{
                        log.setTimestamp(getTime(result.getEndMillis()));
                    }
                });

                long resultStartTime = result.getStartMillis();
                long resultEndTime = result.getEndMillis();
                testNode.getModel().setStartTime(getTime(resultStartTime));
                testNode.getModel().setEndTime(getTime(resultEndTime));

                // Bugfix: 修改Class未正确显示耗时问题
                Date classNodeStartTime = classNode.getModel().getStartTime();
                if(classNodeStartTime == null || classNodeStartTime.getTime() > resultStartTime) {
                    classNode.getModel().setStartTime(getTime(resultStartTime));
                }
                Date classNodeEndTime = classNode.getModel().getEndTime();
                if(classNodeEndTime == null || classNodeEndTime.getTime() < resultEndTime) {
                    classNode.getModel().setEndTime(getTime(resultEndTime));
                }

                // Bugfix: 修改Suit-Test未正确显示耗时问题
                if(suiteTestStartTime == null || resultStartTime < suiteTestStartTime) {
                    suiteTestStartTime = resultStartTime;
                }
                if(suiteTestEndTime == null || resultEndTime > suiteTestEndTime) {
                    suiteTestEndTime = resultEndTime;
                }
            }
        }
    }

    private void callBackListener() {
        ByeBugCallbackListener listener = ApiSingleton.getInstance().getByeBugCallbackListener();
        if(listener == null) {
            listener = UISingleton.getInstance().getByeBugCallbackListener();
        }
        if(listener != null) {
            String projectRootPath = System.getProperty("user.dir");
            int total = allSuitePassSize + allSuiteFailSize + allSuiteSkipSize;
            String zipName = "index-" + DateTime.now().toString().replaceAll(":", "-") + ".zip";
            ZipUtil.zip(projectRootPath + ByteBugConfig.OUTPUT_DIR, projectRootPath + File.separator + zipName, false);
            listener.afterTestReportGenerated(projectRootPath + "/" + zipName, total, allSuitePassSize, allSuiteFailSize, allSuiteSkipSize);
            FileUtil.del(projectRootPath + File.separator + zipName);
        }
    }

    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

    /**
     * 借助testng的groups注解内容，实现ExtentReport的标签功能
     * @param test
     * @param groups
     */
    public void assignGroups(ExtentTest test, String[] groups) {
        if (groups.length > 0) {
            for (String g : groups) {
                if (g.startsWith("d:") || g.startsWith("device:")) {
                    String d = g.replace("d:", "").replace("device:", "");
                    test.assignDevice(d);
                } else if (g.startsWith("a:") || g.startsWith("author:")) {
                    String a = g.replace("a:", "").replace("author:", "");
                    test.assignAuthor(a);
                } else if (g.startsWith("t:") || g.startsWith("tag:")) {
                    String t = g.replace("t:", "").replace("tag:", "");
                    test.assignCategory(t);
                } else {
                    test.assignCategory(g);
                }
            }
        }
    }

}
