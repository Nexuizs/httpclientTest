package auth;

import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;

public class CookieDemo {
    public static void main(String[] args) {
        // Create a local instance of cookie store
        CookieStore cookieStore = new BasicCookieStore();
        // Populate cookies if needed
        BasicClientCookie cookie = new BasicClientCookie("name", "value");
        cookie.setDomain(".mycompany.com");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);
        // Set the store
        CloseableHttpClient httpclient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
    }
}
