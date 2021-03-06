package proxy;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileOutputStream;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class UseProxy {
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
        SSLContext sslContext = SSLContext.getInstance("TLSv1.1");
        sslContext.init(null, new TrustManager[]{simpleVerifier}, new java.security.SecureRandom());
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(sslContext, simpleVerifier);
        //创建httpClient实例
        CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
        //创建httpGet实例
        HttpGet httpGet = new HttpGet("https://www.tuicool.com/");
        //设置代理IP，设置连接超时时间 、 设置 请求读取数据的超时时间 、 设置从connect Manager获取Connection超时时间、
        HttpHost proxy = new HttpHost("36.59.117.88",4216);
        RequestConfig requestConfig = RequestConfig.custom()
                .setProxy(proxy)
                .setConnectTimeout(10000)
                .setSocketTimeout(10000)
                .setConnectionRequestTimeout(3000)
                .build();
        httpGet.setConfig(requestConfig);
        //设置请求头消息
        httpGet.setHeader("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.61 Safari/537.36");
        CloseableHttpResponse response = httpClient.execute(httpGet);
        if (response != null){
            HttpEntity entity = response.getEntity();  //获取返回实体
            if (entity != null){
                System.out.println("网页内容为:"+ EntityUtils.toString(entity,"utf-8"));
            }
        }
        if (response != null){
            response.close();
        }
        if (httpClient != null){
            httpClient.close();
        }
    }
}