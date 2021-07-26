package io.github.icrazyblaze.twitchmod.util;

import io.github.icrazyblaze.twitchmod.Main;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * This class is responsible for returning the uptime/online status of the specified channel using decapi.
 */
public class UptimeReader {

    public static String getUptimeString(String username) {

        try {
            return readStringFromURL("https://decapi.me/twitch/uptime?channel=" + username);
        } catch (Exception e) {
            Main.logger.error(e);
        }

        return "Could not get uptime";

    }

    public static String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8.toString())) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}