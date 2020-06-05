import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 携带cookie
 */
public class HttpClientCookie {
    public static void main(String[] args) {
        String url = "http://172.18.111.106:8090/login";
        BasicCookieStore cookieStore = new BasicCookieStore();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        HttpPost httpPost = new HttpPost(url);
        List<NameValuePair> nvpr = new ArrayList<>();
        Map<String, Object> hashMap = new HashMap<>();
        hashMap.put("username", "admin");
        hashMap.put("password", "dbApp123!@#");
        for (Map.Entry<String, Object> entry : hashMap.entrySet()) {
            nvpr.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
        }
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nvpr, "utf-8"));
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            CloseableHttpResponse execute = httpClient.execute(httpPost);
            HttpEntity entity = execute.getEntity();
            if(null != entity){
                String result = EntityUtils.toString(entity);
                System.out.println(result);
                EntityUtils.consume(entity);
            }
            httpClient.close();
            execute.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
