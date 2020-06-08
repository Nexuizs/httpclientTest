package thread;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class HttpClientThreadTest {
    private AtomicInteger counter = new AtomicInteger(0);
    private String url = "http://www.baidu.com/";

    public static void main(String[] args) {
        new HttpClientThreadTest().test();
    }

    // 执行测试
    private void test() {
        int number = 100000; // 总请求数
        int concurrent = 50; // 每次并发请求数
        CountDownLatch countDownLatch = new CountDownLatch(number); // 计数器
        ExecutorService threadPool = Executors.newFixedThreadPool(concurrent); // 线程池
        int concurrentPer = concurrent;
        boolean over = false;
        while(!over) {
            number = number - concurrent;
            if(number <= 0) {
                concurrentPer = number + concurrent;
                over = true;
            }

            // 线程池批量提交
            for(int i = 0; i < concurrentPer; i++) {
                threadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            request(url);
                            Thread.sleep(100);
                        } catch (IOException | InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            countDownLatch.countDown();
                        }
                    }
                });
            }
        }
        try {
            countDownLatch.await();
            threadPool.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 访问指定地址
    private void request(String url) throws IOException {
        HttpGet httpGet = new HttpGet(url);
        commnicate(httpGet);
    }

    // 负责底层通信处理
    private void commnicate(HttpRequestBase request) throws IOException {
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
                return EntityUtils.toString(response.getEntity());
            }
        };

        HttpClient client = HttpClients.createDefault();
        String body = client.execute(request, responseHandler); // 线程可能会在这里被阻塞
        System.out.println(String.format("body size: %s, counter: %s", body.length(), counter.incrementAndGet()));
    }
}
