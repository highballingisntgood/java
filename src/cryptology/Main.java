package cryptology;

import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Arrays;
import java.util.Base64;

public class Main {
    public static void main(String[] args) throws IOException {
        Path path = Path.of("test.txt");

        //XorEncryption
        byte encryption_key = (byte) 0x65;

        System.out.println("--------------XOR Encryption--------------");
        System.out.println("Raw:       " + Base64.getEncoder().encodeToString(Files.readAllBytes(path)));
        System.out.println("Encrypted: " + Base64.getEncoder().encodeToString(XorEncryption.encryptByXOR(path, encryption_key)));
        System.out.println("Decrypted: " + Base64.getEncoder().encodeToString(XorEncryption.decryptByXOR(XorEncryption.encryptByXOR(path, encryption_key), encryption_key)));
        System.out.println();
        System.out.println();

        //Hashing
        String salt1 = "Encryption!";
        String salt2 = "Decryption!";

        System.out.println("--------------Hashing--------------");
        System.out.println("Raw:          " + Files.readString(path));
        System.out.println("MD5:          " + Base64.getEncoder().encodeToString(Hashing.newHashMD5(path)));
        System.out.println("SHA1:         " + Base64.getEncoder().encodeToString(Hashing.newHashSHA1(path)));
        System.out.println("SHA2 (salt1): " + Base64.getEncoder().encodeToString(Hashing.newHashSHA2Salt(path, salt1)));
        System.out.println("SHA2 (salt2): " + Base64.getEncoder().encodeToString(Hashing.newHashSHA2Salt(path, salt2)));
        System.out.println();
        System.out.println();

        //AsymmetricEncryption
        byte[] privateKeyBytes = Files.readAllBytes(Path.of("private-key.pem"));
        PrivateKey privateKey = AsymmetricEncryption.getPrivateKey(privateKeyBytes);
        PublicKey publicKey = AsymmetricEncryption.getPublicKey(AsymmetricEncryption.getPrivateKey(privateKeyBytes));

        System.out.println("--------------Asymmetric Encryption--------------");
        System.out.println("Private key:");
        System.out.println(Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(privateKey.getEncoded()));
        System.out.println();
        System.out.println("Public key:");
        System.out.println(Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(publicKey.getEncoded()));
        System.out.println();
        System.out.println("Raw:");
        System.out.println(Files.readString(path));
        System.out.println();
        System.out.println("Encrypted:");
        System.out.println(AsymmetricEncryption.encrypt(path, publicKey));
        System.out.println();
        System.out.println("Decrypted:");
        System.out.println(AsymmetricEncryption.decrypt(path, privateKey));
        System.out.println();
        System.out.println();

        //SymmetricEncryption
        SecretKey secretKey = SymmetricEncryption.generateKey();
        IvParameterSpec ivParameterSpec = SymmetricEncryption.generateIV();

        System.out.println("--------------Symmetric Ciphering--------------");
        System.out.println("Secret Key:");
        System.out.println(Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(secretKey.getEncoded()));
        System.out.println();
        System.out.println("IV Parameter:");
        System.out.println(Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(ivParameterSpec.getIV()));
        System.out.println();
        System.out.println("Raw:");
        System.out.println(Files.readString(path));
        System.out.println();
        System.out.println("Encrypted:");
        System.out.println(SymmetricEncryption.encrypt(path, secretKey, ivParameterSpec));
        System.out.println();
        System.out.println("Decrypted:");
        System.out.println(SymmetricEncryption.decrypt(path, secretKey, ivParameterSpec));
        System.out.println();
        System.out.println();

        //Encoding
        byte[] bytes = {65, 104, 111, 106, 33};

        System.out.println("--------------Encoding--------------");
        System.out.println("Bytes:   " + Arrays.toString(bytes));
        System.out.println("Encoded: " + Encoding.toText(bytes));
    }
}