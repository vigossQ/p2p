package cn.wolfcode.p2p.website;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Reimu on 2018/3/29.
 */
public class URLTest {
    public static void main(String[] args) throws Exception {
        //模拟http的请求
        URL url = new URL("https://way.jd.com/turing/turing");
        //创建连接
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //设置请求的方式
        conn.setRequestMethod("POST");
        //是否需要获取响应数据
        conn.setDoOutput(true);
        //info
        StringBuilder param = new StringBuilder(50);
        param.append("info")
                .append("&loc=")
                .append("&userid=")
                .append("&appkey=");
        //把数据写到对方服务器中
        conn.getOutputStream().write(param.toString().getBytes(Charset.forName("utf-8")));
        //连接对方服务器
        conn.connect();
    }
}
