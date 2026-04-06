package cryptology;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.*;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;

public class AsymmetricEncryption {
    public static PrivateKey getPrivateKey(byte[] privateKeyBytes) {
        try {
            String privateKeyString = new String(privateKeyBytes);
            privateKeyString = privateKeyString.replace("-----BEGIN PRIVATE KEY-----\r\n", "").replace("-----END PRIVATE KEY-----", "").replaceAll("\\s", "");
            byte[] derBytes = Base64.getDecoder().decode(privateKeyString);
            PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(derBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(privateKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static PublicKey getPublicKey(PrivateKey privateKey) {
        try {
            RSAPrivateCrtKey privateKeyRSA = (RSAPrivateCrtKey) privateKey;
            RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(privateKeyRSA.getModulus(), privateKeyRSA.getPublicExponent());
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }

    public static String encrypt(Path path, PublicKey publicKey) {
        try {
            byte[] data = Files.readAllBytes(path);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] encrypted = cipher.doFinal(data);
            String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encrypted);
            Files.writeString(path, base64);
            return base64;
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String decrypt(Path path, PrivateKey privateKey) {
        try {
            String base64 = Files.readString(path);
            byte[] encrypted = Base64.getMimeDecoder().decode(base64);
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] decrypted = cipher.doFinal(encrypted);
            Files.write(path, decrypted);
            return new String(decrypted);
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                 IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException(e);
        }
    }
}