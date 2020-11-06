package test;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class InterfaceInfoTest {
    public static void main(String[] args) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(15000).setConnectTimeout(15000).setConnectionRequestTimeout(15000).build();
        HttpPost httpPost = new HttpPost("http://172.18.110.60:37811/query/policyApi/zsApiCodeName");
        httpPost.setConfig(requestConfig);
        try {
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String s = EntityUtils.toString(entity, "UTF-8");
            System.out.println(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
