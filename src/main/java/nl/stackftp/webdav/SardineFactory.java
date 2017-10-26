package nl.stackftp.webdav;

import com.github.sardine.Sardine;
import com.github.sardine.impl.SardineImpl;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

final class SardineFactory {

    /**
     * Create a Sardine instance.
     *
     * @param username The username.
     * @param password The password.
     * @return The Sardine instance.
     */
    public static Sardine begin(String username, String password) {
        Sardine sardine = new SardineImpl(
                HttpClients.custom()
                        .setUserAgent("StackFtp")
                        .setDefaultRequestConfig(RequestConfig.custom()
                                .setCookieSpec(CookieSpecs.STANDARD).build()));

        sardine.setCredentials(username, password);

        return sardine;
    }
}
