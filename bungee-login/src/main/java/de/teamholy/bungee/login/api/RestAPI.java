package de.teamholy.bungee.login.api;

import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class RestAPI {

    @Getter
    private static final RestAPI INSTANCE = new RestAPI();

    public RestAPIResponse get(String urlstring) {
        return get(urlstring,15000, null);
    }

    public RestAPIResponse get(String urlstring,Proxy proxy) {
        return get(urlstring,15000, proxy);
    }

    public RestAPIResponse get(String urlstring,int timeout,Proxy proxy) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL(urlstring.replaceAll("\n", ""));
            URLConnection con;
            if (proxy == null) {
                con = url.openConnection();
            } else {
                con = url.openConnection(proxy);
            }
            con.setConnectTimeout(timeout);
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine).append("\n");
            }
            in.close();
        } catch (IOException e) {
            Arrays.stream(e.getStackTrace())
                .map(StackTraceElement::toString)
                .forEach(System.out::println);
            return new RestAPIResponse("Error", true, urlstring);
        }
        return new RestAPIResponse(response.toString(), false, urlstring);
    }
}