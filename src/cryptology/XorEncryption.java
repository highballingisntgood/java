package cryptology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class XorEncryption {
    public static byte[] encryptByXOR(Path path, byte encryption_key) {
        try {
            byte[] data = Files.readAllBytes(path);
            byte[] encrypted = new byte[data.length];
            for (int i = 0; i < data.length; i++) {
                encrypted[i] = (byte) (data[i]^encryption_key);
            }
            return encrypted;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] decryptByXOR(byte[] encrypted, byte encryption_key) {
        byte[] decrypted = new byte[encrypted.length];
        for (int i = 0; i < encrypted.length; i++) {
            decrypted[i] = (byte) (encrypted[i]^encryption_key);
        }
        return decrypted;
    }
}