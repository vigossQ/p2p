package cn.wolfcode.p2p.base.service.impl;

import cn.wolfcode.p2p.base.VerifyCodeVo;
import cn.wolfcode.p2p.base.service.IVerifyCodeService;
import cn.wolfcode.p2p.base.utils.BidConst;
import cn.wolfcode.p2p.base.utils.DateUtil;
import cn.wolfcode.p2p.base.utils.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.UUID;

@Service
@Transactional
public class VerifyCodeServiceImpl implements IVerifyCodeService {
    @Override
    public void sendVerifyCode(String phoneNumber) {
        //判断上次发送短信的时候是否超过90秒
        VerifyCodeVo vo = UserContext.getVerifyCodeVo();
        if (vo == null || DateUtil.getBetweenTime(vo.getSendTime(), new Date()) > BidConst.MESSAGE_INTERVAL_TIME) {
            //1.生成4位验证码
            String randomCode = UUID.randomUUID().toString().substring(0, 4);
            //2.执行发送短信.TODO
            StringBuilder msg = new StringBuilder(50);
            msg.append("这是您的手机认证码:").append(randomCode).append(",有效期为").append(BidConst.MESSAGER_VAILD_TIME).append("分钟,请尽快使用!【狼码教育公司】");
            //模拟短信发送
            //System.out.println(msg);
            //真实发送短信
            try {
                sendSms(phoneNumber, msg.toString());

                //3.把验证码,手机号,发送时间存入session
                vo = new VerifyCodeVo();
                vo.setPhoneNumber(phoneNumber);
                vo.setRandomCode(randomCode);
                vo.setSendTime(new Date());
                UserContext.setVerifyCodeVo(vo);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }
        } else {
            throw new RuntimeException("您发送短信太频繁,请稍后再试!");
        }
    }

    private void sendSms(String phoneNumber, String content) throws Exception {
        //http://utf8.api.smschinese.cn/?Uid=本站用户名&Key=接口安全秘钥&smsMob=手机号码&smsText=验证码:8888
        //模拟http的请求
        URL url = new URL("http://utf8.api.smschinese.cn");
        //创建连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置请求的方式
        conn.setRequestMethod("POST");
        //是否需要获取响应数据
        conn.setDoOutput(true);
        //info
        StringBuilder param = new StringBuilder(50);
        param.append("Uid=").append("qws491571070")
                .append("&Key=").append("d41d8cd98f00b204e980")
                .append("&smsMob=").append(phoneNumber)
                .append("&smsText=").append(content);
        //把数据写到对方服务器中
        conn.getOutputStream().write(param.toString().getBytes(Charset.forName("utf-8")));
        //连接对方服务器
        conn.connect();
        String repStr = StreamUtils.copyToString(conn.getInputStream(), Charset.forName("utf-8"));
        int repCode = Integer.parseInt(repStr);
        System.out.println(repStr);
        System.out.println(content);
        if (repCode < 0) {
            throw new RuntimeException("短信发送失败");
        }
    }

    @Override
    public boolean validate(String phoneNumber, String verifyCode) {
        //1.判断验证码是否正确,判断手机号码是否一致,判断验证码是否过期
        VerifyCodeVo vo = UserContext.getVerifyCodeVo();
        if (vo != null &&//发过短信
                vo.getPhoneNumber().equals(phoneNumber) &&//手机号码一致
                vo.getRandomCode().equalsIgnoreCase(verifyCode) &&//验证码正确
                DateUtil.getBetweenTime(vo.getSendTime(), new Date()) < BidConst.MESSAGER_VAILD_TIME * 24 * 60 * 60) {//验证码在有效期内
            return true;
        }
        return false;
    }
}
