package proxy;

import org.apache.http.HttpHost;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class ClientExecuteProxy {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            HttpHost target = new HttpHost("httpbin.org", 443, "https");
            HttpHost proxy = new HttpHost("127.0.0.1", 8080, "http");
            RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy).build();
            HttpGet httpGet = new HttpGet("/");
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response = httpClient.execute(target, httpGet);
            try {
                System.out.println(EntityUtils.toString(response.getEntity()));
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            } finally {
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpClient.close();
        }
    }
}
