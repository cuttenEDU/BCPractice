package blockchain;

import com.google.common.hash.HashCode;

import javax.xml.bind.DatatypeConverter;
import java.security.*;

public class Transaction {
    public enum Type {
        Parcel, Track
    }


    private long timestamp;
    private HashCode hashCode;
    private PublicKey senderPublicKey;
    private PublicKey recieverPublicKey;
    private String parcelTN;
    private byte[] signature;
    private String data;
    private Type type;

    public Transaction(String parcelTN, PublicKey senderPublicKey, PublicKey recieverPublicKey, String data, long timestamp, byte[] signature) {
        this.parcelTN = parcelTN;
        this.senderPublicKey = senderPublicKey;
        this.recieverPublicKey = recieverPublicKey;
        this.data = data;
        this.timestamp = timestamp;
        this.hashCode = Hasher.hash(senderPublicKey + parcelTN + data);
        this.signature = signature;
    }

    private String truncate(String string) {
        return string.substring(0, 5) + "..." + string.substring(string.length() - 6, string.length() - 1);
    }

    boolean isValid() throws InvalidKeyException, SignatureException, NoSuchAlgorithmException {
        Signature ecdsaVerify = Signature.getInstance("SHA256withECDSA");
        ecdsaVerify.initVerify(senderPublicKey);
        ecdsaVerify.update(parcelTN.getBytes());
        ecdsaVerify.update(data.getBytes());
        return ecdsaVerify.verify(signature);
    }

    public String getParcelTN() { return parcelTN; }

    public HashCode getHashCode() { return hashCode; }

    public String getData() {
        return data;
    }

    public String getInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return "Hash: " + hashCode.toString() + "\tPubkey: " + Hasher.bytesToHex(senderPublicKey.getEncoded()) + "\tParcel TN: " + parcelTN + "\tData: " + data + "\tTimestamp: " + timestamp + "\tSig: " + DatatypeConverter.printHexBinary(signature) + "\tisValid: " + String.valueOf(isValid());
    }

    public String getTruncInfo() throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
        return "Hash: " + truncate(hashCode.toString()) + "\tPubkey: " + truncate(Hasher.bytesToHex(senderPublicKey.getEncoded())) + "\tParcel TN: " + parcelTN + "\tData: " + data + "\tTimestamp: " + timestamp + "\tSig: " + truncate(DatatypeConverter.printHexBinary(signature)) + "\tisValid: " + String.valueOf(isValid());

    }

}
