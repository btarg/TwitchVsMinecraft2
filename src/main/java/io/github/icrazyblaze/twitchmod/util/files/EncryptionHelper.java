package io.github.icrazyblaze.twitchmod.util.files;

import io.github.icrazyblaze.twitchmod.Main;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

public class EncryptionHelper {

    private static Cipher cipher = null;

    static {
        try {
            cipher = Cipher.getInstance("RC4");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(String toEncrypt, String key) {
        try {

            // Create key and cipher
            Key rc4Key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "RC4");
            // encrypt the text
            cipher.init(Cipher.ENCRYPT_MODE, rc4Key);
            byte[] encrypted = cipher.doFinal(toEncrypt.getBytes(StandardCharsets.UTF_8));

            return encrypted;

        } catch (Exception e) {
            Main.logger.error("Could not encrypt string: " + e);
            return toEncrypt.getBytes(StandardCharsets.UTF_8);
        }

    }

    public static byte[] decrypt(byte[] encrypted, String key) {
        try {

            Key rc4Key = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "RC4");
            cipher.init(Cipher.DECRYPT_MODE, rc4Key);

            return cipher.doFinal(encrypted);

        } catch (Exception e) {
            Main.logger.error("Could not decrypt string: " + e);
            return encrypted;
        }

    }

}
