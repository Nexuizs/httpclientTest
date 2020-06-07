import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;

public class PostDemo {
    public static void main(String[] args) {
        HttpPost httpPost = new HttpPost("http://www.baidu.com");
        String s = sendPost(httpPost, null);
        System.out.println(s);
    }

    // application/x-www-form-urlencoded
    public static String sendPost(HttpPost post, List<NameValuePair> nvps) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String content = null;
        try {
            // nvps是包装请求参数的list
            if (nvps != null) {
                post.setEntity(new UrlEncodedFormEntity(nvps, "UTF-8"));
            }
            // 执行请求用execute方法，content用来帮我们附带上额外信息
            response = httpclient.execute(post);
            // 得到相应实体、包括响应头以及相应内容
            HttpEntity entity = response.getEntity();
            // 得到response的内容
            content = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return content;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content;
    }

    // application/json
    public static String sendPostJson (String url, JSONObject object) {
        HttpPost httpPost = new HttpPost(url);
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // json方式
            StringEntity entity = new StringEntity(object.toString(),"utf-8");//解决中文乱码问题
            entity.setContentEncoding("UTF-8");
            entity.setContentType("application/json;charset=UTF-8");
            httpPost.setEntity(entity);
            HttpResponse resp = httpclient.execute(httpPost);
            if(resp.getStatusLine().getStatusCode() == 200) {
                HttpEntity he = resp.getEntity();
                return EntityUtils.toString(he,"UTF-8");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
