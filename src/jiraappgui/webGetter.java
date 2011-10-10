/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jiraappgui;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author rogerunwin
 */

public class webGetter {
    private final URL url;
    private final String username;
    private final String password;

    public webGetter(String url, String username, String password) throws MalformedURLException {
        this.url = new URL(url);
        this.username = username;
        this.password = password;
    }

    public InputStream fetch() throws IOException {
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();

        String userPassword = username + ":" + password;

        con.setRequestProperty("Authorization", "Basic " + new sun.misc.BASE64Encoder().encode (userPassword.getBytes()));

        return con.getInputStream();
    }


}
