package io.github.icrazyblaze.twitchmod.util;

import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.function.Supplier;

/**
 * SUCK = Serialised Unreadable Connection Key
 * <br>
 * This class is responsible for reading from and writing to base64-encoded serialised files that contain keys.
 * This new system allows for the keys to be hidden, without requiring the keys to be entered every time the game starts.
 * No real encryption is done in this class: this system is entirely designed for idiot-proofing and preventing the keys from being leaked!
 *
 * @see io.github.icrazyblaze.twitchmod.config.BotConfig
 * @since 3.5.0
 */
public class SecretFileHelper implements Serializable {

    private static final Supplier<Path> path_twitch = () -> FMLPaths.CONFIGDIR.get().resolve("twitch_key.suck");
    private static final Supplier<Path> path_discord = () -> FMLPaths.CONFIGDIR.get().resolve("discord_token.suck");


    public static void setValuesFromFiles() {

        try {
            BotConfig.TWITCH_KEY = getStringFromFile(path_twitch);
        } catch (Exception e) {
            Main.logger.error("No Twitch credentials found");
        }

        try {
            BotConfig.DISCORD_TOKEN = getStringFromFile(path_discord);
        } catch (Exception e) {
            Main.logger.info("No Discord credentials found");
        }

    }

    private static void writeToFile(String toWrite, Supplier<Path> path) throws IOException {

        FileOutputStream f_out = new FileOutputStream(path.get().toFile());
        ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

        // Encode into base64
        String encoded = Base64.getEncoder().encodeToString(toWrite.getBytes());
        // Now with reversing!
        String reversed = new StringBuilder(encoded).reverse().toString();

        // Write to file
        SerializationUtils.serialize(reversed, obj_out);

    }

    public static void setTwitchKey(String toWrite) {
        try {
            writeToFile(toWrite, path_twitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setValuesFromFiles();
    }

    public static void setDiscordToken(String toWrite) {
        try {
            writeToFile(toWrite, path_discord);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setValuesFromFiles();
    }

    private static String getStringFromFile(Supplier<Path> path) throws IOException {

        try (ObjectInputStream obj_in = new ObjectInputStream(Files.newInputStream(path.get()))) {

            // Read string from file
            String encoded = SerializationUtils.deserialize(obj_in);
            String reversed = new StringBuilder(encoded).reverse().toString();

            // Decode and convert bytes to string
            byte[] decoded = Base64.getDecoder().decode(reversed);
            return new String(decoded);

        }

    }


}
