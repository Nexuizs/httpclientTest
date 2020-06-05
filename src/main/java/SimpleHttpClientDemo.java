import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimpleHttpClientDemo {

    public static String send(String url, Map<String, String> param, String encoding) throws IOException {
        String body = "";
        //创建httpclient请求对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //创建post方式请求对象
        HttpPost post = new HttpPost(url);
        //填写参数
        List<NameValuePair> nvps = new ArrayList<>();
        if (param != null) {
            for (Map.Entry<String, String> entry : param.entrySet()) {
                nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        //设置参数header信息
        post.setEntity(new UrlEncodedFormEntity(nvps, encoding));
        post.setHeader("Content-type", "application/x-www-form-urlencoded");
        post.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
        //执行请求操作，并拿到结果
        CloseableHttpResponse response = httpClient.execute(post);
        //获取结果实体
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            body = EntityUtils.toString(entity, encoding);
        }
        EntityUtils.consume(entity);
        //释放连接
        httpClient.close();
        response.close();
        return body;
    }

    public static void main(String[] args) throws ParseException, IOException {
        String url = "http://www.jd.com/";
        String body = send(url, null, "utf-8");
        System.out.println("交易响应结果：");
        System.out.println(body);
    }
}
