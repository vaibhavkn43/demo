package in.onlinebiodatamaker.util;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONObject;

/**
 *
 * @author Vaibhav
 */
@Component
public class LogUtil {

    public String getCountry(String ip) {

        try {

            URL url = new URL("http://ip-api.com/json/" + ip);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader
                    = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String response = reader.readLine();

            JSONObject json = new JSONObject(response);

            return json.getString("country");

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    public String getDeviceType(String userAgent) {

        if (userAgent == null) {
            return "UNKNOWN";
        }

        if (userAgent.toLowerCase().contains("mobile")) {
            return "MOBILE";
        }

        if (userAgent.toLowerCase().contains("tablet")) {
            return "TABLET";
        }

        return "DESKTOP";
    }
}
