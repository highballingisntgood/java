package cryptology;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Hashing {
    public static byte[] newHashMD5(Path path) {
        try {
            byte[] unsecured = Files.readAllBytes(path);
            MessageDigest md = MessageDigest.getInstance("MD5");
            return md.digest(unsecured);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] newHashSHA1(Path path) {
        try {
            byte[] unsecured = Files.readAllBytes(path);
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(unsecured);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] newHashSHA2Salt(Path path, String salt) {
        try {
            byte[] unsecured = Files.readAllBytes(path);
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            md.update(salt.getBytes());
            return md.digest(unsecured);
        } catch (NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}