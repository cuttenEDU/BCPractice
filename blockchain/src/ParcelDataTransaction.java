import com.google.common.hash.HashCode;

import java.sql.Time;

public class ParcelDataTransaction implements Transaction {
    private String parcelTN;
    private String senderPublicKey;
    private String data;
    private long timestamp;
    private HashCode hashCode;

    public ParcelDataTransaction(String parcelTN, String senderPublicKey, String data, long timestamp) {
        this.parcelTN = parcelTN;
        this.senderPublicKey = senderPublicKey;
        this.data = data;
        this.timestamp = timestamp;
        this.hashCode = Hasher.hash(senderPublicKey+parcelTN+data);
    }


    public String getParcelTN() {
        return parcelTN;
    }

    String getInfo(){
        return "Hashcode: " + hashCode.toString() + "\tPublic key: " + senderPublicKey  + "\tParcel TN: " + parcelTN + "\tData: " + data + "\tTimestamp: " + timestamp;
    }


}
