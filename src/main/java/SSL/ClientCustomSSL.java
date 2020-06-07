package SSL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.File;

/**
 * 设置自定义sslcontext
 */
public class ClientCustomSSL {
    public static void main(String[] args) throws Exception {
        //密钥库路径，密钥库密码
        SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(new File("my.keystore"), "nopassword".toCharArray(),
                new TrustSelfSignedStrategy()).build();

        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1"},
                null,
                SSLConnectionSocketFactory.getDefaultHostnameVerifier());
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
        try {

            HttpGet httpget = new HttpGet("https://github.com/Arronlong/httpclientutil");

            System.out.println("Executing request " + httpget.getRequestLine());

            CloseableHttpResponse response = httpClient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }
}
