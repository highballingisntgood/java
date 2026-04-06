package cryptology;

public class Encoding {
    public static String toText(byte[] bytes) {
        StringBuilder string = new StringBuilder();
        for (byte aByte : bytes) {
            string.append((char) aByte);
        }
        return string.toString();
    }
}