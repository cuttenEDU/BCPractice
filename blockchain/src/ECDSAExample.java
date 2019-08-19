import javax.xml.bind.DatatypeConverter;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class ECDSAExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        /*
         * Generate an ECDSA signature
         */

        /*
         * Generate a key pair
         */




        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
        byte[] seed = new byte[]{56, 89, 34, 77, 12, 90};
        keyGen.initialize(new ECGenParameterSpec("secp256r1"), new SecureRandom(seed));
        KeyPair pair = keyGen.generateKeyPair();
        PrivateKey priv = pair.getPrivate();
        PublicKey pub = pair.getPublic();
        /*
         * Create a Signature object and initialize it with the private key
         */

        Signature ecdsa = Signature.getInstance("SHA256withECDSA");
        ecdsa.initSign(priv);
        String str = "This is string to sign";
        byte[] strByte = str.getBytes(StandardCharsets.UTF_8);
        ecdsa.update(strByte);


        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(strByte);
        byte[] shaBytes = digest.digest();

        byte[] realSig = ecdsa.sign();
        System.out.println("Signature: " + DatatypeConverter.printHexBinary(realSig));
        System.out.println(realSig.length);
        System.out.println((priv.getEncoded().length));
        System.out.println((pub.getEncoded().length));
        System.out.println(bytesToHex(priv.getEncoded()));



        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify.initVerify(pub);
        ecdsaVerify.update(strByte);
        boolean result = ecdsaVerify.verify(realSig);
        System.out.println(result);

        System.out.println(Arrays.toString(realSig));
        System.out.println(Arrays.toString(hexStringToByteArray(DatatypeConverter.printHexBinary(realSig))));
        System.out.println(Arrays.toString(Security.getProviders()));


        KeyPair keypair = keyGen.genKeyPair();
        PrivateKey privateKey = keypair.getPrivate();
        PublicKey publicKey = keypair.getPublic();

        byte[] privateKeyBytes = privateKey.getEncoded();
        byte[] publicKeyBytes = publicKey.getEncoded();

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        PrivateKey privateKey2 = keyFactory.generatePrivate(privateKeySpec);

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey2 = keyFactory.generatePublic(publicKeySpec);

        // КЕК
        System.out.println(privateKey.equals(privateKey2));
        System.out.println(publicKey.equals(publicKey2));




        


    }
    private static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}