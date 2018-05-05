package cn.wolfcode.p2p.base.service.impl;

import cn.wolfcode.p2p.base.domain.MailVerify;
import cn.wolfcode.p2p.base.service.IEmailService;
import cn.wolfcode.p2p.base.service.IMailVerifyService;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class EmailServiceImpl implements IEmailService {

    @Value("${email.applicationUrl}")
    private String applicationUrl;
    @Autowired
    private IMailVerifyService mailVerifyService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void sendEmail(String email) {
        //生成地址上的key
        String key = UUID.randomUUID().toString();
        //发送一封html邮件
        StringBuilder content = new StringBuilder(100);
        content.append("感谢注册P2P,这是您的认证邮件,点击<a href='").append(applicationUrl).append("/bindEmail?key=").append(key)
                .append("'>这里</a>完成注册,有效期为").append(BidConst.EMAIL_VAILD_DAY).append("天,请尽快认证");
        //执行邮件发送
        //System.out.println(content);
        //真实邮件发送
        try {
            //把数据存放到数据库中
            MailVerify mailVerify = new MailVerify();
            mailVerify.setUserid(UserContext.getCurrent().getId());
            mailVerify.setEmail(email);
            mailVerify.setSendTime(new Date());
            mailVerify.setUuid(key);
            mailVerifyService.save(mailVerify);
            //如果插入报错则事务可以回滚,但是发送邮件无法回滚
            sendRealEmail(email, content.toString());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    private void sendRealEmail(String email, String content) throws Exception {
        //邮件消息体
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        //设置收件人
        helper.setTo(email);
        //设置发件人
        helper.setFrom(fromEmail);
        //设置主题
        helper.setSubject("认证邮件");
        //添加邮件内容
        helper.setText(content, true);
        //执行邮件发送
        javaMailSender.send(mimeMessage);
    }
}
