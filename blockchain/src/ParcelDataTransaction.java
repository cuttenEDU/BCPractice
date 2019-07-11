import com.google.common.hash.HashCode;

import javax.xml.bind.DatatypeConverter;
import java.security.*;
import java.sql.Time;

public class ParcelDataTransaction implements Transaction {
    private String parcelTN;
    private PublicKey senderPublicKey;
    private byte[] signature;
    private String data;
    private long timestamp;
    private HashCode hashCode;

    public ParcelDataTransaction(String parcelTN, PublicKey senderPublicKey, String data, long timestamp, byte[] signature) {
        this.parcelTN = parcelTN;
        this.senderPublicKey = senderPublicKey;
        this.data = data;
        this.timestamp = timestamp;
        this.hashCode = Hasher.hash(senderPublicKey + parcelTN + data);
        this.signature = signature;
    }


    public String getParcelTN() {
        return parcelTN;
    }

    public HashCode getHashCode() {
        return hashCode;
    }

    String getInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return "Hash: " + hashCode.toString() + "\tPubkey: " + Hasher.bytesToHex(senderPublicKey.getEncoded()) + "\tParcel TN: " + parcelTN + "\tData: " + data + "\tTimestamp: " + timestamp + "\tSig: " + DatatypeConverter.printHexBinary(signature) + "\tisValid: " + String.valueOf(isValid());
    }

    String getTruncInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return "Hash: " + truncate(hashCode.toString()) + "\tPubkey: " + truncate(Hasher.bytesToHex(senderPublicKey.getEncoded())) + "\tParcel TN: " + parcelTN + "\tData: " + data + "\tTimestamp: " + timestamp + "\tSig: " + truncate(DatatypeConverter.printHexBinary(signature)) + "\tisValid: " + String.valueOf(isValid());

    }

    public String getData() {
        return data;
    }

    String truncate(String string) {
        return string.substring(0, 5) + "..." + string.substring(string.length() - 6, string.length() - 1);
    }


    boolean isValid() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify.initVerify(senderPublicKey);
        ecdsaVerify.update(parcelTN.getBytes());
        ecdsaVerify.update(data.getBytes());
        return ecdsaVerify.verify(signature);
    }
}



