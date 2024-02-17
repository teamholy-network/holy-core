package de.teamholy.core.paste;

import com.google.gson.JsonObject;
import de.dytanic.cloudnet.wrapper.Wrapper;
import de.teamholy.core.api.utility.DiscordWebhook;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PasteService {

    private static final String PASTE_URL = "https://pastebin.com/api/api_post.php";

    private static final String API_KEY = "vWpOZeOhQqtctVA2Hzt2IFlTESG9P2W7";

    public static String paste(String service, String content) {
        String response = null;
        try {
            URL url = new URL(PASTE_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String postData = "api_option=paste&api_user_key=" + "" +
                "&api_paste_private=" + 1 +
                "&api_paste_name=" + "TeamHoly-Log-" + service + "-" + System.currentTimeMillis() +
                "&api_paste_format=" + "java" +
                "&api_dev_key=" + API_KEY +
                "&api_paste_code=" + URLEncoder.encode(content, "UTF-8");

            OutputStream stream = connection.getOutputStream();

            stream.write(postData.getBytes("UTF-8"));
            stream.flush();
            stream.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder responseBuilder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    responseBuilder.append(line);
                }
                reader.close();

                response = responseBuilder.toString();
             //   System.out.println("Paste URL: " + response);
            } else {
                System.out.println("Error: " + responseCode);
            }
            connection.getInputStream().close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    public static void main(String[] args) {

        StringBuilder sb = new StringBuilder();

        List<String> msg = List.of("test", "test");

        try {
            for (String message : msg) {
                sb.append(message).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        String paste = paste("test", sb.toString());
        System.out.println(paste);
    }
}
