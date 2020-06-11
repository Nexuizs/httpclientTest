package pool;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.HttpConnectionFactory;
import org.apache.http.conn.ManagedHttpClientConnection;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultHttpResponseParserFactory;
import org.apache.http.impl.conn.ManagedHttpClientConnectionFactory;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.io.DefaultHttpRequestWriterFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zs
 * @description http连接池的相关获取信息
 */
public class HttpClientUtils {

    public static final Logger log = LoggerFactory.getLogger(HttpClientUtils.class);

    private static PoolingHttpClientConnectionManager manager = null;
    private static CloseableHttpClient httpClient = null;

    public static void main(String[] args) {
        testGet();
//        testPost();
    }

    public static synchronized CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            System.out.println("---------------------------------------------------------创建");

            //注册访问协议相关的Socket工厂
            Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                    .<ConnectionSocketFactory>create()
                    .register("http", PlainConnectionSocketFactory.INSTANCE)
                    .register("https", SSLConnectionSocketFactory.getSystemSocketFactory())
                    .build();

            //HttpConnection 工厂:配置写请求/解析响应处理器
            HttpConnectionFactory<HttpRoute, ManagedHttpClientConnection> connectionFactory
                    = new ManagedHttpClientConnectionFactory(
                    DefaultHttpRequestWriterFactory.INSTANCE,
                    DefaultHttpResponseParserFactory.INSTANCE);
            //DNS 解析器
            DnsResolver dnsResolver = SystemDefaultDnsResolver.INSTANCE;
            //创建池化连接管理器
            manager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, connectionFactory, dnsResolver);
            //默认为Socket配置
            SocketConfig defaultSocketConfig = SocketConfig.custom().setTcpNoDelay(true).build();
            manager.setDefaultSocketConfig(defaultSocketConfig);
            //设置整个连接池的最大连接数
            manager.setMaxTotal(300);
            //每个路由的默认最大连接，每个路由实际最大连接数由DefaultMaxPerRoute控制，而MaxTotal是整个池子的最大数
            //设置过小无法支持大并发(ConnectionPoolTimeoutException) Timeout waiting for connection from pool
            //每个路由的最大连接数
            manager.setDefaultMaxPerRoute(200);
            //在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证，默认为2s
            manager.setValidateAfterInactivity(5 * 1000);
            //默认请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom()
                    //设置连接超时时间，2s
                    .setConnectTimeout(2 * 1000)
                    //设置等待数据超时时间，5s
                    .setSocketTimeout(5 * 1000)
                    //设置从连接池获取连接的等待超时时间
                    .setConnectionRequestTimeout(2000)
                    .build();
            //创建HttpClient
            httpClient = HttpClients.custom()
                    //如果我们不给httpclient配置指定的连接管理器，在默认情况下，
                    // httpclient也会自动使用PoolingHttpClientConnectionManager作为连接管理器。
                    // 但是PoolingHttpClientConnectionManager默认的maxConnPerRoute和maxConnTotal分别是是2和20。
                    // 也就是对于每个服务器最多只会维护2个连接，看起来有点少。所以，在日常使用时我们尽量使用自己配置的连接管理器比较好。
                    .setConnectionManager(manager)
                    //连接池不是共享模式
                    .setConnectionManagerShared(false)
                    //开启后台线程定期回收空闲连接
                    .evictIdleConnections(60L, TimeUnit.SECONDS)
                    //开启后台线程定期回收过期连接
                    .evictExpiredConnections()
                    //连接存活时间，如果不设置，则根据长连接信息决定
                    .setConnectionTimeToLive(60, TimeUnit.SECONDS)
                    //设置默认请求配置
                    .setDefaultRequestConfig(defaultRequestConfig)
                    //连接重用策略，即是否能keepAlive
                    .setConnectionReuseStrategy(DefaultConnectionReuseStrategy.INSTANCE)
                    //长连接配置，即获取长连接生产多长时间
                    .setKeepAliveStrategy(DefaultConnectionKeepAliveStrategy.INSTANCE)
                    //设置重试次数，默认是3次，当前是禁用掉（根据需要开启）
                    .setRetryHandler(new DefaultHttpRequestRetryHandler(0, false))
                    .build();

            //JVM 停止或重启时，关闭连接池释放掉连接(跟数据库连接池类似)
            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    try {
                        if (httpClient != null) {
                            httpClient.close();
                        }
                    } catch (IOException e) {
                        log.error("error when close httpClient:{}", e);
                    }
                }
            });
        }
        return httpClient;
    }

    public static void testGet() {
        try {
            //第一步：把HttpClient使用的jar包添加到工程中。
            //第二步：创建一个HttpClient的测试类
            //第三步：创建测试方法。
            //第四步：创建一个HttpClient对象
            CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();
            //第五步：创建一个HttpGet对象，需要制定一个请求的url
            HttpGet get = new HttpGet("http://www.baidu.com");
            //第六步：执行请求。
            CloseableHttpResponse response = httpClient.execute(get);
            //第七步：接收返回结果。HttpEntity对象。
            HttpEntity entity = response.getEntity();
            //第八步：取响应的内容。
            String html = EntityUtils.toString(entity, "utf-8");
            System.out.println(html);
            //第九步：关闭response、HttpClient
            response.close();
            httpClient.close();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    public static void testPost() {
        try {
            //  第一步：创建一个httpClient对象
            CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();
            //  第二步：创建一个HttpPost对象。需要指定一个url
            HttpPost post=new HttpPost("http://www.baidu.com");
            //  第三步：创建一个list模拟表单，list中每个元素是一个NameValuePair对象
            List<NameValuePair> formList=new ArrayList<NameValuePair>();
            formList.add(new BasicNameValuePair("name","张三"));
            formList.add(new BasicNameValuePair("pass","1234"));
            //  第四步：需要把表单包装到Entity对象中。StringEntity
            StringEntity entity=new UrlEncodedFormEntity(formList,"utf-8");
            post.setEntity(entity);
            //  第五步：执行请求。
            CloseableHttpResponse response = httpClient.execute(post);
            //  第六步：接收返回结果
            HttpEntity httpEntity=response.getEntity();
            String result = EntityUtils.toString(httpEntity);
            System.out.println(result);
            //  第七步：关闭流。
            response.close();
            httpClient.close();
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
    }

    /**
     * post请求传输map数据
     *
     * @param url      请求地址
     * @param map      传递的参数
     * @param encoding 参数编码
     * @return
     * @throws 
     * @throws IOException
     */
    public static String sendPostDataByMap(String url, Map<String, String> map, String encoding) {
        String result = "";
        CloseableHttpResponse response = null;
        try {
            // 创建httpclient对象
            CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();
            // 创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);
            // 装填参数
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            // 设置参数到请求对象中
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, encoding));
            // 设置header信息
            // 指定报文头【Content-type】、【User-Agent】
            httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
            httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            // 执行请求操作，并拿到结果（同步阻塞）
            response = httpClient.execute(httpPost);
            // 获取结果实体
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
            // 释放链接
            response.close();
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 释放链接
            try {
                response.close();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
            return result;
        }
    }

    /**
     * post请求传输json数据
     *
     * @param url      请求地址
     * @param json     传递的参数
     * @param encoding 参数编码
     * @return
     * @throws 
     * @throws IOException
     */
    public static String sendPostDataByJson(String url, String json, String encoding) {
        String result = "";
        CloseableHttpResponse response = null;
        try {
            // 创建httpclient对象
            CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();
            // 创建post方式请求对象
            HttpPost httpPost = new HttpPost(url);
            // 设置参数到请求对象中
            StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
            // "utf-8"
            stringEntity.setContentEncoding(encoding);
            httpPost.setEntity(stringEntity);
            // 执行请求操作，并拿到结果（同步阻塞）
            response = httpClient.execute(httpPost);
            // 获取结果实体
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
            // 释放链接
            response.close();
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            // 释放链接
            try {
                response.close();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
            return result;
        }
    }

    /**
     * get请求传输数据
     *
     * @param url
     * @param encoding
     * @return
     * @throws 
     * @throws IOException
     */
    public String sendGetData(String url, String encoding) {
        String result = "";
        CloseableHttpResponse response = null;
        try {
            // 创建httpclient对象
            CloseableHttpClient httpClient = HttpClientUtils.getHttpClient();

            // 创建get方式请求对象
            HttpGet httpGet = new HttpGet(url);
            httpGet.addHeader("Content-type", "application/json");
            // 通过请求对象获取响应对象
            response = httpClient.execute(httpGet);

            // 获取结果实体
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
            // 释放链接
            response.close();
            return result;
        } catch (Exception e) {
            try {
                response.close();
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
            return result;
        }
    }
}
