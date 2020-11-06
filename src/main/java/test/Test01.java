package test;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test01 {
    public static void main(String[] args) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        HttpPost httpPost = new HttpPost("http://10.41.16.142:8105/a2/checkUserInfo?idcard=532625199401212124&name=赵礼云");
        HttpPost httpPost = new HttpPost("http://10.41.62.145/api/gateway/api/xchgshrApi/aggregationApi/c5e593e312bc4bb8a14848bb0e07533a");
        httpPost.setHeader("token", "klvVuMY8mkZlqX_WT972260BuwAB3Ura2VR2hnn5cjAYMRsH1rsF6WmEWaEZroqqIdrjj4wsv3GM+6m/wBBLk8aoBtS6IEUHCgcVyNRKCfb1OuxR5yfMVA==");
        List<NameValuePair> formList=new ArrayList<NameValuePair>();
        formList.add(new BasicNameValuePair("idcard","1234567891111"));
        formList.add(new BasicNameValuePair("name","张三"));
        //  第四步：需要把表单包装到Entity对象中。StringEntity
        httpPost.setEntity(new UrlEncodedFormEntity(formList,"utf-8"));
        CloseableHttpResponse execute = httpClient.execute(httpPost);
        if(execute.getStatusLine().getStatusCode() == 200){
            HttpEntity entity = execute.getEntity();
            String s = EntityUtils.toString(entity);
            System.out.println(s);
        }
    }
}
