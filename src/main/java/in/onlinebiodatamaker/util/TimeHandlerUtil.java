package in.onlinebiodatamaker.util;

import java.time.LocalTime;
import java.util.Locale;

/**
 *
 * @author admin
 */
public class TimeHandlerUtil {

    public static String toMarathiTime(LocalTime time, Locale locale) {

        if (locale.getLanguage().equals("mr")) {

            int hour = time.getHour();
            int minute = time.getMinute();

            String period;

            if (hour < 5) {
                period = "पहाटे";
            } else if (hour < 12) {
                period = "सकाळी";
            } else if (hour < 16) {
                period = "दुपारी";
            } else if (hour < 20) {
                period = "संध्याकाळी";
            } else {
                period = "रात्री";
            }

            int displayHour = hour % 12;
            if (displayHour == 0) {
                displayHour = 12;
            }

            return String.format("%s %d वाजून %d मिनिटे", period, displayHour, minute);
        }

        // English fallback
        return time.toString();
    }
}
