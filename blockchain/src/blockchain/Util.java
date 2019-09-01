package blockchain;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class Util {

    static HashCode hash(String string) {
        com.google.common.hash.Hasher hasher = Hashing.sha256().newHasher();
        hasher.putString(string, Charsets.UTF_8);
        return hasher.hash();
    }


    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static String keyToString(Key key) {
        byte[] bytes = key.getEncoded();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);


    }

    public static Key stringToPubKey(String string) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(string.getBytes());
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static Key stringToPrvKey(String string) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        EncodedKeySpec privateKeySpec = new X509EncodedKeySpec(string.getBytes());
        return keyFactory.generatePrivate(privateKeySpec);
    }
}
