package SSL;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SSLDefault {

    private static final SSLHandler simpleVerifier = new SSLHandler();

    private static class SSLHandler implements X509TrustManager, HostnameVerifier {

        /**
         * 在握手期间，如果URL的主机名和服务器的标识主机名不匹配，
         * 则验证机制可以回调此接口实现程序来确定是否应该允许此连接，
         * 如果回调内实现不恰当，默认接受所有域名，则有安全风险。
         * @return
         */
        @Override
        public boolean verify(String s, SSLSession sslSession) {
            return true;
        }

        /**
         * 该方法检查客户端的证书，若不信任该证书则抛出异常。由于我们不需要对客户端进行认证，
         * 因此我们只需要执行默认的信任管理器的这个方法。JSSE中，默认的信任管理器类为TrustManager。
         * @throws java.security.cert.CertificateException
         */
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        /**
         * 该方法检查服务器的证书，若不信任该证书同样抛出异常。通过自己实现该方法，可以使之信任我们指定的任何证书。在实现该方法时，
         * 也可以简单的不做任何处理，即一个空的函数体，由于不会抛出异常，它就会信任任何证书。
         * @throws java.security.cert.CertificateException
         */
        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }
        /**
         * 返回受信任的X509证书数组。
         * @return
         */
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static void main(String[] args) throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, new TrustManager[]{simpleVerifier}, new java.security.SecureRandom());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, simpleVerifier);
        CloseableHttpClient httpClient = HttpClients.custom().build();
        try {

            HttpGet httpget = new HttpGet("https://github.com/Arronlong/httpclientutil");

            System.out.println("Executing request " + httpget.getRequestLine());

            CloseableHttpResponse response = httpClient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                System.out.println("----------------------------------------");
                System.out.println(response.getStatusLine());
                System.out.println(EntityUtils.toString(entity, "utf-8"));
                EntityUtils.consume(entity);
            } finally {
                response.close();
            }
        } finally {
            httpClient.close();
        }
    }
}
