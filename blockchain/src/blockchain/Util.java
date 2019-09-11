package blockchain;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;

import java.security.*;
import java.security.spec.EncodedKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class Util {

    public static String hash(String string) {
        com.google.common.hash.Hasher hasher = Hashing.sha256().newHasher();
        hasher.putString(string, Charsets.UTF_8);
        return hasher.hash().toString();
    }


    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static String k2str(Key key) {
        byte[] bytes = key.getEncoded();
        return bytes2hex(bytes);


    }

    public static String bytes2hex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);


    }


    public static PublicKey stringToPubKey(String string) throws InvalidKeySpecException, NoSuchAlgorithmException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(hex2bytes(string,false));
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
        return publicKey;
    }

    public static PrivateKey stringToPrvKey(String string) throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(hex2bytes(string,false));
        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
        return privateKey;
    }

    public static byte[] hex2bytes(String hex, boolean skipZeros) {
        if ((hex.length() & 1) == 1) {
            hex = "0" + hex;
        }
        while (hex.startsWith("00") && skipZeros) {
            hex = hex.substring(2);
        }
        byte[] bytes = new byte[hex.length() / 2];
        String str = "0123456789abcdef";
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) ((str.indexOf(hex.charAt(i * 2)) << 4) | str.indexOf(hex.charAt(i * 2 + 1)));
        }
        return bytes;//new BigInteger(hex, 16).toByteArray();
    }

}
