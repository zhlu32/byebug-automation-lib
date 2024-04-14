package com.byebug.automation.utils.mail;

import cn.hutool.core.util.StrUtil;
import com.byebug.automation.config.ByteBugConfig;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Date;
import java.util.Properties;

public class MailUtil {

    // 是否要求身份认证
    private final static String IS_AUTH = "true";
    // 是否启用调试模式（启用调试模式可打印客户端与服务器交互过程时一问一答的响应消息）
    private final static String IS_ENABLED_DEBUG_MOD = "false";

    // 初始化连接邮件服务器的会话信息
    private static Properties props = null;

    //初始化加载配置信息
    static {
        props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", ByteBugConfig.EMAIL_HOST);
        props.setProperty("mail.smtp.port", ByteBugConfig.EMAIL_PORT);
        props.setProperty("mail.smtp.auth", IS_AUTH);
        props.setProperty("mail.debug", IS_ENABLED_DEBUG_MOD);
    }

    public static void main(String[] args) {
        String projectRootPath = System.getProperty("user.dir");
        sendEmail("midisec@126.com", "sub", "body", projectRootPath + "/properties/default.properties");
    }

    public static boolean sendEmail(String recipients, String subject, String content, String fileStr) {
        //使用SSL，企业邮箱必需！
        MailSSLSocketFactory mailSSLSocketFactory = null;
        try {
            mailSSLSocketFactory = new MailSSLSocketFactory();
            mailSSLSocketFactory.setTrustAllHosts(true);
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", mailSSLSocketFactory);

            Session session = Session.getDefaultInstance(props, new SimpleAuthenticator(ByteBugConfig.EMAIL_FROM_USER, ByteBugConfig.EMAIL_FROM_PWD));
            session.setDebug(true);
            MimeMessage mimeMessage = new MimeMessage(session);

            //发件人
            if (StrUtil.isNotEmpty(ByteBugConfig.EMAIL_FROM_NICKNAME)) {
                mimeMessage.setFrom(new InternetAddress(ByteBugConfig.EMAIL_FROM_USER, ByteBugConfig.EMAIL_FROM_NICKNAME));
            } else {
                mimeMessage.setFrom(new InternetAddress(ByteBugConfig.EMAIL_FROM_USER));
            }
            //收件人
            Address[] toAddress;
            if(recipients.contains(";")) {
                String[] tos = recipients.split(";");
                int num = tos.length;
                toAddress = new Address[num];
                for(int i = 0; i < num; i++) {
                    toAddress[i] = new InternetAddress(tos[i]);
                }
            }else {
                toAddress = new Address[1];
                toAddress[0] = new InternetAddress(recipients);
            }
            mimeMessage.addRecipients(Message.RecipientType.TO, toAddress);
            //主题
            mimeMessage.setSubject(subject);
            //时间
            mimeMessage.setSentDate(new Date());
            //容器类，可以包含多个MimeBodyPart对象
            Multipart mp = new MimeMultipart();

            //MimeBodyPart可以包装文本，图片，附件
            MimeBodyPart body = new MimeBodyPart();
            //HTML正文
            body.setContent(content, "text/html; charset=UTF-8");
            mp.addBodyPart(body);

            //添加图片&附件
            if (StrUtil.isNotEmpty(fileStr)) {
                body = new MimeBodyPart();
                body.attachFile(fileStr);
                mp.addBodyPart(body);
            }

            //设置邮件内容
            mimeMessage.setContent(mp);
            //仅仅发送文本
            //mimeMessage.setText(content);
            mimeMessage.saveChanges();
            Transport.send(mimeMessage);
            // 发送成功
            return true;

        } catch (Exception e1) {
            return false;
        }

    }

}