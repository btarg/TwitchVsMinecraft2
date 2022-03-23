package io.github.icrazyblaze.twitchmod.util.files;

import io.github.icrazyblaze.twitchmod.Main;
import io.github.icrazyblaze.twitchmod.config.BotConfig;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.SerializationUtils;

import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.function.Supplier;

/**
 * SUCK = Serialised Unreadable Connection Key
 * <br>
 * This class is responsible for serialising and deserializing encrypted password strings.
 * This system allows for keys to be stored securely, without requiring them to be entered every time the game starts.
 * The key strings are Base64 encoded, then RC4 encrypted using an MD5 (hex) of the hardware address.
 *
 * @see io.github.icrazyblaze.twitchmod.config.BotConfig
 * @see EncryptionHelper
 * @since 3.5.0
 */
public class SecretFileHelper implements Serializable {

    private static final Supplier<Path> path_twitch = () -> FMLPaths.CONFIGDIR.get().resolve("twitch_key.suck");
    private static final Supplier<Path> path_discord = () -> FMLPaths.CONFIGDIR.get().resolve("discord_token.suck");
    private static String hardwareAddress = null;


    static {
        try {
            // Get MAC address
            byte[] bytes = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
            // MD5 hash the MAC address
            byte[] md5 = MessageDigest.getInstance("md5").digest(bytes);
            hardwareAddress = Hex.encodeHexString(md5);

        } catch (Exception e) {
            Main.logger.error(e);
        }
    }

    public static void setValuesFromFiles() {

        try {
            BotConfig.TWITCH_KEY = getStringFromFile(path_twitch);
        } catch (Exception e) {
            Main.logger.error(new TranslatableComponent("exception.twitchmod.login_not_found_twitch", e).getString());
        }

        try {
            BotConfig.DISCORD_TOKEN = getStringFromFile(path_discord);
        } catch (Exception e) {
            Main.logger.error(new TranslatableComponent("exception.twitchmod.login_not_found_discord", e).getString());
        }

    }

    private static void writeToFile(String toWrite, Supplier<Path> path) throws IOException {

        FileOutputStream f_out = new FileOutputStream(path.get().toFile());
        ObjectOutputStream obj_out = new ObjectOutputStream(f_out);

        // Encrypt string
        byte[] encrypted = EncryptionHelper.encrypt(toWrite, hardwareAddress);
        String encoded = Base64.getEncoder().encodeToString(encrypted);

        // Write to file
        SerializationUtils.serialize(encoded, obj_out);

    }

    public static void writeTwitchKey(String toWrite) {
        try {
            writeToFile(toWrite, path_twitch);
        } catch (Exception e) {
            Main.logger.error(e);
            return;
        }
        BotConfig.TWITCH_KEY = toWrite;
    }

    public static void writeDiscordToken(String toWrite) {
        try {
            writeToFile(toWrite, path_discord);
        } catch (Exception e) {
            Main.logger.error(e);
            return;
        }
        BotConfig.DISCORD_TOKEN = toWrite;
    }

    private static String getStringFromFile(Supplier<Path> path) throws IOException {

        try (ObjectInputStream obj_in = new ObjectInputStream(Files.newInputStream(path.get()))) {

            // Read string from file
            String encoded = SerializationUtils.deserialize(obj_in);
            byte[] decoded = Base64.getDecoder().decode(encoded);
            byte[] decrypted = EncryptionHelper.decrypt(decoded, hardwareAddress);
            return new String(decrypted, StandardCharsets.UTF_8);

        }

    }

}
