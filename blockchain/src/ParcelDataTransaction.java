import com.google.common.hash.HashCode;

import javax.xml.bind.DatatypeConverter;
import java.security.PublicKey;
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

    String getInfo() {
        return "Hashcode: " + hashCode.toString() + "\tPublic key: " + Hasher.bytesToHex(senderPublicKey.getEncoded()) + "\tParcel TN: " + parcelTN + "\tData: " + data + "\tTimestamp: " + timestamp + "\tSignature: " + DatatypeConverter.printHexBinary(signature);
    }

    String getTruncInfo() {
        return "Hashcode: " + truncate(hashCode.toString()) + "\tPublic key: " + truncate(Hasher.bytesToHex(senderPublicKey.getEncoded())) + "\tParcel TN: " + parcelTN + "\tData: " + data + "\tTimestamp: " + timestamp + "\tSignature: " + truncate(DatatypeConverter.printHexBinary(signature));

    }

    public String getData() {
        return data;
    }

    String truncate(String string) {
        return string.substring(0, 6) + "..." + string.substring(string.length() - 7, string.length() - 1);
    }


}



