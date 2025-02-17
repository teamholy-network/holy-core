package de.teamholy.core.api.paste;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Copyright (c) charon, All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by charon
 **/
public class PasteService {

    private static final String PASTE_URL = "https://pastebin.com/api/api_post.php";

    private static final String API_KEY = "hIoFTw2oSQ3cVPyQ7bZO_oeGS8dXzZ0q";
    private static final String USER_KEY = "029c32be5b8b52275e20331e97a5303f";

    public static String paste(String service, String content) {
        String response = null;
        try {
            URL url = new URL(PASTE_URL);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            String postData = "api_option=paste&api_dev_key=" + API_KEY +
                "&api_paste_private=" + 2 +
                "&api_user_key=" + USER_KEY +

                "&api_paste_name=" + "Log-" + service + "-" + System.currentTimeMillis() +
                "&api_paste_format=" + "java" +
                "&api_paste_expire_date=" + "1W" +

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

    public static String logFile(String service, String content) {
        File logs = new File("bukkit-crashlogs");
        if (!logs.exists()) {
            logs.mkdirs();
        }
        File file = new File("bukkit-crashlogs/" + service + "-" + System.currentTimeMillis() + ".txt");

        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file.getAbsolutePath();
    }


    public static void main(String[] args) throws Exception {
        String response = paste("Test2", "Test");
        System.out.println(response);
    }
}
