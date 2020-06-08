package auth;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

/**
 * 身份验证
 */
public class HttpClientWithBasicAuth {

    public static void main(String args[]){
        // 创建HttpClientBuilder
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        // 设置BasicAuth
        CredentialsProvider provider = new BasicCredentialsProvider();
        // Create the authentication scope
        AuthScope scope = new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, AuthScope.ANY_REALM);
        // Create credential pair，在此处填写用户名和密码
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials("admin", "lh886");
        // Inject the credentials
        provider.setCredentials(scope, credentials);
        // Set the default credentials provider
        httpClientBuilder.setDefaultCredentialsProvider(provider);
        // HttpClient
        CloseableHttpClient closeableHttpClient = httpClientBuilder.build();
        String result = "";
        HttpGet httpGet = null;
        HttpResponse httpResponse = null;
        HttpEntity entity = null;
        HttpPost httpPost = new HttpPost("http://10.231.34.210/api/v1/Cubes('%E9%A1%B9%E7%9B%AE%E8%82%A1%E4%B8%9C%E6%96%B9%E6%8C%87%E6%A0%87')/Views('%E7%B3%BB%E7%BB%9F%E6%8E%A5%E5%8F%A3')/tm1.Execute?$expand=Axes($expand=Hierarchies($select=Name),Tuples($expand=Members($select=Name))),Cells");
        httpPost.addHeader("Content-Type","application/json; charset=utf-8");
        try {
            httpResponse = closeableHttpClient.execute(httpPost);
            entity = httpResponse.getEntity();
            if( entity != null ){
                result = EntityUtils.toString(entity);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 关闭连接
        try {
            closeableHttpClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(result);
    }
}