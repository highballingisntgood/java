package server;

import java.io.*;
import java.net.*;
import javax.crypto.*;
import java.security.*;
import java.util.Base64;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private static SecretKey aesKey;

    public static void main(String[] args) {
        try {
            KeyPair rsaKeyPair = generateRSAKeyPair();
            System.out.println("Your Public Key (Share this): " +
                    Base64.getEncoder().encodeToString(rsaKeyPair.getPublic().getEncoded()));

            Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader userInputReader = new BufferedReader(new InputStreamReader(System.in));

            new Thread(() -> {
                try {
                    String fromServer;
                    while ((fromServer = in.readLine()) != null) {
                        handleIncomingMessage(fromServer);
                    }
                } catch (IOException e) {
                    System.out.println("Connection closed.");
                }
            }).start();

            System.out.println("Format: <IP>:<MESSAGE> or <IP>:KEY:<ENCRYPTED_AES_KEY>");
            while (true) {
                String input = userInputReader.readLine();
                if ("exit".equalsIgnoreCase(input)) break;

                if (input.contains(":")) {
                    String[] parts = input.split(":", 2);
                    String targetIP = parts[0];
                    String rawMessage = parts[1];

                    if (aesKey == null) aesKey = generateAESKey();

                    String encryptedMsg = encryptAES(rawMessage, aesKey);
                    out.println(targetIP + ":" + encryptedMsg);
                }
            }

            socket.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void handleIncomingMessage(String message) {
        try {
            System.out.println("\n[Raw Received]: " + message);

            if (aesKey != null) {
                String decrypted = decryptAES(message, aesKey);
                System.out.println("[Decrypted]: " + decrypted);
            } else {
                System.out.println("Cannot decrypt: AES key not set yet.");
            }
        } catch (Exception e) {
            System.out.println("Error decrypting: " + e.getMessage());
        }
    }

    // --- CRYPTO UTILITIES ---

    private static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        return kpg.generateKeyPair();
    }

    private static SecretKey generateAESKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        return keyGen.generateKey();
    }

    private static String encryptAES(String data, SecretKey key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return Base64.getEncoder().encodeToString(cipher.doFinal(data.getBytes()));
    }

    private static String decryptAES(String encryptedData, SecretKey key) throws GeneralSecurityException {
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(encryptedData);
        return new String(cipher.doFinal(decoded));
    }
}