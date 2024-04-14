package com.mall.api.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.util.StrUtil;
import com.byebug.automation.listeners.ByeBugCallbackListener;
import com.byebug.automation.utils.ReportUtil;
import com.byebug.automation.utils.mail.MailUtil;

public class MallCallbackListener implements ByeBugCallbackListener {
    @Override
    public void onException(String filePath, String throwable) {

    }

    @Override
    public void afterTestReportGenerated(String filePath, int total, int passed, int failed, int skip) {
        String email = MallProperties.get(MallProperties.g_config_receipt_email);
        if(StrUtil.isNotEmpty(email)) {
            String sb = "测试系统地址：" + MallProperties.get(MallProperties.g_mall_host) +
                    "<br/><br/>总测试用例数：" + total +
                    "<br/>通过用例数：" + passed +
                    "<br/>失败用例数：" + failed +
                    "<br/>跳过用例数：" + skip;
            ReportUtil.log("邮件发送结果 "+ MailUtil.sendEmail(email, ReportUtil.getReportName() + "-【" + DateTime.now() + "】", sb, filePath));
        }
    }
}
